/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.security.PermissionManager;

/**
 *
 * @author oli
 */
public class ChatStartAction extends AbstractChatAction {

    public ChatStartAction(ChatManager chatManager, ContentEntityManager contentEntityManager, PermissionManager permissionManager) {
        super(chatManager, contentEntityManager, permissionManager);
    }

    @Override
    public final String execute() throws Exception {
        super.start();
        return SUCCESS;
    }
}
