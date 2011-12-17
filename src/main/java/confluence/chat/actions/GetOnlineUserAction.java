package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.spring.container.ContainerManager;
import confluence.chat.utils.ChatReplyTransformer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOnlineUserAction extends ConfluenceActionSupport implements Beanable {

    private ChatManager chatManager = (ChatManager) ContainerManager.getComponent("chatManager");

    @Override
    public final String execute() throws Exception {
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        Map<String, Object> bean = new HashMap<String, Object>();
        List<Map> chatUserListToMap = ChatReplyTransformer.chatUserListToMap(chatManager.getOnlineUsers());
        bean.put("users", chatUserListToMap);
        return bean;
    }
}
