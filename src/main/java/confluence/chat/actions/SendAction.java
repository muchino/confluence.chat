package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class SendAction extends AbstractChatAction {

	private String message;
	private String receiver;

	public SendAction(ChatManager chatManager, PageManager pageManager) {
		super(chatManager, pageManager);
	}

	@Override
	public final String execute() throws Exception {
		if (hasChatAccess()) {
			HttpServletRequest request = ServletActionContext.getRequest();
			if (StringUtils.isNotEmpty(receiver)) {
				chatManager.sendMessage(
						getRemoteUser().getName(),
						receiver,
						message);

			}
		}
		return SUCCESS;
	}

	@Override
	public Object getBean() {
		Map<String, Object> bean = new HashMap<>();
		if (hasChatAccess()) {
			return true;
		} else {
			ServletActionContext.getResponse().setStatus(401);
			bean.put("error", "unauthorized");
		}
		return bean;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiver() {
		return receiver;
	}

}
