package confluence.chat.model;

import confluence.chat.actions.AbstractChatAction;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBox implements Serializable {

	private List<String> members = new ArrayList<>();
	private ChatMessageList messages = new ChatMessageList();
	private Date lastMessage = new Date();
	private Boolean open = true;

	public ChatBox() {
	}

	public ChatBox(ChatBoxId id) {
		this.members = id.getMembers();
	}

	public void setId(ChatBoxId id) {
		this.members = id.getMembers();
	}

	public ChatBoxId getId() {
		return new ChatBoxId(members);
	}

	public void addMessage(ChatMessage chatMessages) {
		if (this.getLastMessage().before(chatMessages.getSenddate())) {
			this.setLastMessage(chatMessages.getSenddate());
		}
		this.messages.add(chatMessages);
	}

	public Boolean hasMessageSince(Date date) {
		return this.getLastMessage().after(date);
	}

	/**
	 * @return the messages
	 */
	public ChatMessageList getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(ChatMessageList messages) {
		this.messages = messages;
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChatBox other = (ChatBox) obj;

		return other.getId().equals(this.getId());
	}

	public Map<String, Object> getJSONMap(ChatManager manager) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("id", this.getId().toString());
		List<String> userNameMembers = new ArrayList<String>();
		for (String member : members) {
			userNameMembers.add(ChatUtils.getUserNameByKeyOrUserName(member));
		}
		jsonMap.put("un", userNameMembers);
		jsonMap.put("open", this.isOpen());
		jsonMap.put("lm", this.getLastMessage().getTime());
		if (!this.messages.isEmpty()) {
			List<Map> messageList = new ArrayList<>();
			for (ChatMessage chatMessage : messages) {

				ChatUser chatUserFrom = manager.getChatUser(chatMessage.getFrom());
				ChatUser chatUserTo = manager.getChatUser(chatMessage.getTo());

				if (chatUserFrom != null && chatUserTo != null) {
					Map<String, Object> message = new HashMap<>();
					message.put(ChatMessage.FROM, chatUserFrom.getJSONMap());
					message.put(ChatMessage.TO, chatUserTo.getJSONMap());
					message.put(ChatMessage.MESSAGE, chatMessage.getMessage());
					message.put(ChatMessage.SENDDATE, chatMessage.getSenddate().getTime());
					message.put(ChatMessage.MESSAGE_ID, chatMessage.getId());
					messageList.add(message);
				}
			}
			jsonMap.put("messages", messageList);
		}

		return jsonMap;
	}

	public void close() {
		this.open = false;
	}

	public void open() {
		this.open = true;
	}

	public Boolean isOpen() {
		return this.open;
	}

	/**
	 * @return the lastMessage
	 */
	public Date getLastMessage() {
		return lastMessage;
	}

	/**
	 * @param lastMessage the lastMessage to set
	 */
	public void setLastMessage(Date lastMessage) {
		this.lastMessage = lastMessage;
	}

	@Override
	public String toString() {
		return "ChatBox " + getId().toString();
	}

	/**
	 *
	 * @param date
	 * @return eine chat nachrichten liste , die neuste nachricht ist ganz vorne
	 */
	public ChatMessageList getMessagesSince(Date date) {
		ChatMessageList list = new ChatMessageList();
		for (int j = getMessages().size() - 1; j >= 0; j--) {
			ChatMessage message = getMessages().get(j);
			if (message.getSenddate().after(date)) {
				list.add(message);
			} else {
				break;
			}
		}
		return list;
	}

	/**
	 *
	 * @param date
	 * @return eine chat nachrichten liste , die �lteste ist ganz vorne
	 */
	public ChatMessageList getMessagesBefore(Date date) {
		ChatMessageList list = new ChatMessageList();
		for (int j = 0; j < getMessages().size(); j++) {
			ChatMessage message = getMessages().get(j);
			if (message.getSenddate().before(date)) {
				list.add(message);
			} else {
				break;
			}
		}
		return list;
	}

	public List<String> getUserKeyMembers() {
		return members;
	}
}
