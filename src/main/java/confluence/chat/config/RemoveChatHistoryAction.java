package confluence.chat.config;

import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class RemoveChatHistoryAction extends AbstractChatConfigAction {

    @Override
    public String execute() throws Exception {
        super.execute();
        HttpServletRequest request = ServletActionContext.getRequest();
        String username = request.getParameter("username");
        if (StringUtils.isNotBlank(username)) {
            User user = userAccessor.getUser(username);
            if (user != null) {
                try {
                    getChatManager().deleteChatBoxesOfUser(user);    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        }
        return SUCCESS;
    }
}
