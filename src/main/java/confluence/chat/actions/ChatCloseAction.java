/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import confluence.chat.manager.ChatManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;

/**
 *
 * @author oli
 */
public class ChatCloseAction extends AbstractChatAction {

    public ChatCloseAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        super(chatManager, pageManager, permissionManager);
    }

    @Override
    public final String execute() throws Exception {
        super.close();
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        return true;
    }
}
