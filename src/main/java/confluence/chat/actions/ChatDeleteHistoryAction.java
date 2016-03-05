package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class ChatDeleteHistoryAction extends AbstractChatAction {

	public ChatDeleteHistoryAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
		super(chatManager, pageManager, permissionManager);
	}

	@Override
	public String execute() throws Exception {
		if (hasChatAccess()) {
			if (StringUtils.isNotBlank(getChatBoxId())) {
				chatManager.deleteChatBox(getRemoteUser(), getChatBoxId());

			}
		}
		return SUCCESS;
	}

	@Override
	public Object getBean() {
		return true;
	}
}
