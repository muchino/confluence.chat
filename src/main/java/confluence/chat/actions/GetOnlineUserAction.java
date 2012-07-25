package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> bean = new HashMap<String, Object>();
        
        List<Map> chatUserListToMap = getChatReplyTransformer().chatUserListToMap(getRemoteUser() , getChatManager().getOnlineUsers());
        bean.put("users", chatUserListToMap);
        return bean;
    }
}
