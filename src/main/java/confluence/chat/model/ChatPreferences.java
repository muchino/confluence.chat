/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatPreferences implements Serializable {

    private Map<String, Object> jsonMap = new HashMap<String, Object>();
    public static final String SHOW_CONTACTS = "contacts";
    public static final String SHOW_CURRENTSITE = "scs";
    public static final String STATUS = "s";
    private String chatStatus = ChatStatus.ONLINE.toString();

    public ChatPreferences() {
        this.jsonMap.put(SHOW_CONTACTS, true);
        this.jsonMap.put(SHOW_CURRENTSITE, true);


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
     * @return the showContacts
     */
    public Boolean getShowCurrentSite() {
        if (this.jsonMap.get(SHOW_CURRENTSITE) == null) {
            this.setShowCurrentSite(Boolean.TRUE);
        }
        return (Boolean) this.jsonMap.get(SHOW_CURRENTSITE);
    }

    /**
     * @param showContacts the showContacts to set
     */
    public void setShowCurrentSite(Boolean showCurrentSite) {
        this.jsonMap.put(SHOW_CURRENTSITE, showCurrentSite);
    }

    /**
     * @return the chatStatus
     */
    public ChatStatus getChatStatus() {
        return ChatStatus.getChatStatus(chatStatus);

    }

    /**
     * @param chatStatus the chatStatus to set
     */
    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus.toString();
    }

    public Map<String, Object> getJSONMap() {
        jsonMap.put(STATUS, getChatStatus().toString());
        jsonMap.put(SHOW_CURRENTSITE, getShowCurrentSite());
        return jsonMap;
    }
}
