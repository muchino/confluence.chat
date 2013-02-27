package confluence.chat.actions;

import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatStatus;
import confluence.chat.model.ChatBoxId;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatBoxMap;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatMessage;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.utils.ChatReplyTransformer;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractChatAction extends ConfluenceActionSupport implements Beanable {

    private static String PARAM_MESSAGE = "message";
    private static String PARAM_LAST_REQUEST = "lr";
    private static String PARAM_MOUSE_MOVE = "mm";
    private static String PARAM_CLOSE = "close";
    private static String PARAM_DELETE = "deleteBox";
    private static String PARAM_TO = "to";
    private static String PARAM_ID = "id";
    private ChatBoxMap chatBoxMap = new ChatBoxMap();
    private ChatManager chatManager;
    private PageManager pageManager;
    private ChatReplyTransformer chatReplyTransformer;
    private PermissionManager permissionManager;
    private Date newRequestDate = null;

    public AbstractChatAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        this.chatManager = chatManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        chatReplyTransformer = new ChatReplyTransformer(pageManager, permissionManager);

    }

    /**
     * Initial muss ich alle checkboxen holen, und mir dann anschauen, ob diese
     * nachrichten haben, die ich brauche (getInitMessagesShowSince)
     *
     * @return
     * @throws Exception
     */
    public final String start() throws Exception {
        if (hasChatAccess()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpSession session = request.getSession();
            getChatManager().setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
            ChatUser chatUser = getChatManager().getChatUser(getRemoteUser());

            if (chatUser.getPreferences().getShowCurrentSite() && chatManager.getChatConfiguration().getShowWhereIam()) {
                String parameterPageId = request.getParameter("pageId");
                if (StringUtils.isNumeric(parameterPageId)) {
                    Long pageId = new Long(parameterPageId);
                    if (getPageManager().getById(pageId) != null) {
                        chatUser.setCurrentSite(pageId);
                    }
                } else {
                    chatUser.setCurrentSite(request.getParameter("currentUrl"), request.getParameter("currentTitle"));
                }

            } else {
                chatUser.removeCurrentSite();
            }

            ChatBoxMap chatBoxes = getChatManager().getChatBoxes(getRemoteUser());

            Iterator<String> iterator = chatBoxes.keySet().iterator();
            while (iterator.hasNext()) {
                String chatBoxId = iterator.next();
                ChatBox chatBox = chatBoxes.get(chatBoxId);
                Date initMessagesShowSince = chatBox.getInitMessagesShowSince(session);
                this.addMessagesSince(chatBox, initMessagesShowSince);
            }
        }
        return SUCCESS;
    }

    /**
     * Add all important messages from given box
     *
     * @param chatBox
     * @param date
     */
    private void addMessagesSince(ChatBox chatBox, Date date) {
        if (chatBox.hasMessageSince(date)) {
            chatBoxMap.getChatBoxById(chatBox.getId()).setLastMessage(chatBox.getLastMessage());
            if (chatBox.isOpen()) {
                chatBoxMap.getChatBoxById(chatBox.getId()).open();
            } else {
                chatBoxMap.getChatBoxById(chatBox.getId()).close();
            }
            ChatMessageList messagesSince = chatBox.getMessagesSince(date);
            Collections.reverse(messagesSince);

            for (int i = 0; i < messagesSince.size(); i++) {
                ChatMessage message = messagesSince.get(i);
                this.setNewRequestDate(message.getSenddate());
                chatBoxMap.getChatBoxById(chatBox.getId()).addMessage(message);
            }
        }
        chatManager.manageHistory(chatBox, AuthenticatedUserThreadLocal.getUser());
    }

    public final String close() throws Exception {
        if (hasChatAccess()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            String parameter = request.getParameter(PARAM_CLOSE);
            if (StringUtils.isNotBlank(parameter)) {
                getChatManager().closeChatBox(getRemoteUser(), new ChatBoxId(parameter));
            }
        }
        return SUCCESS;
    }

    public final Boolean delete() throws Exception {
        if (hasChatAccess()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            String parameter = request.getParameter(PARAM_DELETE);
            if (StringUtils.isNotBlank(parameter)) {
                getChatManager().deleteChatBox(getRemoteUser(), new ChatBoxId(parameter));
                return true;
            }
        }
        return false;
    }

    public final String heartbeat() throws Exception {
        if (hasChatAccess()) {
            ChatUser chatUser = getChatManager().getChatUser(getRemoteUser());
            if (isMouseMoved()) {
                chatUser.setLastMouseMove(new Date());
            }

            // Keine senden, falls user offline
            if (!ChatStatus.OFFLINE.equals(chatUser.getStatus())) {
                getChatManager().setOnlineStatus(getRemoteUser(), ChatStatus.NO_CHANGE);
                ChatBoxMap chatBoxes = getChatManager().getChatBoxes(getRemoteUser());
                Date lastRequestDate = getLastRequestDate();
                Iterator<String> iterator = chatBoxes.keySet().iterator();
                while (iterator.hasNext()) {
                    ChatBox chatBox = chatBoxes.get(iterator.next());
                    this.addMessagesSince(chatBox, lastRequestDate);
                }
            }
        }
        return SUCCESS;
    }

    public final String send() throws Exception {
        if (hasChatAccess()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            String message = request.getParameter(AbstractChatAction.PARAM_MESSAGE);
            String id = request.getParameter(AbstractChatAction.PARAM_ID);
            String to = request.getParameter(AbstractChatAction.PARAM_TO);
            if (StringUtils.isNotEmpty(to)) {
                getChatManager().sendMessage(getRemoteUser().getName(), to, message , id);
            }
        }
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        Map<String, Object> bean = new HashMap<String, Object>();
        if (hasChatAccess()) {
            bean.put("lr", getNewRequestDate().getTime());

            if (!getChatBoxMap().isEmpty()) {
                List<Map> chatboxes = new ArrayList<Map>();
                Iterator<String> iterator = getChatBoxMap().keySet().iterator();
                while (iterator.hasNext()) {
                    chatboxes.add(getChatBoxMap().get(iterator.next()).getJSONMap(getChatManager()));
                }
                bean.put("chatboxes", chatboxes);
            }
        }
        return bean;
    }

    /**
     * @return the chatBoxMap
     */
    public ChatBoxMap getChatBoxMap() {
        if (chatBoxMap == null) {
            chatBoxMap = new ChatBoxMap();
        }
        return chatBoxMap;
    }

    private Date getLastRequestDate() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String lr = request.getParameter(PARAM_LAST_REQUEST);
        Date date = null;
        if (StringUtils.isNumeric(lr)) {
            try {
                date = new Date(new Long(lr));
            } catch (Exception e) {
            }
        }
        if (date == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            date = cal.getTime();
        }
        return date;
    }

    private Boolean isMouseMoved() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return "true".equals(request.getParameter(PARAM_MOUSE_MOVE));
    }

    /**
     * @return the newRequestDate
     */
    public Date getNewRequestDate() {
        if (this.newRequestDate == null) {
            this.newRequestDate = this.getLastRequestDate();
        }
        return newRequestDate;
    }

    /**
     * @param newRequestDate the newRequestDate to set
     */
    public void setNewRequestDate() {
        this.newRequestDate = new Date();
    }

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * @return the pageManager
     */
    public PageManager getPageManager() {
        return pageManager;
    }

    /**
     * @return the chatReplyTransformer
     */
    public ChatReplyTransformer getChatReplyTransformer() {
        return chatReplyTransformer;
    }

    public Boolean hasChatAccess() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return chatManager.hasChatAccess(getRemoteUser(), request.getParameter("spaceKey"));
    }

    private void setNewRequestDate(Date senddate) {
        if (senddate.after(getNewRequestDate())) {
            this.newRequestDate = senddate;
        }
    }

    public List<ChatUser> getOnlineUsers() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return chatManager.getOnlineUsers(request.getParameter("spaceKey"));
    }
}
