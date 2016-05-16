package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatUser;
import java.util.Date;
import java.util.Iterator;

public class HeartbeatAction extends AbstractChatAction {

	public HeartbeatAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
	}

	@Override
	public final String execute() throws Exception {
		if (hasChatAccess()) {
			ChatUser chatUser = getChatUser();

			// Keine senden, falls user offline
			if (!ChatStatus.OFFLINE.equals(chatUser.getStatus())) {
				chatManager.setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
				ChatBoxMap chatBoxes = chatManager.getChatBoxes(getRemoteUser());
				Date lastRequestDate = getLastRequestDate();
				Iterator<String> iterator = chatBoxes.keySet().iterator();
				while (iterator.hasNext()) {
					ChatBox chatBox = chatBoxes.get(iterator.next());
					this.addMessagesSince(chatBox, lastRequestDate);
				}
			}
		}
		return SUCCESS;
	}
}
