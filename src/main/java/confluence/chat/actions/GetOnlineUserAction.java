package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.spring.container.ContainerManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOnlineUserAction extends ConfluenceActionSupport implements Beanable {

    private ChatManager chatManager = (ChatManager) ContainerManager.getComponent("chatManager");
    List<ChatUser> onlineUsers;

    public GetOnlineUserAction() {
    }

    @Override
    public final String execute() throws Exception {
        onlineUsers = chatManager.getOnlineUsers();

        return SUCCESS;
    }

    @Override
    public Object getBean() {
        Map<String, Object> bean = new HashMap<String, Object>();
        bean.put("users", onlineUsers);
        return bean;
    }
}
