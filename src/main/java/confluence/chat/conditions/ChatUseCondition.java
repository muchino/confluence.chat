/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.conditions;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import confluence.chat.config.ChatConfiguration;
import confluence.chat.config.ChatSpaceConfiguration;
import confluence.chat.manager.ChatManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class ChatUseCondition {

    private ChatManager chatManager;
    private GroupManager groupManager;
    private List<Group> groups = null;

    public ChatUseCondition(ChatManager chatManager, GroupManager groupManager) {
        this.chatManager = chatManager;
        this.groupManager = groupManager;
    }

    public boolean hasAccess(User user, String spaceKey) {
        if (user == null) {
            return false;
        }
        boolean allowed = hasGloablAccess(user);
        if (allowed && StringUtils.isNotEmpty(spaceKey)) {
            allowed = hasSpaceAccess(user, spaceKey);
        }
        return allowed;
    }

    private boolean hasGloablAccess(User user) {
        ChatConfiguration config = chatManager.getChatConfiguration();
        if (config.getAllowAll()) {
            return true;
        } else {
            boolean allowed = false || config.getGroups().isEmpty();
            if (groups == null) {
                groups = new ArrayList<Group>();
                List<String> groupsList = config.getGroups();
                for (int i = 0; i < groupsList.size(); i++) {
                    Group group;
                    try {
                        group = groupManager.getGroup(groupsList.get(i));
                        groups.add(group);
                    } catch (EntityException ex) {
                        Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            for (int i = 0; i < groups.size(); i++) {
                try {
                    if (groups.get(i) != null) {
                        if (groupManager.hasMembership(groups.get(i), user)) {
                            allowed = true;
                        }
                    }
                } catch (EntityException ex) {
                    Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return allowed;
        }
    }

    private boolean hasSpaceAccess(User user, String spaceKey) {
        ChatSpaceConfiguration config = chatManager.getChatSpaceConfiguration(spaceKey);
        if (config.getAllowAll()) {
            return true;
        } else {
            boolean allowed = false || config.getGroups().isEmpty();
            if (groups == null) {
                groups = new ArrayList<Group>();
                List<String> groupsList = config.getGroups();
                for (int i = 0; i < groupsList.size(); i++) {
                    Group group;
                    try {
                        group = groupManager.getGroup(groupsList.get(i));
                        groups.add(group);
                    } catch (EntityException ex) {
                        Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            for (int i = 0; i < groups.size(); i++) {
                try {
                    if (groups.get(i) != null) {
                        if (groupManager.hasMembership(groups.get(i), user)) {
                            allowed = true;
                        }
                    }
                } catch (EntityException ex) {
                    Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return allowed;
        }
    }
}
