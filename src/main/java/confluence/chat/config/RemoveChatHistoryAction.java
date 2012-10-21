package confluence.chat.config;

import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxId;
import confluence.chat.model.ChatBoxMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class RemoveChatHistoryAction extends AbstractChatConfigAction {

    private final ChatManager chatManager;

    public RemoveChatHistoryAction(ChatManager chatManager) {
        super(chatManager);
        this.chatManager = chatManager;
    }

    @Override
    public String execute() throws Exception {
        super.execute();
        HttpServletRequest request = ServletActionContext.getRequest();
        String username = request.getParameter("username");
        if (StringUtils.isNotBlank(username)) {
            User user = userAccessor.getUser(username);
            if (user != null) {
                this.deleteChatBoxesOfUser(user);
            }
        }
        return SUCCESS;
    }

    private void deleteChatBoxesOfUser(User user) {
        List<ChatBoxId> removeableIds = new ArrayList<ChatBoxId>();
        ChatBoxMap chatBoxes = chatManager.getChatBoxes(user);
        Iterator<String> iterator = chatBoxes.keySet().iterator();
        while (iterator.hasNext()) {
            ChatBox get = chatBoxes.get(iterator.next());
            removeableIds.add(get.getId());
        }
        for (int i = 0; i < removeableIds.size(); i++) {
            chatManager.deleteChatBox(user, removeableIds.get(i));
        }
    }

    @Override
    public String getActiveTab() {
        return "history";
    }
}
