/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.io.Serializable;
import java.util.*;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Dev
 */
public class ChatBox implements Serializable {

    private List<String> members = new ArrayList<String>();
    private ChatMessageList messages = new ChatMessageList();
    private Date lastMessage = new Date();
    private Boolean open = true;

    public ChatBox() {
    }

    public ChatBox(ChatBoxId id) {
        this.members = id.getMembers();
    }

    public void setId(ChatBoxId id) {
        this.members = id.getMembers();
    }

    public ChatBoxId getId() {
        return new ChatBoxId(getMembers());
    }

    public void addMessage(ChatMessage chatMessages) {
        if (this.getLastMessage().before(chatMessages.getSenddate())) {
            this.setLastMessage(chatMessages.getSenddate());
        }
        this.messages.add(chatMessages);
    }

    public Boolean hasMessageSince(Date date) {
        return this.getLastMessage().after(date);
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
        jsonMap.put("open", this.isOpen());
        jsonMap.put("lm", this.getLastMessage().getTime());
        if (!this.messages.isEmpty()) {
            List<Map> messageList = new ArrayList<Map>();
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage chatMessage = messages.get(i);
                Map<String, Object> message = new HashMap<String, Object>();
                message.put(ChatMessage.FROM, manager.getChatUser(chatMessage.getFrom()).getJSONMap());
                message.put(ChatMessage.TO, manager.getChatUser(chatMessage.getTo()).getJSONMap());
                message.put(ChatMessage.MESSAGE, chatMessage.getMessage());
                message.put(ChatMessage.SENDDATE, chatMessage.getSenddate().getTime());
                message.put(ChatMessage.MESSAGE_ID, chatMessage.getId());
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

    public void close() {
        this.open = false;
    }

    public void open() {
        this.open = true;
    }

    public Boolean isOpen() {
        return this.open;
    }

    /**
     * Beim initialen Seitenrequest -> wieviele nachrichten zeigen
     *
     * @param session
     * @return
     */
    public Date getInitMessagesShowSince(HttpSession session) {
        String key = ChatManager.SESSION_SHOW_MESSAGES_SINCE + this.getId().toString();
        Date date = (Date) session.getAttribute(key);
        if (date == null) {
            date = ChatUtils.getYesterday();
        }
        session.setAttribute(key, date);
        return date;
    }

    /**
     * @return the lastMessage
     */
    public Date getLastMessage() {
        return lastMessage;
    }

    /**
     * @param lastMessage the lastMessage to set
     */
    public void setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "ChatBox " + getId().toString();
    }

    /**
     *
     * @param date
     * @return eine chat nachrichten liste , die neuste nachricht ist ganz vorne
     */
    public ChatMessageList getMessagesSince(Date date) {
        ChatMessageList list = new ChatMessageList();
        for (int j = getMessages().size() - 1; j >= 0; j--) {
            ChatMessage message = getMessages().get(j);
            if (message.getSenddate().after(date)) {
                list.add(message);
            } else {
                break;
            }
        }
        return list;
    }

    /**
     *
     * @param date
     * @return eine chat nachrichten liste , die �lteste ist ganz vorne
     */
    public ChatMessageList getMessagesBefore(Date date) {
        ChatMessageList list = new ChatMessageList();
        for (int j = 0; j < getMessages().size(); j++) {
            ChatMessage message = getMessages().get(j);
            if (message.getSenddate().before(date)) {
                list.add(message);
            } else {
                break;
            }
        }
        return list;
    }
}
