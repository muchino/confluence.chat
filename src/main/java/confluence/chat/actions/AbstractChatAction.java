package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatMessage;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatUser;
import confluence.chat.utils.ChatUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class AbstractChatAction extends ConfluenceActionSupport implements Beanable {

	private static final Logger logger = Logger.getLogger(AbstractChatAction.class);

	protected final ChatManager chatManager;
	protected final PageManager pageManager;
	protected final PermissionManager permissionManager;

	private String status;
	private String mouseMove;

	/**
	 * holding the chatbox id
	 */
	private String chatBoxId;

	private String spaceKey;
	private String lastHeartBeatServerdate;

	private ChatBoxMap chatBoxMap = new ChatBoxMap();
	private Date newRequestDate = null;

	public AbstractChatAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
		this.chatManager = chatManager;
		this.pageManager = pageManager;
		this.permissionManager = permissionManager;

	}

	/**
	 * Add all important messages from given box
	 *
	 * @param chatBox box where we like to add old messages
	 * @param date the date in the past
	 */
	protected void addMessagesSince(ChatBox chatBox, Date date) {
		if (chatBox.hasMessageSince(date)) {
			chatBoxMap.getChatBoxById(chatBox.getId()).setLastMessage(chatBox.getLastMessage());
			if (chatBox.isOpen()) {
				chatBoxMap.getChatBoxById(chatBox.getId()).open();
			} else {
				chatBoxMap.getChatBoxById(chatBox.getId()).close();
			}
			ChatMessageList messagesSince = chatBox.getMessagesSince(date);
			Collections.reverse(messagesSince);

			for (int i = 0; i < messagesSince.size(); i++) {
				ChatMessage message = messagesSince.get(i);
				this.setNewRequestDate(message.getSenddate());
				chatBoxMap.getChatBoxById(chatBox.getId()).addMessage(message);
			}
		}
		chatManager.manageHistory(chatBox, AuthenticatedUserThreadLocal.getUser());
	}

	@Override
	public Object getBean() {
		Map<String, Object> bean = new HashMap<>();
		if (hasChatAccess()) {
			bean.put("lr", getNewRequestDate().getTime());

			if (!getChatBoxMap().isEmpty()) {
				List<Map> chatboxes = new ArrayList<>();
				Iterator<String> iterator = getChatBoxMap().keySet().iterator();
				while (iterator.hasNext()) {
					chatboxes.add(getChatBoxMap().get(iterator.next()).getJSONMap(chatManager));
				}
				bean.put("chatboxes", chatboxes);
			}
		} else {
			ServletActionContext.getResponse().setStatus(401);
			bean.put("error", "unauthorized");
		}
		return bean;
	}

	/**
	 * @return the chatBoxMap
	 */
	public ChatBoxMap getChatBoxMap() {
		if (chatBoxMap == null) {
			chatBoxMap = new ChatBoxMap();
		}
		return chatBoxMap;
	}

	protected Date getLastRequestDate() {
		Date date = null;
		Calendar cal = Calendar.getInstance();
		if (StringUtils.isNumeric(lastHeartBeatServerdate)) {
			try {
				cal.setTime(new Date(new Long(lastHeartBeatServerdate)));
				cal.add(Calendar.SECOND, -1);
				date = cal.getTime();
			} catch (Exception e) {
			}
		}
		if (date == null) {
			ChatUser chatUser = getChatUser();
			Date lastSeen = chatUser.getLastSeen();
			date = ChatUtils.getYesterday();
			if (lastSeen != null) {
				if (lastSeen.before(date)) {
					date = lastSeen;
				}
			}
		}
		return date;
	}

	protected Boolean isMouseMoved() {
		return "true".equals(mouseMove);
	}

	/**
	 * @return the newRequestDate
	 */
	public Date getNewRequestDate() {
		if (this.newRequestDate == null) {
			this.newRequestDate = this.getLastRequestDate();
		}
		return newRequestDate;
	}

	public void setNewRequestDate() {
		this.newRequestDate = new Date();
	}

	public Boolean hasChatAccess() {
		return chatManager.hasChatAccess(getRemoteUser(), spaceKey);
	}

	private void setNewRequestDate(Date senddate) {
		if (senddate.after(getNewRequestDate())) {
			this.newRequestDate = senddate;
		}
	}

	protected ChatUser getChatUser() {
		return chatManager.getChatUser(getRemoteUser());
	}

	public void setStatus(String status) {
		this.status = status;
		if (StringUtils.isNotBlank(status)) {
			ChatStatus chatStatus = null;
			if ("chat".equals(status)) {
				chatStatus = ChatStatus.ONLINE;
			} else if ("dnd".equals(status)) {
				chatStatus = ChatStatus.DO_NOT_DISTURB;
			} else if ("away".equals(status)) {
				chatStatus = ChatStatus.AWAY;
			} else if ("xa".equals(status)) {
				chatStatus = ChatStatus.OFFLINE;
			}
			if (chatStatus != null) {
				chatManager.setOnlineStatus(getRemoteUser(), chatStatus);
			}

		}
	}

	public void setChatBoxId(String chatBoxId) {
		this.chatBoxId = chatBoxId;
	}

	public String getChatBoxId() {
		return chatBoxId;
	}

	public void setSpaceKey(String spaceKey) {
		this.spaceKey = spaceKey;
	}

	public String getSpaceKey() {
		return spaceKey;
	}

	public void setMouseMove(String mouseMove) {
		this.mouseMove = mouseMove;

		ChatUser chatUser = getChatUser();
		if (chatUser != null
				&& isMouseMoved()) {
			chatUser.setLastMouseMove(new Date());
		}
	}

	public String getMouseMove() {
		return mouseMove;
	}

	public void setLastHeartBeatServerdate(String lastHeartBeatServerdate) {
		this.lastHeartBeatServerdate = lastHeartBeatServerdate;
	}

	public String getLastHeartBeatServerdate() {
		return lastHeartBeatServerdate;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

}
