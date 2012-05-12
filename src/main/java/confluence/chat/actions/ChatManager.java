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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author osr
 */
public class ChatManager {

    private static final String SESSION_OPEN_CHAT_KEY = "confluence.chat.open.chats";
    private static final String KEY_HISTORY_OLD = "confluence.chat.history.old";
    private static final String KEY_HISTORY_NEW = "confluence.chat.history.new";
    private static final String KEY_PREFERENCES = "confluence.chat.preferences";
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    private BandanaManager bandanaManager = (BandanaManager) ContainerManager.getComponent("bandanaManager");
    private UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
    private ConfluenceBandanaContext confluenceBandanaContextNewMessages = new ConfluenceBandanaContext(KEY_HISTORY_NEW);
    private ConfluenceBandanaContext confluenceBandanaContextPreferences = new ConfluenceBandanaContext(KEY_PREFERENCES);
    private Map<String, ChatUser> onlineUsers = new HashMap<String, ChatUser>();
    private ChatUserList users = new ChatUserList();
    private Map<String, ChatBoxMap> newChatBoxes = new HashMap<String, ChatBoxMap>();

    public ChatManager() {
    }

    public ChatBoxMap getNewChatBoxesOfUser(String username) {
//        ChatBoxMap newMessages = null;
//        try {
//            newMessages = (ChatBoxMap) bandanaManager.getValue(confluenceBandanaContextNewMessages, username);
//        } catch (Exception e) {
//        }
        if (!newChatBoxes.containsKey(username)) {
            newChatBoxes.put(username, new ChatBoxMap());

        }

        return newChatBoxes.get(username);
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

    public ChatBox sendMessage(String sender, String receiver, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(sender);
        chatMessage.setTo(receiver);
        chatMessage.setMessage(message);

//        chatboxes of receiver
        ChatBox chatBoxWithReceiver = this.getNewChatBoxesOfUser(receiver).getChatBoxWithUser(sender);
        chatBoxWithReceiver.addMessage(chatMessage);
        this.saveChatBox(receiver, chatBoxWithReceiver);

//        own chatboxes
        ChatBox chatBoxSelf = this.getNewChatBoxesOfUser(sender).getChatBoxWithUser(receiver);
        chatBoxSelf.addMessage(chatMessage);
        this.saveChatBox(sender, chatBoxSelf);
        return chatBoxSelf;
    }

    private ChatBoxMap saveChatBox(String chatBoxOwner, ChatBox chatBox) {
        ChatBoxMap newMessages = this.getNewChatBoxesOfUser(chatBoxOwner);
        newMessages.put(chatBox.getId(), chatBox);
//        bandanaManager.setValue(confluenceBandanaContextNewMessages, chatBoxOwner, newMessages);
        return newMessages;
    }

    public void clearNewMessages(String username) {
//        bandanaManager.removeValue(confluenceBandanaContextNewMessages, username);
        newChatBoxes.remove(username);
    }

    public ChatBoxMap getOpenChats(HttpSession session) {
        ChatBoxMap sessionChatBoxes = null;
        try {
            sessionChatBoxes = (ChatBoxMap) session.getAttribute(SESSION_OPEN_CHAT_KEY);

        } catch (Exception e) {
        }
        if (sessionChatBoxes == null) {
            sessionChatBoxes = new ChatBoxMap();
        }
        return sessionChatBoxes;
    }

    public void saveOpenChats(HttpSession session, ChatBoxMap newChatBoxes) {
        ChatBoxMap sessionChatBoxes = this.getOpenChats(session);
        ChatMessage chatMessage;
        ChatMessageList newMessageList;
        ChatMessageList sessionMessageList;
        ChatBoxId chatBoxId;
        Iterator<ChatBoxId> iterator = newChatBoxes.keySet().iterator();
        while (iterator.hasNext()) {
            chatBoxId = iterator.next();
            newMessageList = newChatBoxes.getChatBoxById(chatBoxId).getMessages();
            sessionMessageList = sessionChatBoxes.getChatBoxById(chatBoxId).getMessages();
            for (int i = 0; i < newMessageList.size(); i++) {
                chatMessage = newMessageList.get(i);
                if (!sessionMessageList.contains(chatMessage)) {
                    sessionMessageList.add(chatMessage);
                }

            }

        }


        session.setAttribute(SESSION_OPEN_CHAT_KEY, sessionChatBoxes);
    }

    public void closeChatWith(HttpSession session, ChatBoxId chatBoxId) {
        if (chatBoxId != null) {
            ChatBoxMap sessionChatBoxes = this.getOpenChats(session);
            sessionChatBoxes.remove(chatBoxId);
            session.setAttribute(SESSION_OPEN_CHAT_KEY, sessionChatBoxes);
        }

    }

    public List<ChatUser> getOnlineUsers() {
        List<ChatUser> onlineUserList = new ArrayList<ChatUser>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -30);
        Date time = cal.getTime();
        for (Map.Entry<String, ChatUser> user : onlineUsers.entrySet()) {
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

    public ChatUser getOnlineChatUser(User user) {
        if (!this.onlineUsers.containsKey(user.getName())) {
            this.onlineUsers.put(user.getName(), this.getChatUser(user));
        }
        return this.onlineUsers.get(user.getName());

    }

    public ChatUser getOnlineChatUser(String username) {
        User user = userAccessor.getUser(username);
        return getOnlineChatUser(user);
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
        ChatUser chatUser = getOnlineChatUser(user);
        if (chatUser != null) {
            // change status
            if (status != null && status != ChatStatus.NO_CHANGE) {
                chatUser.setStatus(status);
            }
            chatUser.setLastSeen(new Date());
        }
    }
}
