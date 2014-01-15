/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatUser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class GetUserHistoryAction extends AbstractUserProfileAction implements UserAware {

    private static final String PARAM_CHATBOX = "historyUsername";
    private static final String PARAM_DAYS = "days";
    private ChatMessageList messages = new ChatMessageList();
    private ChatUser chatUser = null;
    private Integer days = 7;
    private DateFormat miuntes = new SimpleDateFormat("yMdkm");
    private String lastWrittenMessageDate = null;
    private Date messagesince = null;
    private String usernameList;
    private ChatManager chatManager;
    private PaginationSupport paginationSupport;
    private Integer startIndex = 0;

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


        if (StringUtils.isNotBlank(usernameList)) {
            messagesince = GetHistoryAjaxAction.getSinceDate(getDays());
            chatUser = getChatManager().getChatUser(usernameList);
            if (getDays() > 0) {
                messages = getChatManager().getChatBoxes(getRemoteUser()).getChatBoxWithUser(usernameList).getMessagesSince(getMessagesince());
                Collections.reverse(messages);
            } else {
                messages = getChatManager().getChatBoxes(getRemoteUser()).getChatBoxWithUser(usernameList).getMessages();
            }

        } else {
            paginationSupport = new PaginationSupport(getChatManager().getKeysOfChats(getRemoteUser()), 10);
            paginationSupport.setStartIndex(startIndex);
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

    /**
     * @return the days
     */
    public Integer getDays() {
        return days;
    }

    /**
     * @return the paginationSupport
     */
    public PaginationSupport getPaginationSupport() {
        return paginationSupport;
    }

    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
}
