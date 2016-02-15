/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import static com.opensymphony.xwork.Action.SUCCESS;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatUser;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class ChatStartAction extends AbstractChatAction {

	public ChatStartAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
		super(chatManager, pageManager, permissionManager);
	}

	/**
	 * Initial muss ich alle checkboxen holen, und mir dann anschauen, ob diese
	 * nachrichten haben, die ich brauche (getInitMessagesShowSince)
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	public final String execute() throws Exception {
		if (hasChatAccess()) {
			HttpServletRequest request = ServletActionContext.getRequest();
			chatManager.setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
			ChatUser chatUser = chatManager.getChatUser(getRemoteUser());
			if (chatUser != null) {
				if (chatUser.getPreferences().getShowCurrentSite() && chatManager.getChatConfiguration().getShowWhereIam()) {
					String parameterPageId = request.getParameter("pageId");
					if (StringUtils.isNumeric(parameterPageId) && StringUtils.isNotBlank(parameterPageId)) {
						Long pageId = new Long(parameterPageId);
						if (pageManager.getById(pageId) != null) {
							chatUser.setCurrentSite(pageId);
						}
					} else {
						chatUser.setCurrentSite(request.getParameter("currentUrl"), request.getParameter("currentTitle"));
					}
				} else {
					chatUser.removeCurrentSite();
				}
			}
			ChatBoxMap chatBoxes = chatManager.getChatBoxes(getRemoteUser());
			Date lastRequestDate = getLastRequestDate();
			Iterator<String> iterator = chatBoxes.keySet().iterator();
			while (iterator.hasNext()) {
				String chatBoxId = iterator.next();
				ChatBox chatBox = chatBoxes.get(chatBoxId);
				this.addMessagesSince(chatBox, lastRequestDate);
			}
		}
		return SUCCESS;
	}
}
