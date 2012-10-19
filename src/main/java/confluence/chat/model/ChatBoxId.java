/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import com.atlassian.user.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author oli
 */
public final class ChatBoxId implements Serializable {

    private List<String> members = new ArrayList<String>();

    public ChatBoxId() {
    }

    public ChatBoxId(List<String> members) {
        this.members = members;
    }

    public ChatBoxId(ChatUser user) {
        this.addMember(user.getUsername());
    }

    public ChatBoxId(String username) {
        this.addMember(username);
    }

    public ChatBoxId(User user) {
        this.addMember(user.getName());
    }

    /**
     * @param members the members to set
     */
    private void addMember(String username) {
        if (!members.contains(username)) {
            this.getMembers().add(username);
            Collections.sort(members);
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
        final ChatBoxId other = (ChatBoxId) obj;
        return other.getMembers().containsAll(getMembers()) && getMembers().containsAll(other.getMembers());

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.members != null ? this.members.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {

        return hashCode() + "";
    }

    /**
     * @return the members
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<String> members) {
        this.members = members;
    }
}
