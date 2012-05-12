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

    private List<String> members = new ArrayList<String>();
    private ChatMessageList messages = new ChatMessageList();

    ChatBox(ChatBoxId id) {
        this.members = id.getMembers();
    }

    public ChatBoxId getId() {
        return new ChatBoxId(getMembers());
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
        return this.getId().hashCode();
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

        return other.getId().equals(this.getId());
    }

    public Map<String, Object> getJSONMap(ChatManager manager) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("id", this.getId().toString());
        jsonMap.put("un", this.getMembers());
        if (!this.messages.isEmpty()) {
            List<Map> messageList = new ArrayList<Map>();
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage chatMessage = messages.get(i);
                Map<String, Object> message = new HashMap<String, Object>();
                message.put(ChatMessage.FROM, manager.getChatUser(chatMessage.getFrom()).getJSONMap());
                message.put(ChatMessage.TO, manager.getChatUser(chatMessage.getTo()).getJSONMap());
                message.put(ChatMessage.MESSAGE,chatMessage.getMessage());
                message.put(ChatMessage.SENDDATE, chatMessage.getSenddate().getTime());
                messageList.add(message);
            }
            jsonMap.put("messages", messageList);
        }


        return jsonMap;
    }

    /**
     * @return the members
     */
    public List<String> getMembers() {
        return members;
    }
}
