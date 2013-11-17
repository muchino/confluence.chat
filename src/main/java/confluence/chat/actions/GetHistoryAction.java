/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatBox;
import confluence.chat.model.ChatMessageList;
import confluence.chat.model.ChatUser;
import confluence.chat.utils.ChatUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class GetHistoryAction extends AbstractChatAction {

    private static final String PARAM_CHATBOX = "chatBoxId";
    private static final String PARAM_DAYS = "days";
    private ChatMessageList messages = new ChatMessageList();
    private ChatUser chatUser = null;
    private Integer days = 7;
    private DateFormat miuntes = new SimpleDateFormat("yMdkm");
    private String lastWrittenMessageDate = null;
    private Date messagesince = null;
    private String chatBoxId;

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
        chatBoxId = request.getParameter(PARAM_CHATBOX);
        if (StringUtils.isNumeric(request.getParameter(PARAM_DAYS))) {
            try {
                days = new Integer(request.getParameter(PARAM_DAYS));
            } catch (Exception e) {
            }
        }

        messagesince = GetHistoryAction.getSinceDate(days);
        if (StringUtils.isNotBlank(chatBoxId)) {
            ChatBox box = getChatManager().getChatBoxes(getRemoteUser()).getChatBoxByStringId(chatBoxId);
            if (days > 0) {
                messages = box.getMessagesSince(getMessagesince());
                Collections.reverse(messages);
            } else {
                messages = box.getMessages();
            }
            List<String> userKeyMembers = box.getUserKeyMembers();
            for (int i = 0; i < userKeyMembers.size(); i++) {
                String userKey = userKeyMembers.get(i);
                String userName = ChatUtils.getUserNameByKeyOrUserName(userKey);
                chatUser = getChatManager().getChatUser(userName);
                break;
            }
        }
        return SUCCESS;
    }

    public static Date getSinceDate(Integer numberOfDays) {

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
