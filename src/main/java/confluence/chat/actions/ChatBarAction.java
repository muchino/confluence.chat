package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import confluence.chat.config.ChatConfiguration;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatStatus;
import java.util.HashMap;
import java.util.Map;

public class ChatBarAction extends AbstractChatAction {

	public ChatBarAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
	}

	@Override
	public final String execute() throws Exception {
		return SUCCESS;
	}

	public Map<String, Object> getSoyRenderData() {

		Map<String, Object> soyRenderData = new HashMap<>();
		ChatConfiguration chatConfiguration = chatManager.getChatConfiguration();
		ChatStatus status = getChatUser().getStatus();

		soyRenderData.put("playSound", chatConfiguration.getPlaySound());
		soyRenderData.put("showHistory", chatConfiguration.getShowHistory());
		soyRenderData.put("heartbeat", chatConfiguration.getHeartBeat());
		soyRenderData.put("debugMode", chatConfiguration.getDebugMode());
		soyRenderData.put("hideInEditor", chatConfiguration.getHideInEditMode());
		soyRenderData.put("version", chatManager.getVersion());
		soyRenderData.put("chatUser", chatManager.getChatUser(getRemoteUser()));
		soyRenderData.put("status", status.toString());
		soyRenderData.put("hasChatAccess", hasChatAccess());
		if ("xa".equals(status.toString())) {
			soyRenderData.put("onlineOffline", "offline");
		} else {
			soyRenderData.put("onlineOffline", "online");
		}
		return soyRenderData;
	}
}
