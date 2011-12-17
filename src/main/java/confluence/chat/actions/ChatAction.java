package confluence.chat.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.webwork.ServletActionContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

public class ChatAction extends ConfluenceActionSupport implements Beanable {

    private static String PARAM_MESSAGE = "message";
    private static String PARAM_CLOSE = "close";
    private static String PARAM_TO = "to";
    private ChatBoxMap chatBoxMap = new ChatBoxMap();
    private ChatManager chatManager = (ChatManager) ContainerManager.getComponent("chatManager");

    public ChatAction() {
    }

    @Override
    public final String execute() throws Exception {


        return SUCCESS;
    }

    public final String start() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        chatBoxMap = chatManager.getOpenChats(session);
        return SUCCESS;
    }

    public final String close() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        chatManager.closeChatWith(session, request.getParameter(PARAM_CLOSE));
        return SUCCESS;
    }

    public final String heartbeat() throws Exception {

        this.chatBoxMap = chatManager.getNewChatBoxesOfUser(getRemoteUser().getName());
        if (!this.chatBoxMap.isEmpty()) {
            chatManager.clearNewMessages(getRemoteUser().getName());
        }
        chatManager.setOnlineStatus(getRemoteUser(), ChatStatus.ONLINE);
        return SUCCESS;
    }

    public final String send() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String message = request.getParameter(ChatAction.PARAM_MESSAGE);
        String to = request.getParameter(ChatAction.PARAM_TO);
        if (StringUtils.isNotEmpty(to)) {
            chatManager.sendMessage(getRemoteUser().getName(), to, message);
        }
        return this.heartbeat();
    }

    @Override
    public Object getBean() {
        HttpServletRequest request = ServletActionContext.getRequest();
        if (!chatBoxMap.isEmpty()) {
            chatManager.saveOpenChats(request.getSession(), chatBoxMap);
        }

        for (int i = 0; i < this.chatBoxMap.size(); i++) {
            ChatBox get = this.chatBoxMap.get(i);
            
        }
        
        Map<String, Object> bean = new HashMap<String, Object>();
        bean.put("username", getRemoteUser().getName());
        bean.put("chatboxes", this.chatBoxMap);
        bean.put("users", chatManager.getOnlineUsers());
        return bean;
    }
}
