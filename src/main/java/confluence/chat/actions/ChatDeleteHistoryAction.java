package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import confluence.chat.manager.ChatManager;
import org.apache.commons.lang.StringUtils;

public class ChatDeleteHistoryAction extends AbstractChatAction {

	public ChatDeleteHistoryAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
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
