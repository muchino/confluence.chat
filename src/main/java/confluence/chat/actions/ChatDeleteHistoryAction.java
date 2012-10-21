package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import confluence.chat.manager.ChatManager;

public class ChatDeleteHistoryAction extends AbstractChatAction {

    public ChatDeleteHistoryAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        super(chatManager, pageManager, permissionManager);
    }

    @Override
    public String execute() throws Exception {
        super.delete();

        return SUCCESS;
    }

    @Override
    public Object getBean() {
        return true;
    }
}
