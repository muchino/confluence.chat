/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.Date;

/**
 *
 * @author Dev
 */
public class ChatUser {

    private String username;
    private String userImage;
    private Date lastSeen;
    private ChatStatus status = ChatStatus.OFFLINE;

    public ChatUser(String username) {
        this.username = username;
        this.lastSeen = new Date();
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
        return status;
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
        if ((this.getUsername() == null) ? (other.getUsername() != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the userimage
     */
    public String getUserImage() {
        return userImage;
    }

    /**
     * @param userimage the userimage to set
     */
    public void setUserImage(String userimage) {
        this.userImage = userimage;
    }
}
