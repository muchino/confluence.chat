/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.thoughtworks.xstream.XStream;
import confluence.chat.conditions.ChatUseCondition;
import confluence.chat.config.ChatConfiguration;
import java.util.*;

/**
 *
 * @author osr
 */
public final class DefaultChatManager implements ChatManager {

    private static final String KEY_HISTORY = "confluence.chat.history.";
    private static final String KEY_PREFERENCES = "confluence.chat.preferences";
    private static final String KEY_GLOBAL_CONFIGURATION = "confluence.chat.configuration";
    private static final String BANDANA_CHAT = "confluence.chat";
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    private BandanaManager bandanaManager;
    private UserAccessor userAccessor;
    private ConfluenceBandanaContext confluenceBandanaContextPreferences = new ConfluenceBandanaContext(KEY_PREFERENCES);
    private ConfluenceBandanaContext confluenceBandanaContextChat = new ConfluenceBandanaContext(BANDANA_CHAT);
    private ChatUserList users = new ChatUserList();
    private Map<String, ChatBoxMap> chatBoxes = new HashMap<String, ChatBoxMap>();
    private TransactionTemplate transactionTemplate;
    private GroupManager groupManager;
    private ChatUseCondition chatUseCondition;
    private ChatConfiguration chatConfiguration;

    public DefaultChatManager(final BandanaManager bandanaManager, final UserAccessor userAccessor, final TransactionTemplate transactionTemplate, final GroupManager groupManager) {
        this.bandanaManager = bandanaManager;
        this.userAccessor = userAccessor;
        this.transactionTemplate = transactionTemplate;
        this.groupManager = groupManager;
    }

    @Override
    public ChatBoxMap getChatBoxes(User user) {
        return getChatBoxes(user.getName());
    }

    @Override
    public ChatBoxMap getNewMessageChatBoxes(User user) {
        return getChatBoxes(user.getName());
    }

    private ChatBoxMap getChatBoxes(final String username) {


        if (!chatBoxes.containsKey(username)) {

            return (ChatBoxMap) transactionTemplate.execute(new TransactionCallback() {

                @Override
                public ChatBoxMap doInTransaction() {
                    ChatBoxMap chatBoxMap = new ChatBoxMap();
                    ConfluenceBandanaContext confluenceBandanaContext = getConfluenceBandanaContextHistory(username);
                    Boolean validChatBox = true;
                    try {

                        XStream xStream = new XStream();
                        // Set the classloader to this class's class loader, which should be the plugin classloader
                        xStream.setClassLoader(ChatBox.class.getClassLoader());
                        xStream.alias("ChatBox", ChatBox.class);
                        Iterator<String> iterator = bandanaManager.getKeys(confluenceBandanaContext).iterator();
                        while (iterator.hasNext()) {
                            String chatBoxId = iterator.next();
                            try {
                                ChatBox chatBox = (ChatBox) bandanaManager.getValue(confluenceBandanaContext, chatBoxId);
                                chatBoxMap.put(chatBoxId, chatBox);
                            } catch (Exception e) {
                                logger.warn(" error bandanaManager.getValue for chat box  " + chatBoxId + " for user: " + username);
                                validChatBox = false;
                            }
                        }
                    } catch (Exception e) {
                        logger.warn(" error getChatBoxes " + username, e);
                    }
                    /**
                     * while updateing plugin -> an error could occour
                     */
                    if (validChatBox) {
                        chatBoxes.put(username, chatBoxMap);
                    }
                    return chatBoxMap;

                }
            });
        } else {
            return chatBoxes.get(username);
        }

    }

    private ChatPreferences getPreferencesOfUser(String username) {
        ChatPreferences preferences = null;
        try {
            preferences = (ChatPreferences) bandanaManager.getValue(confluenceBandanaContextPreferences, username);
        } catch (Exception e) {
        }
        if (preferences == null) {
            preferences = new ChatPreferences();
        }
        return preferences;
    }

    @Override
    public void setPreferencesOfUser(String username, ChatPreferences preferences) {
        bandanaManager.setValue(confluenceBandanaContextPreferences, username, preferences);
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
                saveChatBox(receiver, chatBoxWithReceiver);
                //        own chatboxes
                ChatBox chatBoxSelf = getChatBoxes(sender).getChatBoxWithUser(receiver);
                chatBoxSelf.addMessage(chatMessage);
                chatBoxSelf.open();
                saveChatBox(sender, chatBoxSelf);
                return true;
            }
        });
    }

    @Override
    public void closeChatBox(final User user, final ChatBoxId chatBoxId) {
        transactionTemplate.execute(new TransactionCallback() {

            @Override
            public Boolean doInTransaction() {
                ChatBox chatBoxById = getChatBoxes(user).getChatBoxById(chatBoxId);
                chatBoxById.close();
                saveChatBox(user.getName(), chatBoxById);
                return true;
            }
        });
    }

    private void saveChatBox(String chatBoxOwner, ChatBox chatBox) {
        bandanaManager.setValue(getConfluenceBandanaContextHistory(chatBoxOwner), chatBox.getId().toString(), chatBox);
    }

    @Override
    public List<ChatUser> getOnlineUsers() {
        List<ChatUser> onlineUserList = new ArrayList<ChatUser>();
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
                onlineUserList.add(chatUser);

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
        ChatUser chatUser;
        if (!this.users.containsKey(user.getName())) {
            chatUser = this.users.putUser(user, getPreferencesOfUser(user.getName()));
            this.setProfilPicture(user, chatUser);
        } else {
            chatUser = this.users.get(user.getName());
        }
        if (!getChatConfiguration().getShowWhereIam()) {
            chatUser.removeCurrentSite();
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
    public void setOnlineStatus(User user, ChatStatus status) {
        ChatUser chatUser = getChatUser(user);
        if (chatUser != null) {
            // change status
            if (status != null && status != ChatStatus.NO_CHANGE) {
                chatUser.setStatus(status);
            } else {
                chatUser.setStatus(chatUser.getPreferences().getChatStatus());
            }
            chatUser.setLastSeen(new Date());
        }
    }

    private ConfluenceBandanaContext getConfluenceBandanaContextHistory(String username) {
        return new ConfluenceBandanaContext(KEY_HISTORY + username);
    }

    @Override
    public ChatConfiguration getChatConfiguration() {
        if (chatConfiguration == null) {
            XStream xStream = new XStream();
            xStream.setClassLoader(ChatConfiguration.class.getClassLoader());
            xStream.alias("ChatConfiguration", ChatConfiguration.class);
            try {
                chatConfiguration = (ChatConfiguration) bandanaManager.getValue(confluenceBandanaContextChat, KEY_GLOBAL_CONFIGURATION);
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
            bandanaManager.setValue(confluenceBandanaContextChat, KEY_GLOBAL_CONFIGURATION, config);
            chatConfiguration = config;
            chatUseCondition = null;
        }
    }

    @Override
    public Boolean hasChatAccess(User user) {
        if (getChatConfiguration().getAllowAll()) {
            return true;
        } else {
            if (chatUseCondition == null) {
                chatUseCondition = new ChatUseCondition(this, groupManager);
            }
            return chatUseCondition.hasAccess(user);
        }
    }
}
