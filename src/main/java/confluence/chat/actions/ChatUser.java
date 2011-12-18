/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatUser {

    private static String USERNAME = "un";
    private static String FULLNAME = "fn";
    private static String STATUS = "s";
    private static String STATUSMESSAGE = "sm";
    private static String LASTSEEN = "d";
    private static String USERIMAGE = "p";
    private Date lastSeen;
    private Map<String, String> jsonMap = new HashMap<String, String>();
    private ChatPreferences preferences;

    public ChatUser(String username, ChatPreferences preferences) {
        jsonMap.put(USERNAME, username);
        this.lastSeen = new Date();
        this.preferences = preferences;
    }

    /**
     * @return the lastSeen
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * @return the status
     */
    public ChatStatus getStatus() {
        return preferences.getChatStatus();
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ChatStatus status) {
        this.preferences.setChatStatus(status);
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatUser other = (ChatUser) obj;
        if ((this.getUsername() == null) ? (other.getUsername() != null) : !this.jsonMap.get(USERNAME).equals(other.jsonMap.get(USERNAME))) {
            return false;
        }
        return true;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return this.jsonMap.get(USERNAME);
    }

    /**
     * @return the userimage
     */
    public String getUserImage() {
        return this.jsonMap.get(USERIMAGE);
    }

    /**
     * @param userimage the userimage to set
     */
    public void setUserImage(String userimage) {
        this.jsonMap.put(USERIMAGE, userimage);
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        if (this.jsonMap.get(FULLNAME) == null) {
            return getUsername();
        }
        return this.jsonMap.get(FULLNAME);
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.jsonMap.put(FULLNAME, fullName);
    }

    public Map<String, String> getJSONMap() {
        jsonMap.put(STATUS, getStatus().toString());
//        jsonMap.put(LASTSEEN, formatter.getFormatMessage(getLastSeen()).toString());
        return jsonMap;
    }

    /**
     * @return the preferences
     */
    public ChatPreferences getPreferences() {
        return preferences;
    }
}
