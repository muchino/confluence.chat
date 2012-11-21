package confluence.chat.config;

import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class UpdateSpaceConfigurationAction extends ViewSpaceConfigurationAction {

    private ChatManager chatManager;

    public UpdateSpaceConfigurationAction(ChatManager ChatManager) {
        super(ChatManager);
        this.chatManager = ChatManager;
    }

    @Override
    public String execute() throws Exception {
        super.execute();
        HttpServletRequest request = ServletActionContext.getRequest();
        ChatSpaceConfiguration config = chatManager.getChatSpaceConfiguration(getSpaceKey());
        config.setAllowAll(StringUtils.isNotEmpty(request.getParameter("allowAll")));
        config.setGroups(ChatUtils.stringToList(request.getParameter("groups")));
        chatManager.setChatSpaceConfiguration(config, getSpaceKey());
        return SUCCESS;
    }
}
