/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.user.User;
import confluence.chat.utils.ChatUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author oli
 */
public final class ChatBoxId implements Serializable {

	/**
	 * Members als userkey oder username (pre 5.3)/
	 */
	private List<String> members = new ArrayList<String>();

	public ChatBoxId() {
	}

	/**
	 *
	 * @param memberKeys Username
	 */
	public ChatBoxId(List<String> memberKeys) {
		this.members = memberKeys;
	}

	public ChatBoxId(String userKey) {
		this.addMember(userKey);
	}

	public ChatBoxId(User user) {
		this(ChatUtils.getCorrectUserKey(user.getName()));
	}

	/**
	 * @param the username of the member
	 */
	private void addMember(String usernameOrKey) {
		if (!members.contains(usernameOrKey)) {
			this.getMembers().add(usernameOrKey);
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
		if (UserCompatibilityHelper.isRenameUserImplemented()) {
			if (this.members.size() == 1) {
				return this.members.get(0);
			} else {
			}
			return "group_" + hashCode();
		} else {
			return hashCode() + "";
		}
	}

	/**
	 * @return the members as userkeys
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
