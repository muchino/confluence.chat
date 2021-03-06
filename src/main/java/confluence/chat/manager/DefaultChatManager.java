package confluence.chat.manager;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.thoughtworks.xstream.XStream;
import confluence.chat.Version;
import confluence.chat.conditions.ChatUseCondition;
import confluence.chat.config.ChatConfiguration;
import confluence.chat.config.ChatSpaceConfiguration;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatMessage;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatPreferences;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatUserList;
import confluence.chat.utils.ChatUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;

public final class DefaultChatManager implements ChatManager {

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	private final BandanaManager bandanaManager;
	private final UserAccessor userAccessor;
	private final ChatUserList users = new ChatUserList();
	private final Map<String, ChatBoxMap> chatBoxes = new ConcurrentHashMap<>();
	private final Map<String, ChatSpaceConfiguration> configurationSpace = new ConcurrentHashMap<>();
	private final TransactionTemplate transactionTemplate;
	private final GroupManager groupManager;
	private ChatUseCondition chatUseCondition;
	private ChatConfiguration chatConfiguration;
//    private ChatVersionTransformer chatVersionTransformer;

	public DefaultChatManager(final BandanaManager bandanaManager,
			final UserAccessor userAccessor,
			final TransactionTemplate transactionTemplate,
			final GroupManager groupManager) {
		this.bandanaManager = bandanaManager;
		this.userAccessor = userAccessor;
		this.transactionTemplate = transactionTemplate;
		this.groupManager = groupManager;
		this.logger.debug("Init ChatManager in version " + getVersion());
	}

	@Override
	public ChatBoxMap getChatBoxes(User user) {
		return getChatBoxes(user.getName());
	}

	@Override
	public ChatBoxMap getNewMessageChatBoxes(User user) {
		return getChatBoxes(user.getName());
	}

	/**
	 * Returns all chatboxes of an user
	 *
	 * @param userKey
	 * @return
	 */
	private ChatBoxMap getChatBoxes(final String username) {

		String userKey = ChatUtils.getCorrectUserKey(username);
		if (!chatBoxes.containsKey(userKey)) {

			logger.debug(" no boxes for " + userKey);

			ChatBoxMap chatBoxMapOfUser = getChatBoxMapOfUserFromBandana(userKey);

			/**
			 * check if old chatboxes are there (pre 5.3) and convert them if
			 * needed
			 */
			if (chatBoxMapOfUser.isEmpty()
					&& UserCompatibilityHelper.isRenameUserImplemented()
					&& !username.equals(userKey)) {
				ChatBoxMap oldChatBoxeMapOfUser = getChatBoxMapOfUserFromBandana(username);
				chatBoxMapOfUser = this.transformToUserKeyChatBoxMap(username, oldChatBoxeMapOfUser);

			}

			chatBoxMapOfUser.removeInvalidChatBoxes(userAccessor);
			chatBoxes.put(userKey, chatBoxMapOfUser);
		}

		return chatBoxes.get(userKey);
	}

	private ChatBoxMap getChatBoxMapOfUserFromBandana(final String username) {
		return (ChatBoxMap) transactionTemplate.execute(new TransactionCallback() {
			@Override
			public ChatBoxMap doInTransaction() {
				ChatBoxMap chatBoxMap = new ChatBoxMap();
				ConfluenceBandanaContext confluenceBandanaContext = getConfluenceBandanaContextHistory(username);
				try {

					XStream xStream = new XStream();
					// Set the classloader to this class's class loader, which should be the plugin classloader
					xStream.setClassLoader(ChatBox.class.getClassLoader());
					xStream.alias("ChatBox", ChatBox.class);
					try {
						Iterator<String> iterator = bandanaManager.getKeys(confluenceBandanaContext).iterator();
						while (iterator.hasNext()) {
							String chatBoxId = iterator.next();
							String userName = ChatUtils.getUserNameByKeyOrUserName(chatBoxId);
							if (userAccessor.getUser(userName) != null) {
								try {
									ChatBox chatBox = (ChatBox) bandanaManager.getValue(confluenceBandanaContext, chatBoxId);
									if (chatBox == null) {
										bandanaManager.removeValue(confluenceBandanaContext, chatBoxId);

									} else {
										chatBoxMap.put(chatBoxId, chatBox);
									}
								} catch (Exception e) {
									logger.warn(" error bandanaManager.getValue for chat box  " + chatBoxId + " for user: " + username);
								}

							} else {
								bandanaManager.removeValue(confluenceBandanaContext, chatBoxId);
							}
						}
					} catch (Exception e) {
						logger.warn(" error getChatBoxes " + username, e);
					}
				} catch (Exception e) {
					logger.warn(" error getChatBoxes " + username, e);
				}
				/**
				 * while updateing plugin -> an error could occour
				 */
				return chatBoxMap;
			}
		});
	}

