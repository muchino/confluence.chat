/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import confluence.chat.manager.ChatManager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Dev
 */
public class ChatUser implements Serializable {

    private static String ID = "id";
    private Date lastSeen;
    private Date lastMouseMove;
    private ChatStatus status = null;
    private Map<String, String> jsonMap = new HashMap<String, String>();
    private ChatPreferences preferences;
    public static String USERNAME = "un";
    public static String FULLNAME = "fn";
    public static String STATUS = "s";
    public static String STATUSMESSAGE = "sm";
    public static String LASTSEEN = "d";
    public static String USERIMAGE = "p";
    public static String CURRENT_SITE_URL = "su";
    public static String CURRENT_SITE_TITLE = "st";
    public static String CURRENT_CONTENT_ID = "pageId";
    private static final String CONFLUENCE_TITLE = " - Confluence";

    public ChatUser(String username, ChatPreferences preferences) {
        jsonMap.put(USERNAME, username);
        ChatBoxId chatBoxId = new ChatBoxId(username);
        jsonMap.put(ID, chatBoxId.toString());
        this.lastSeen = new Date();
        this.preferences = preferences;
        this.status = this.preferences.getChatStatus();
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
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ChatStatus status) {
        this.status = status;
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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -ChatManager.SECONDS_TO_BE_AWAY);
        if (getStatus().equals(ChatStatus.ONLINE)
                && cal.getTime().after(getLastMouseMove())) {

            jsonMap.put(STATUS, ChatStatus.AWAY.toString());
        } else {
            jsonMap.put(STATUS, getStatus().toString());
        }
        if (!this.getPreferences().getShowCurrentSite()) {
            this.removeCurrentSite();
        }
        return jsonMap;
    }

    /**
     * @return the preferences
     */
    public ChatPreferences getPreferences() {
        return preferences;
    }

    public void setCurrentSite(Long pageId) {
        this.removeCurrentSite();
        if (this.getPreferences().getShowCurrentSite()) {
            this.jsonMap.put(CURRENT_CONTENT_ID, pageId + "");
        }
    }

    public void setCurrentSite(String currentUrl, String currentTitle) {
        this.removeCurrentSite();
        if (this.getPreferences().getShowCurrentSite()) {
            if (StringUtils.isNotEmpty(currentTitle) && StringUtils.isNotEmpty(currentUrl)) {
                this.jsonMap.put(CURRENT_SITE_TITLE, parseSiteTitle(currentTitle));
                this.jsonMap.put(CURRENT_SITE_URL, currentUrl);
            }
        }
    }

    public void removeCurrentSite() {
        this.jsonMap.remove(CURRENT_SITE_TITLE);
        this.jsonMap.remove(CURRENT_SITE_URL);
        this.jsonMap.remove(CURRENT_CONTENT_ID);
    }

    /**
     * @return the lastMouseMove
     */
    public Date getLastMouseMove() {
        return lastMouseMove;
    }

    /**
     * @param lastMouseMove the lastMouseMove to set
     */
    public void setLastMouseMove(Date lastMouseMove) {
        this.lastMouseMove = lastMouseMove;
    }

    private static String parseSiteTitle(String title) {
        if (StringUtils.isNotBlank(title)) {
            if (title.endsWith(CONFLUENCE_TITLE)) {
                title = title.replace(CONFLUENCE_TITLE, "");
            }
        }
        return title;
    }
}
