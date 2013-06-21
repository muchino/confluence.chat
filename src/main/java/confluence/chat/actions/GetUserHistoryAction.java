/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatUser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class GetUserHistoryAction extends AbstractUsersAction implements UserAware {

    private static final String PARAM_CHATBOX = "historyUsername";
    private static final String PARAM_DAYS = "days";
    private ChatMessageList messages = new ChatMessageList();
    private ChatUser chatUser = null;
    private Integer days = 1;
    private DateFormat miuntes = new SimpleDateFormat("yMdkm");
    private String lastWrittenMessageDate = null;
    private Date messagesince = null;
    private String usernameList;
    private ChatManager chatManager;

    /**
     * @return the messages
     */
    public ChatMessageList getMessages() {
        return messages;
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public String execute() throws Exception {
        super.execute();
        HttpServletRequest request = ServletActionContext.getRequest();
        usernameList = request.getParameter(PARAM_CHATBOX);
        if (StringUtils.isNumeric(request.getParameter(PARAM_DAYS))) {
            try {
                days = new Integer(request.getParameter(PARAM_DAYS));
            } catch (Exception e) {
            }
        }

        messagesince = GetHistoryAction.getSinceDate(days);
        if (StringUtils.isNotBlank(usernameList)) {
            chatUser = getChatManager().getChatUser(usernameList);
            messages = getChatManager().getChatBoxes(getRemoteUser()).getChatBoxWithUser(usernameList).getMessagesSince(getMessagesince());
            Collections.reverse(messages);
        }
        return SUCCESS;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public User getUser() {
        return getRemoteUser();
    }

    @Override
    public boolean isUserRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    /**
     * @return the chatUser
     */
    public ChatUser getChatUser() {
        return chatUser;
    }

    public boolean writeNewLine(Date newDate) {
        String formated = miuntes.format(newDate);
        boolean isSame = false;
        if (lastWrittenMessageDate != null) {
            isSame = lastWrittenMessageDate.equals(formated);

        }

        lastWrittenMessageDate = formated;
        return !isSame;
    }

    /**
     * @return the messagesince
     */
    public Date getMessagesince() {
        return messagesince;
    }

    public Map<String, Integer> getChatBoxCount() {
        return getChatManager().getChatBoxCountOfUser(getRemoteUser());
    }

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * @param chatManager the chatManager to set
     */
    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }
}
