/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import confluence.chat.manager.ChatManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

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
		if (hasChatAccess() && StringUtils.isNotBlank(getChatBoxId())) {
			chatManager.closeChatBox(getRemoteUser(), getChatBoxId());
		}
		return SUCCESS;
	}

	@Override
	public Object getBean() {
		return true;
	}
}
