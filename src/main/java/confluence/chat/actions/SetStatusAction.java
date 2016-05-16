package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.pages.PageManager;
import confluence.chat.manager.ChatManager;

public class SetStatusAction extends AbstractChatAction implements Beanable {

	public SetStatusAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
	}

	@Override
	public final String execute() throws Exception {
		return SUCCESS;
	}

	@Override
	public Object getBean() {
		return true;
	}
}
