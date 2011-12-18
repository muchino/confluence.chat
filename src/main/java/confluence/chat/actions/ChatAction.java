package confluence.chat.actions;

import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.webwork.ServletActionContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

public class ChatAction extends AbstractChatAction {

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
        chatManager.setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
        return SUCCESS;
    }

    public final String close() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        chatManager.closeChatWith(session, request.getParameter(PARAM_CLOSE));
        return SUCCESS;
    }

    public final String heartbeat() throws Exception {
        if (getRemoteUser() != null) {
            ChatUser chatUser = chatManager.getChatUser(getRemoteUser());
            // Keine senden, falls user offline
            if (!ChatStatus.OFFLINE.equals(chatUser.getStatus())) {
                this.chatBoxMap = chatManager.getNewChatBoxesOfUser(getRemoteUser().getName());
                if (!this.chatBoxMap.isEmpty()) {
                    chatManager.clearNewMessages(getRemoteUser().getName());
                }
                chatManager.setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
            }
        }
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
        Map<String, Object> bean = new HashMap<String, Object>();

        if (getRemoteUser() != null) {
            HttpServletRequest request = ServletActionContext.getRequest();
            ChatUser chatUser = chatManager.getChatUser(getRemoteUser());
            bean.put("user", chatUser.getJSONMap());
            if (!chatBoxMap.isEmpty()) {
                chatManager.saveOpenChats(request.getSession(), chatBoxMap);
                List<Map> chatboxes = new ArrayList<Map>();
                Iterator<String> iterator = chatBoxMap.keySet().iterator();
                while (iterator.hasNext()) {
                    chatboxes.add(chatBoxMap.get(iterator.next()).getJSONMap());
                }
                bean.put("chatboxes", chatboxes);
            }
        }
        return bean;
    }
}
