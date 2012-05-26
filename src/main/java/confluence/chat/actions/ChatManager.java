/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.thoughtworks.xstream.XStream;
import java.util.*;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author osr
 */
public class ChatManager {

    public static final String SESSION_OPEN_CHAT_KEY = "confluence.chat.open.chats";
    public static final String SESSION_LAST_REQUEST = "confluence.chat.time.last.message";
    public static final String SESSION_SHOW_MESSAGES_SINCE = "confluence.chat.show.message.since.";
    private static final String KEY_HISTORY = "confluence.chat.history.";
    private static final String KEY_PREFERENCES = "confluence.chat.preferences";
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    private BandanaManager bandanaManager = (BandanaManager) ContainerManager.getComponent("bandanaManager");
    private UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
    private PlatformTransactionManager transactionManager = (PlatformTransactionManager) ContainerManager.getComponent("transactionManager");
    private ConfluenceBandanaContext confluenceBandanaContextPreferences = new ConfluenceBandanaContext(KEY_PREFERENCES);
    private ChatUserList users = new ChatUserList();
    private Map<String, ChatBoxMap> chatBoxes = new HashMap<String, ChatBoxMap>();

    public ChatManager() {
    }

    public ChatBoxMap getChatBoxes(User user) {
        return getChatBoxes(user.getName());
    }

    public ChatBoxMap getNewMessageChatBoxes(User user) {
        return getChatBoxes(user.getName());
    }

    private ChatBoxMap getChatBoxes(final String username) {


        if (!chatBoxes.containsKey(username)) {
            TransactionTemplate tt = new TransactionTemplate();
            tt.setTransactionManager(transactionManager);
            return (ChatBoxMap) tt.execute(new TransactionCallback() {

                @Override
                public ChatBoxMap doInTransaction(TransactionStatus ts) {
                    ChatBoxMap chatBoxMap = new ChatBoxMap();
                    ConfluenceBandanaContext confluenceBandanaContext = getConfluenceBandanaContext(username);
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

    public ChatPreferences getPreferencesOfUser(String username) {
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

    public void setPreferencesOfUser(String username, ChatPreferences preferences) {
        bandanaManager.setValue(confluenceBandanaContextPreferences, username, preferences);
    }

    public void sendMessage(final String sender, final String receiver, final String message) {

        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(transactionManager);
        tt.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
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
            }
        });
    }

    public void closeChatBox(final User user, final ChatBoxId chatBoxId) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(transactionManager);
        tt.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
                ChatBox chatBoxById = getChatBoxes(user).getChatBoxById(chatBoxId);
                chatBoxById.close();
                saveChatBox(user.getName(), chatBoxById);
            }
        });
    }

    private void saveChatBox(String chatBoxOwner, ChatBox chatBox) {
        bandanaManager.setValue(getConfluenceBandanaContext(chatBoxOwner), chatBox.getId().toString(), chatBox);
    }

    public List<ChatUser> getOnlineUsers() {
        List<ChatUser> onlineUserList = new ArrayList<ChatUser>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -30);
        Date time = cal.getTime();
        for (Map.Entry<String, ChatUser> user : users.entrySet()) {
            ChatUser chatUser = user.getValue();
            if (!ChatStatus.OFFLINE.equals(chatUser.getStatus())
                    && time.before(chatUser.getLastSeen())) {
                onlineUserList.add(chatUser);

            }
        }
        return onlineUserList;
    }

    public void setOnlineStatus(String user, ChatStatus status) {
        this.setOnlineStatus(userAccessor.getUser(user), status);
    }

    public ChatUser getChatUser(User user) {
        ChatUser chatUser;
        if (!this.users.containsKey(user.getName())) {
            chatUser = this.users.putUser(user, getPreferencesOfUser(user.getName()));
            this.setProfilPicture(user, chatUser);
        } else {
            chatUser = this.users.get(user.getName());
        }
        return chatUser;

    }

    public ChatUser getChatUser(String username) {
        User user = userAccessor.getUser(username);
        return getChatUser(user);
    }

    public void setProfilPicture(User user, ChatUser chatUser) {
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

    public void setOnlineStatus(User user, ChatStatus status) {
        ChatUser chatUser = getChatUser(user);
        if (chatUser != null) {
            // change status
            if (status != null && status != ChatStatus.NO_CHANGE) {
                chatUser.setStatus(status);
            }
            chatUser.setLastSeen(new Date());
        }
    }

    private ConfluenceBandanaContext getConfluenceBandanaContext(String username) {
        return new ConfluenceBandanaContext(KEY_HISTORY + username);
    }
}
