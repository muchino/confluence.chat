package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import confluence.chat.manager.ChatManager;

public class SetStatusAction extends AbstractChatAction implements Beanable {

    public SetStatusAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        super(chatManager, pageManager, permissionManager);
    }

    @Override
    public final String execute() throws Exception {
        setStatus();
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        return true;
    }
}
