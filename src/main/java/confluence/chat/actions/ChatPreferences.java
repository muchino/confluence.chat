/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author Dev
 */
public class ChatPreferences {

    private Boolean showContacts = false;
    private ChatStatus chatStatus = ChatStatus.ONLINE;

    /**
     * @return the showContacts
     */
    public Boolean getShowContacts() {
        return showContacts;
    }

    /**
     * @param showContacts the showContacts to set
     */
    public void setShowContacts(Boolean showContacts) {
        this.showContacts = showContacts;
    }

    /**
     * @return the chatStatus
     */
    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    /**
     * @param chatStatus the chatStatus to set
     */
    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }
}
