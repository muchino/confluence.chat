package confluence.chat.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import javax.servlet.http.HttpServletRequest;

public class ChatBarAction extends ConfluenceActionSupport {

    private final ChatManager chatManager;

    public ChatBarAction(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public final String execute() throws Exception {
        return SUCCESS;
    }

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    public Boolean hasChatAccess() {

        HttpServletRequest request = ServletActionContext.getRequest();


        return chatManager.hasChatAccess(getRemoteUser(), request.getParameter("spaceKey"));
    }
}