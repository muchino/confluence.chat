package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class GetOnlineUserAction extends AbstractChatAction {

    public GetOnlineUserAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        super(chatManager, pageManager, permissionManager);
    }

    @Override
    public final String execute() throws Exception {
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> bean = new HashMap<String, Object>();
        bean.put("users", getChatReplyTransformer().chatUserListToMap(getRemoteUser(), getChatManager().getOnlineUsers(request.getParameter("spaceKey"))));
        return bean;
    }
}
