/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatPreferences {

    private Map<String, Object> jsonMap = new HashMap<String, Object>();
    public static final String SHOW_CONTACTS = "contacts";
    public static final String STATUS = "s";
    private ChatStatus chatStatus = ChatStatus.ONLINE;

    public ChatPreferences() {
        this.jsonMap.put(SHOW_CONTACTS, true);
    }

    /**
     * @return the showContacts
     */
    public Boolean getShowContacts() {
        return (Boolean) this.jsonMap.get(SHOW_CONTACTS);
    }

    /**
     * @param showContacts the showContacts to set
     */
    public void setShowContacts(Boolean showContacts) {
        this.jsonMap.put(SHOW_CONTACTS, showContacts);
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

    public Map<String, Object> getJSONMap() {
        jsonMap.put(STATUS, getChatStatus().toString());
        return jsonMap;
    }
}
