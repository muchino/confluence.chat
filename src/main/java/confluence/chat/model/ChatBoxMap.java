/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Dev
 */
public class ChatBoxMap extends ConcurrentHashMap<String, ChatBox> implements Serializable {

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

    public ChatBox remove(ChatBoxId id) {
        return this.remove(id.toString());
    }

    public Boolean hasChatBoxWithUser(String user) {
        ChatBoxId chatBoxId = new ChatBoxId(user);
        return this.containsKey(chatBoxId.toString());
    }
}
