package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import confluence.chat.manager.ChatManager;

public class ChatBarAction extends AbstractChatAction {

	public ChatBarAction(ChatManager chatManager, PageManager pageManager ) {
		super(chatManager, pageManager);
	}

	@Override
	public final String execute() throws Exception {
		return SUCCESS;
	}
}
