/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

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

    public void addMessage(ChatMessage chatMessagesa){
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
}
