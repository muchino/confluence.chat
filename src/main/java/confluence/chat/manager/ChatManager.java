/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.manager;

import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.user.User;
import confluence.chat.config.ChatConfiguration;
import confluence.chat.config.ChatSpaceConfiguration;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatPreferences;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatUser;
import java.util.List;

/**
 *
 * @author oli
 */
public interface ChatManager {

    public static final String KEY_GLOBAL_CONFIGURATION = "confluence.chat.configuration";
    public static final String SESSION_LAST_REQUEST = "confluence.chat.time.last.message";
    public static final String KEY_HISTORY = "confluence.chat.history.";
    public static final String QUERY_HISTORIES = "SELECT DISTINCT BANDANACONTEXT FROM bandana WHERE BANDANACONTEXT LIKE '" + KEY_HISTORY + "%'";
    public static final String SESSION_OPEN_CHAT_KEY = "confluence.chat.open.chats";
    public static final String SESSION_SHOW_MESSAGES_SINCE = "confluence.chat.show.message.since.";
    public static final Integer SECONDS_TO_BE_AWAY = 60 * 10;
    public static final Integer SECONDS_TO_BE_OFFLINE = 10;
    public ConfluenceBandanaContext CONTEXT_USER_PREFERENCES = new ConfluenceBandanaContext("confluence.chat.preferences");
    public ConfluenceBandanaContext CONTEXT_CHAT_SETTINGS = new ConfluenceBandanaContext("confluence.chat");

    void closeChatBox(final User user, final String chatBoxId);

    void deleteChatBox(final User user, final String chatBoxId);

    public ChatBoxMap getChatBoxes(User user);

    public ChatUser getChatUser(User user);

    public ChatUser getChatUser(String username);

    public ChatBoxMap getNewMessageChatBoxes(User user);

    public List<ChatUser> getOnlineUsers(String spaceKey);

    public void sendMessage(final String sender, final String receiver, final String message, final String id);

    public void setOnlineStatus(String user, ChatStatus status);

    public void setOnlineStatus(User user, ChatStatus status);

    public void setPreferencesOfUser(String username, ChatPreferences preferences);

    public void setChatConfiguration(ChatConfiguration config);

    public ChatConfiguration getChatConfiguration();

    public ChatSpaceConfiguration getChatSpaceConfiguration(String spaceKey);

    public void setChatSpaceConfiguration(ChatSpaceConfiguration config, String spaceKey);

    public Boolean hasChatAccess(User user, String spaceKey);

    public void manageHistory(ChatBox chatBox, User owner);

    public String getVersion();

    public void deleteAllMessages();

    public void deleteChatBoxesOfUser(User user);

    public void doLogout(String username);

    public void doLogin(String username);

    public List<String> getUsersWithChats(User user);
}
