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
public class ChatBoxMap extends HashMap<String, ChatBox> {
    
    public ChatBox getChatBoxWithUser(String user){
        if(!this.containsKey(user)){
            this.put(user, new ChatBox(user));
        }
        return this.get(user);
    }
 
}
