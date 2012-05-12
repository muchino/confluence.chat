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
public class ChatBoxMap extends HashMap<ChatBoxId, ChatBox> {

    public ChatBox getChatBoxWithUser(String user) {
        
        
        ChatBoxId chatBoxId = new ChatBoxId(user);
        return this.getChatBoxById(chatBoxId);
    }

    public ChatBox getChatBoxById(ChatBoxId chatBoxId) {

        if (!this.containsKey(chatBoxId)) {
            this.put(chatBoxId, new ChatBox(chatBoxId));
        }
        return this.get(chatBoxId);
    }
}
