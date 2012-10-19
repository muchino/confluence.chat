package confluence.chat.actions;

import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatStatus;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.opensymphony.webwork.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class SetStatusAction extends ConfluenceActionSupport implements Beanable {

    private ChatManager chatManager;
    private TransactionTemplate transactionTemplate;

    public SetStatusAction(final ChatManager chatManager, final TransactionTemplate transactionTemplate) {
        this.chatManager = chatManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public final String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String status = request.getParameter("status");
        if (StringUtils.isNotBlank(status)) {
            ChatStatus chatStatus = ChatStatus.ONLINE;
            if ("chat".equals(status)) {
                chatStatus = ChatStatus.ONLINE;
            } else if ("dnd".equals(status)) {
                chatStatus = ChatStatus.DO_NOT_DISTURB;
            } else if ("away".equals(status)) {
                chatStatus = ChatStatus.AWAY;
            } else if ("xa".equals(status)) {
                chatStatus = ChatStatus.OFFLINE;
            }

            final ChatStatus saveChatStatus = chatStatus;
            transactionTemplate.execute(new TransactionCallback() {

                @Override
                public String doInTransaction() {
                    ChatUser chatUser = chatManager.getChatUser(getRemoteUser());
                    chatUser.getPreferences().setChatStatus(saveChatStatus);
                    chatManager.setPreferencesOfUser(chatUser.getUsername(), chatUser.getPreferences());
                    chatManager.setOnlineStatus(getRemoteUser(), saveChatStatus);
                    return SUCCESS;
                }
            });
        }
        return SUCCESS;
    }

    @Override
    public Object getBean() {
        return true;
    }

    public void setSalTransactionTemplate(TransactionTemplate template) {
        this.transactionTemplate = template;
    }
}
