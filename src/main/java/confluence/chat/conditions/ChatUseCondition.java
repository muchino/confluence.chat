/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.conditions;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import confluence.chat.actions.ChatManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public boolean hasAccess(User user) {
        if (user == null) {
            return false;
        }


        System.out.println("cond " + chatManager.getChatConfiguration().getAllowAll());
        if (chatManager.getChatConfiguration().getAllowAll()) {
            return true;
        } else {
            boolean allowed = false || chatManager.getChatConfiguration().getGroups().isEmpty();
            System.out.println("allowed " + allowed);
            if (groups == null) {
                groups = new ArrayList<Group>();
                List<String> groupsList = chatManager.getChatConfiguration().getGroups();
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
                    if(groupManager.hasMembership(groups.get(i), user)){
                        allowed = true;
                    }
                } catch (EntityException ex) {
                    Logger.getLogger(ChatUseCondition.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return allowed;
        }

    }
}
