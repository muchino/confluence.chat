/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatUser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class GetHistoryAction extends AbstractChatAction {

    private static final String PARAM_CHATBOX = "historyUsername";
    private static final String PARAM_DAYS = "days";
    private ChatMessageList messages = new ChatMessageList();
    private ChatUser chatUser = null;
    private Integer days = 65000;
    private DateFormat miuntes = new SimpleDateFormat("yMdkm");
    private String lastWrittenMessageDate = null;
    private Date messagesince = null;
    private String usernameList;

    /**
     * @return the messages
     */
    public ChatMessageList getMessages() {
        return messages;
    }

    public GetHistoryAction(ChatManager chatManager, PageManager pageManager, PermissionManager permissionManager) {
        super(chatManager, pageManager, permissionManager);
    }

    @Override
    public String execute() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
        usernameList = request.getParameter(PARAM_CHATBOX);
        if (StringUtils.isNumeric(request.getParameter(PARAM_DAYS))) {
            try {
                days = new Integer(request.getParameter(PARAM_DAYS));
            } catch (Exception e) {
            }
        }

        messagesince = getSinceDate(days);
        if (StringUtils.isNotBlank(usernameList)) {
            chatUser = getChatManager().getChatUser(usernameList);
            messages = getChatManager().getChatBoxes(getRemoteUser()).getChatBoxWithUser(usernameList).getMessagesSince(getMessagesince());
            Collections.reverse(messages);
        }
        return SUCCESS;
    }

    public static Date getSinceDate(Integer numberOfDays){
    
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.add(Calendar.DATE, -1 * numberOfDays);
        return cal.getTime();
    }
    
    public Map<String, Integer> getChatBoxCount() {
        return getChatManager().getChatBoxCountOfUser(getRemoteUser());
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
}
