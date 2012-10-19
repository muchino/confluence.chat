/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Dev
 */
public class ChatBoxMap extends HashMap<String, ChatBox> implements Serializable {

    public ChatBox getChatBoxWithUser(String user) {


        ChatBoxId chatBoxId = new ChatBoxId(user);
        return this.getChatBoxById(chatBoxId);
    }

    public ChatBox getChatBoxById(ChatBoxId chatBoxId) {

        if (!this.containsKey(chatBoxId.toString())) {
            this.put(chatBoxId.toString(), new ChatBox(chatBoxId));
        }
        return this.get(chatBoxId.toString());
    }
}
