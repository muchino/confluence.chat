/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatBox {

    private String usernameOfChatPartner = null;
    private ChatMessageList messages = new ChatMessageList();

    ChatBox() {
    }

    ChatBox(String user) {
        this.usernameOfChatPartner = user;
    }

    /**
     * @return the usernameOfChatPartner
     */
    public String getUsernameOfChatPartner() {
        return usernameOfChatPartner;
    }

    /**
     * @param usernameOfChatPartner the usernameOfChatPartner to set
     */
    public void setUsernameOfChatPartner(String usernameOfChatPartner) {
        this.usernameOfChatPartner = usernameOfChatPartner;
    }

    public void addMessage(ChatMessage chatMessagesa) {
        this.messages.add(chatMessagesa);
    }

    /**
     * @return the messages
     */
    public ChatMessageList getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(ChatMessageList messages) {
        this.messages = messages;
    }

    @Override
    public int hashCode() {
        if (this.usernameOfChatPartner == null) {
            return super.hashCode();
        } else {
            return usernameOfChatPartner.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatBox other = (ChatBox) obj;
        if ((this.usernameOfChatPartner == null) ? (other.usernameOfChatPartner != null) : !this.usernameOfChatPartner.equals(other.usernameOfChatPartner)) {
            return false;
        }
        return true;
    }

    public Map<String, Object> getJSONMap() {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("un", usernameOfChatPartner);
        if (!this.messages.isEmpty()) {
            List<Map> messageList = new ArrayList<Map>();
            for (int i = 0; i < messages.size(); i++) {
                messageList.add(messages.get(i).getJSONMap());
            }
            jsonMap.put("messages", messageList);
        }


        return jsonMap;
    }
}
