/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import com.atlassian.user.User;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Dev
 */
public class ChatUserList extends HashMap<String, ChatUser> {

    public ChatUser putUser(User user, ChatPreferences pref) {
        ChatUser chatuser = new ChatUser(user.getName(), pref);
        chatuser.setLastMouseMove(new Date());
        chatuser.setFullName(user.getFullName());
        put(chatuser.getUsername(), chatuser);
        return chatuser;
    }

    public ChatUser getUser(User user) {
        return this.get(user.getName());
    }
}
