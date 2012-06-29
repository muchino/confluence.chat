/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.user.User;
import java.util.List;

/**
 *
 * @author oli
 */
public interface ChatManager {

    public static final String SESSION_LAST_REQUEST = "confluence.chat.time.last.message";
    public static final String SESSION_OPEN_CHAT_KEY = "confluence.chat.open.chats";
    public static final String SESSION_SHOW_MESSAGES_SINCE = "confluence.chat.show.message.since.";
    public static final Integer SECONDS_TO_BE_AWAY = 30;
    public static final Integer SECONDS_TO_BE_OFFLINE = 60 * 10;

    void closeChatBox(final User user, final ChatBoxId chatBoxId);

    ChatBoxMap getChatBoxes(User user);

    ChatUser getChatUser(User user);

    ChatUser getChatUser(String username);

    ChatBoxMap getNewMessageChatBoxes(User user);

    List<ChatUser> getOnlineUsers();

    ChatPreferences getPreferencesOfUser(String username);

    void sendMessage(final String sender, final String receiver, final String message);

    void setOnlineStatus(String user, ChatStatus status);

    void setOnlineStatus(User user, ChatStatus status);

    void setPreferencesOfUser(String username, ChatPreferences preferences);

    void setProfilPicture(User user, ChatUser chatUser);
}
