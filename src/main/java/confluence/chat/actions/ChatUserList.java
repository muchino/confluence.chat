/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.HashMap;

/**
 *
 * @author Dev
 */
public class ChatUserList extends HashMap<String, ChatUser> {
    
    public ChatUser getChatUser(String user){
        if(!this.containsKey(user)){
            this.put(user, new ChatUser(user));
        }
        return this.get(user);
    }
 
}