	private ChatPreferences getPreferencesOfUser(String username) {
		String userKey = ChatUtils.getCorrectUserKey(username);
		ChatPreferences preferences = null;
		try {
			preferences = (ChatPreferences) bandanaManager.getValue(CONTEXT_USER_PREFERENCES, userKey);
		} catch (Exception e) {
		}
		if (preferences == null) {
			preferences = new ChatPreferences();
		}
		return preferences;
	}

	@Override
	public void setPreferencesOfUser(String username, ChatPreferences preferences) {
		String userKey = ChatUtils.getCorrectUserKey(username);
		bandanaManager.setValue(CONTEXT_USER_PREFERENCES, userKey, preferences);
	}

	@Override
	public void sendMessage(final String sender, final String receiver, final String message) {

		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Boolean doInTransaction() {
				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setFrom(sender);
				chatMessage.setTo(receiver);
				chatMessage.setMessage(message);

//        chatboxes of receiver
				ChatBox chatBoxWithReceiver = getChatBoxes(receiver).getChatBoxWithUser(sender);
				chatBoxWithReceiver.addMessage(chatMessage);
				chatBoxWithReceiver.open();
				saveChatBox(ChatUtils.getCorrectUserKey(receiver), chatBoxWithReceiver);
				//        own chatboxes
				ChatBox chatBoxSelf = getChatBoxes(sender).getChatBoxWithUser(receiver);
				chatBoxSelf.addMessage(chatMessage);
				chatBoxSelf.open();
				saveChatBox(ChatUtils.getCorrectUserKey(sender), chatBoxSelf);
				return true;
			}
		});
	}

	@Override
	public void closeChatBox(final User user, final String chatBoxId) {
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Boolean doInTransaction() {
				ChatBox chatBoxById = getChatBoxes(user).getChatBoxByStringId(chatBoxId);
				chatBoxById.close();
				saveChatBox(ChatUtils.getCorrectUserKey(user.getName()), chatBoxById);
				return true;
			}
		});
	}

	@Override
	public void deleteChatBox(final User user, final String chatBoxId) {
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Boolean doInTransaction() {
				getChatBoxes(user).remove(chatBoxId);
				ConfluenceBandanaContext confluenceBandanaContext
						= getConfluenceBandanaContextHistory(ChatUtils.getCorrectUserKey(user.getName()));
				bandanaManager.removeValue(confluenceBandanaContext, chatBoxId);
				return true;
			}
		});
	}

	/**
	 * Die Funkttion wird auch beim transform genutzt - daher muss der userkey
	 * gegeben werden
	 */
	private void saveChatBox(String userKey, ChatBox chatBox) {
		bandanaManager.setValue(getConfluenceBandanaContextHistory(userKey), chatBox.getId().toString(), chatBox);
	}

	@Override
	public List<ChatUser> getOnlineUsers(String spaceKey) {
		List<ChatUser> onlineUserList = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -ChatManager.SECONDS_TO_BE_OFFLINE);
		Date time = cal.getTime();
		for (Map.Entry<String, ChatUser> user : users.entrySet()) {
			ChatUser chatUser = user.getValue();
			if (!getChatConfiguration().getShowWhereIam()) {
				chatUser.removeCurrentSite();
			}
			if (!ChatStatus.OFFLINE.equals(chatUser.getStatus())
					&& time.before(chatUser.getLastSeen())) {
				if (StringUtils.isEmpty(spaceKey)) {
					onlineUserList.add(chatUser);
				} else if (this.hasChatAccess(userAccessor.getUser(chatUser.getUsername()), spaceKey)) {
					onlineUserList.add(chatUser);
				}
			}
		}
		return onlineUserList;
	}

	@Override
	public void setOnlineStatus(String user, ChatStatus status) {
		this.setOnlineStatus(userAccessor.getUser(user), status);
	}

	@Override
	public ChatUser getChatUser(User user) {
		ChatUser chatUser = null;
		if (user != null) {
			if (!this.users.containsKey(user.getName())) {
				chatUser = this.users.putUser(user, getPreferencesOfUser(user.getName()));
				this.setProfilPicture(user, chatUser);
			} else {
				chatUser = this.users.get(user.getName());
			}
			if (!getChatConfiguration().getShowWhereIam()) {
				chatUser.removeCurrentSite();
			}
		}
		return chatUser;

	}

	@Override
	public ChatUser getChatUser(String username) {
		User user = userAccessor.getUser(username);
		return getChatUser(user);
	}

	private void setProfilPicture(User user, ChatUser chatUser) {
		if (chatUser.getUserImage() == null) {

			ProfilePictureInfo picture = userAccessor.getUserProfilePicture(user);
			String fileName;
			if (!picture.isDefault()) {
				String downloadPath = picture.getDownloadPath();
				fileName = picture.getFileName();
				if (downloadPath.trim() == null ? fileName.trim() != null : !downloadPath.trim().equals(fileName.trim())) {
					if (downloadPath.endsWith(fileName)) {
						fileName = downloadPath;
					} else {
						fileName = downloadPath + fileName;
					}

				}
			} else {
				fileName = ProfilePictureInfo.DEFAULT_PROFILE_PATH;

			}
			chatUser.setUserImage(fileName);
		}
	}

	@Override
	public void setOnlineStatus(User user, final ChatStatus status) {
		final ChatUser chatUser = getChatUser(user);
		if (chatUser != null && status != ChatStatus.NO_CHANGE && status != null) {
			// change status
			if (chatUser.getStatus() == status) {
				chatUser.setStatus(status);
			} else {
				transactionTemplate.execute(new TransactionCallback() {
					@Override
					public String doInTransaction() {
						chatUser.getPreferences().setChatStatus(status);
						setPreferencesOfUser(chatUser.getUsername(), chatUser.getPreferences());
						return "";
					}
				});
				chatUser.setStatus(chatUser.getPreferences().getChatStatus());
			}
			chatUser.setLastSeen(new Date());
		}
	}

	private ConfluenceBandanaContext getConfluenceBandanaContextHistory(String usernameOrKey) {
		return new ConfluenceBandanaContext(KEY_HISTORY + usernameOrKey);
	}

	@Override
	public ChatConfiguration getChatConfiguration() {
		if (chatConfiguration == null) {
			XStream xStream = new XStream();
			xStream.setClassLoader(ChatConfiguration.class.getClassLoader());
			xStream.alias("ChatConfiguration", ChatConfiguration.class);
			try {
				chatConfiguration = (ChatConfiguration) bandanaManager.getValue(CONTEXT_CHAT_SETTINGS, KEY_GLOBAL_CONFIGURATION);
			} catch (Exception e) {
				logger.warn(" error reading chat configuration");
			}
			if (chatConfiguration == null) {
				chatConfiguration = new ChatConfiguration();
			}
		}
		return chatConfiguration;
	}

	@Override
	public void setChatConfiguration(ChatConfiguration config) {
		if (config != null) {
			bandanaManager.setValue(CONTEXT_CHAT_SETTINGS, KEY_GLOBAL_CONFIGURATION, config);
			chatConfiguration = config;
			chatUseCondition = null;
		}
	}

	@Override
	public ChatSpaceConfiguration getChatSpaceConfiguration(String spaceKey) {
		if (!this.configurationSpace.containsKey(spaceKey)) {
			XStream xStream = new XStream();
			xStream.setClassLoader(ChatSpaceConfiguration.class.getClassLoader());
			ChatSpaceConfiguration config = null;
			try {
				config = (ChatSpaceConfiguration) bandanaManager.getValue(CONTEXT_CHAT_SETTINGS, KEY_GLOBAL_CONFIGURATION + "." + spaceKey);
			} catch (Exception e) {
				logger.warn(" error reading chat ChatSpaceConfiguration");
			}
			if (config == null) {
				config = new ChatSpaceConfiguration();
			}
			this.configurationSpace.put(spaceKey, config);
		}
		return this.configurationSpace.get(spaceKey);
	}

	@Override
	public void setChatSpaceConfiguration(ChatSpaceConfiguration config, String spaceKey) {
		if (config != null) {
			bandanaManager.setValue(CONTEXT_CHAT_SETTINGS, KEY_GLOBAL_CONFIGURATION + "." + spaceKey, config);
			this.configurationSpace.put(spaceKey, config);
			chatUseCondition = null;
		}
	}

	@Override
	public Boolean hasChatAccess(User user, String spaceKey) {
		if (chatUseCondition == null) {
			chatUseCondition = new ChatUseCondition(this, groupManager);
		}
		return chatUseCondition.hasAccess(user, spaceKey);
	}

	@Override
	public void manageHistory(ChatBox chatBox, User owner) {
		if (owner != null && chatBox != null) {
			ChatMessageList messagesBefore = chatBox.getMessagesBefore(ChatUtils.getYesterday());
			if (!messagesBefore.isEmpty()) {
//                chatBox.getMessages().removeAll(messagesBefore);
//                this.logger.info("cleanup chat current box of user " + owner.getName() + " chatbox: " + chatBox.getId().toString());
//                this.saveChatBox(owner.getName(), chatBox);
			}
		}
	}

	/**
	 *
	 * @return The Version of the chat plugin
	 */
	@Override
	public String getVersion() {
		return Version.VERSION;
	}

	@Override
	public void deleteAllMessages() {
		Iterator<User> iterator = userAccessor.getUsers().iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			deleteChatBoxesOfUser(user);
		}

	}

	@Override
	public void deleteChatBoxesOfUser(User user) {
		deleteChatBoxesOfUser(ChatUtils.getCorrectUserKey(user.getName()));
	}

	/**
	 * Wird genutzt um alte oder auch neue Formate zu entefernen
	 *
	 * @param usernameOrkey
	 */
	@Override
	public void deleteChatBoxesOfUser(final String usernameOrkey) {
		logger.debug("Remove all chats of user " + usernameOrkey);

		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction() {
				ConfluenceBandanaContext confluenceBandanaContext = getConfluenceBandanaContextHistory(usernameOrkey);
				Iterator<String> iterator = bandanaManager.getKeys(confluenceBandanaContext).iterator();
				while (iterator.hasNext()) {
					bandanaManager.removeValue(confluenceBandanaContext, iterator.next());
				}
				return null;
			}
		});
		chatBoxes.remove(usernameOrkey);
	}

	@Override
	public void doLogout(String username) {
		ChatUser chatUser = getChatUser(username);
		if (chatUser != null) {
		}
	}

	@Override
	public void doLogin(String username) {
		ChatUser chatUser = getChatUser(username);
		if (chatUser != null) {
			this.setOnlineStatus(chatUser.getUsername(), chatUser.getPreferences().getChatStatus());
		}
	}

	@Override
	public List<String> getKeysOfChats(User user) {
		final List<String> usernames = new ArrayList<>();
		final String usernameOrKey = ChatUtils.getCorrectUserKey(user.getName());
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction() {
				ConfluenceBandanaContext confluenceBandanaContextHistory = getConfluenceBandanaContextHistory(usernameOrKey);
				Iterator<String> iterator = bandanaManager.getKeys(confluenceBandanaContextHistory).iterator();
				while (iterator.hasNext()) {

					String key = iterator.next();
					// check if the user was deleted in the meantime
					String userName = ChatUtils.getUserNameByKeyOrUserName(key);
					if (userAccessor.getUser(userName) != null) {
						usernames.add(userName);
					} else {
						bandanaManager.removeValue(confluenceBandanaContextHistory, key);
					}
				}
				return null;
			}
		});
		return usernames;
	}

	private ChatBoxMap transformToUserKeyChatBoxMap(String username, ChatBoxMap map) {
		if (UserCompatibilityHelper.isRenameUserImplemented()) {

			ChatBoxMap newChatBoxMap = new ChatBoxMap();
			String userKey = ChatUtils.getCorrectUserKey(username);

			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				// alte box
				ChatBox box = map.get(iterator.next());
				// change members to userkeys
				List<String> members = new ArrayList<>();
				List<String> userKeyMembers = box.getUserKeyMembers();
				for (String userKeyMember : userKeyMembers) {
					members.add(ChatUtils.getCorrectUserKey(userKeyMember));
				}
				box.getUserKeyMembers().clear();
				box.getUserKeyMembers().addAll(members);

				// change messages from & to
				ChatMessageList messages = box.getMessages();
				for (int i = 0; i < box.getMessages().size(); i++) {
					ChatMessage chatMessage = messages.get(i);
					chatMessage.setFrom(chatMessage.getFrom());
					chatMessage.setTo(chatMessage.getTo());
				}

				this.saveChatBox(userKey, box);
				newChatBoxMap.put(box.getId().toString(), box);
			}
			this.deleteChatBoxesOfUser(username);
			return newChatBoxMap;
		}
		return map;
	}

	@Override
	public Boolean isRenameUserImplemented() {
		return UserCompatibilityHelper.isRenameUserImplemented();
	}
}
