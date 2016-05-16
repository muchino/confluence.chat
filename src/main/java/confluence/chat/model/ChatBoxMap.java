package confluence.chat.model;

import com.atlassian.confluence.user.UserAccessor;
import confluence.chat.actions.AbstractChatAction;
import confluence.chat.utils.ChatUtils;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatBoxMap extends ConcurrentHashMap<String, ChatBox> implements Serializable {

	public ChatBox getChatBoxWithUser(String username) {
		String correctUserKey = ChatUtils.getCorrectUserKey(username);
		ChatBoxId chatBoxId = new ChatBoxId(correctUserKey);
		return this.getChatBoxById(chatBoxId);
	}

	public ChatBox getChatBoxById(ChatBoxId chatBoxId) {

		if (!this.containsKey(chatBoxId.toString())) {
			this.put(chatBoxId.toString(), new ChatBox(chatBoxId));
		}
		return this.getChatBoxByStringId(chatBoxId.toString());
	}

	public ChatBox getChatBoxByStringId(String chatBoxId) {

		return this.get(chatBoxId);
	}

	public ChatBox remove(ChatBoxId id) {
		return this.remove(id.toString());
	}

	public Boolean hasChatBoxWithUser(String username) {
		String correctUserKey = ChatUtils.getCorrectUserKey(username);
		ChatBoxId chatBoxId = new ChatBoxId(correctUserKey);
		return this.containsKey(chatBoxId.toString());
	}

	public void removeInvalidChatBoxes(UserAccessor userAccessor) {
		for (Map.Entry<String, ChatBox> entry : this.entrySet()) {
			String username = ChatUtils.getUserNameByKeyOrUserName(entry.getValue().getUserKeyMembers().get(0));
			if (userAccessor.getUser(username) == null) {
				super.remove(entry.getKey());
			}
		}
	}
}
