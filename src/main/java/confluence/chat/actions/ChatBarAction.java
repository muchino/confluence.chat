package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import confluence.chat.manager.ChatManager;

public class ChatBarAction extends AbstractChatAction {

	public ChatBarAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
		super(chatManager, pageManager, permissionManager);
	}

	@Override
	public final String execute() throws Exception {
		return SUCCESS;
	}
}
