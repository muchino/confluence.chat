/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import confluence.chat.utils.ChatUtils;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Dev
 */
public class ChatBoxMap extends ConcurrentHashMap<String, ChatBox> implements Serializable {

    public ChatBox getChatBoxWithUser(String username) {
        String correctUserKey = ChatUtils.getCorrectUserKey(username);
        ChatBoxId chatBoxId = new ChatBoxId(correctUserKey);
        return this.getChatBoxById(chatBoxId);
    }

    public ChatBox getChatBoxById(ChatBoxId chatBoxId) {

        if (!this.containsKey(chatBoxId.toString())) {
            this.put(chatBoxId.toString(), new ChatBox(chatBoxId));
        }
        return this.getChatBoxByStringId(chatBoxId.toString());
    }

    public ChatBox getChatBoxByStringId(String chatBoxId) {

        return this.get(chatBoxId);
    }

    public ChatBox remove(ChatBoxId id) {
        return this.remove(id.toString());
    }

    public Boolean hasChatBoxWithUser(String username) {
        String correctUserKey = ChatUtils.getCorrectUserKey(username);
        ChatBoxId chatBoxId = new ChatBoxId(correctUserKey);
        return this.containsKey(chatBoxId.toString());
    }
}
