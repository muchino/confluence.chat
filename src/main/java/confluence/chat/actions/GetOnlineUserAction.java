package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatUserMapComparable;
import confluence.chat.utils.ChatReplyTransformer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOnlineUserAction extends AbstractChatAction {

	private List<ChatUserMapComparable> chatUserList;

	public GetOnlineUserAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
		super(chatManager, pageManager, permissionManager);
	}

	@Override
	public final String execute() throws Exception {
		ChatReplyTransformer chatReplyTransformer = new ChatReplyTransformer(pageManager, permissionManager);
		chatUserList = chatReplyTransformer.chatUserListToMap(getRemoteUser(), getOnlineUsers());
		return SUCCESS;
	}

	@Override
	public Object getBean() {
		Map<String, Object> bean = new HashMap<>();
		bean.put("users", chatUserList);
		return bean;
	}

	public List<ChatUser> getOnlineUsers() {
		return chatManager.getOnlineUsers(getSpaceKey());
	}


}
