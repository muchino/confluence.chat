/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.user.User;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBoxId;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatUser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author oli
 */
public class GetUserHistoryAction extends AbstractUserProfileAction implements UserAware {

	private static final Logger logger = Logger.getLogger(GetUserHistoryAction.class);
	private ChatMessageList messages = new ChatMessageList();
	private ChatUser chatUser = null;
	private Integer days = 7;
	private final DateFormat miuntes = new SimpleDateFormat("yMdkm");
	private String lastWrittenMessageDate = null;
	private Date messagesince = null;
	private String historyUsername;
	private ChatManager chatManager;
	private PaginationSupport paginationSupport;
	private Integer startIndex = 0;
	private String username;

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String execute() throws Exception {
		super.execute();
		User currentUser = AuthenticatedUserThreadLocal.getUser();
		boolean hasPermission = permissionManager.hasPermission(
				currentUser, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
		if (!(currentUser == getUser() || hasPermission)) {
			return NONE;
		}

		if (StringUtils.isNotBlank(historyUsername)) {
			messagesince = GetHistoryAjaxAction.getSinceDate(getDays());
			chatUser = getChatManager().getChatUser(historyUsername);
			if (getDays() > 0) {
				messages = getChatManager().getChatBoxes(getUser()).getChatBoxWithUser(historyUsername).getMessagesSince(getMessagesince());
				Collections.reverse(messages);
			} else {
				messages = getChatManager().getChatBoxes(getUser()).getChatBoxWithUser(historyUsername).getMessages();
			}

		} else {
			paginationSupport = new PaginationSupport(getChatManager().getKeysOfChats(getUser()), 10);
			paginationSupport.setStartIndex(startIndex);
		}
		return SUCCESS;
	}

	public String deleteBox() {
		if (StringUtils.isNotBlank(historyUsername)) {
			User historyUser = userAccessor.getUser(historyUsername);
			ChatBoxId chatBoxId = new ChatBoxId(historyUser);
			logger.debug(chatBoxId);
			chatManager.deleteChatBox(getRemoteUser(), chatBoxId.toString());
		}
		return SUCCESS;
	}

	@Override
	public User getUser() {
		return userAccessor.getUser(getUsername());
	}

	/**
	 * @return the chatUser
	 */
	public ChatUser getChatUser() {
		return chatUser;
	}

	public boolean writeNewLine(Date newDate) {
		String formated = miuntes.format(newDate);
		boolean isSame = false;
		if (lastWrittenMessageDate != null) {
			isSame = lastWrittenMessageDate.equals(formated);

		}

		lastWrittenMessageDate = formated;
		return !isSame;
	}

	/**
	 * @return the messagesince
	 */
	public Date getMessagesince() {
		return messagesince;
	}

	/**
	 * @return the chatManager
	 */
	public ChatManager getChatManager() {
		return chatManager;
	}

	/**
	 * @param chatManager the chatManager to set
	 */
	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

	public PaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getDays() {
		return days;
	}

	public void setHistoryUsername(String historyUsername) {
		this.historyUsername = historyUsername;
	}

	public String getHistoryUsername() {
		return historyUsername;
	}

	@Override
	public String getUsername() {
		if (StringUtils.isBlank(username)) {
			username = getRemoteUser().getName();
		}
		return username;
	}

	/**
	 * @return the messages
	 */
	public ChatMessageList getMessages() {
		return messages;
	}

}
