package confluence.chat.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import confluence.chat.manager.ChatManager;

public class ChatHistoryEnabledCondition extends BaseConfluenceCondition {

	private ChatManager chatManager;

	@Override
	protected boolean shouldDisplay(WebInterfaceContext wic) {
		return chatManager.getChatConfiguration().getShowHistory();
	}

	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
