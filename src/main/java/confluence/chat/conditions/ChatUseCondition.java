/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.conditions;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import confluence.chat.config.Configuration;
import confluence.chat.manager.ChatManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class ChatUseCondition {

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ChatUseCondition.class);
    private ChatManager chatManager;
    private GroupManager groupManager;

    public ChatUseCondition(ChatManager chatManager, GroupManager groupManager) {
        this.chatManager = chatManager;
        this.groupManager = groupManager;
    }

    /**
     * Entweder global access oder space access
     */
    public boolean hasAccess(User user, String spaceKey) {
        if (user == null) {
            return false;
        }
        boolean allowed = hasAccess(user, chatManager.getChatConfiguration());
        if (!allowed && StringUtils.isNotEmpty(spaceKey)) {
            allowed = hasAccess(user, chatManager.getChatSpaceConfiguration(spaceKey));
        }
        return allowed;
    }

    private boolean hasAccess(User user, Configuration config) {
        if (config != null) {
            if (config.getAllowAll()) {
                return true;
            } else {
                List<String> groupsList = config.getGroups();
                for (int i = 0; i < groupsList.size(); i++) {
                    Group group;
                    try {
                        group = groupManager.getGroup(groupsList.get(i));
                        if (groupManager.hasMembership(group, user)) {
                            return true;
                        }
                    } catch (EntityException ex) {
                        Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return false;
    }
}
