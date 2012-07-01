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
    public static final Integer SECONDS_TO_BE_AWAY = 60 * 10;
    public static final Integer SECONDS_TO_BE_OFFLINE = 30;

    void closeChatBox(final User user, final ChatBoxId chatBoxId);

    public ChatBoxMap getChatBoxes(User user);

    public ChatUser getChatUser(User user);

    public ChatUser getChatUser(String username);

    public ChatBoxMap getNewMessageChatBoxes(User user);

    public List<ChatUser> getOnlineUsers();

    public void sendMessage(final String sender, final String receiver, final String message);

    public void setOnlineStatus(String user, ChatStatus status);

    public void setOnlineStatus(User user, ChatStatus status);

    public void setPreferencesOfUser(String username, ChatPreferences preferences);
}
