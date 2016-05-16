package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import confluence.chat.manager.ChatManager;
import org.apache.commons.lang.StringUtils;

public class ChatCloseAction extends AbstractChatAction {

	public ChatCloseAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
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
