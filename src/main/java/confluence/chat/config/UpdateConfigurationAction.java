package confluence.chat.config;

import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class UpdateConfigurationAction extends ViewConfigurationAction {

    private ChatManager ChatManager;

    public UpdateConfigurationAction(ChatManager ChatManager) {
        super(ChatManager);
        this.ChatManager = ChatManager;
    }

    @Override
    public String execute() throws Exception {
        super.execute();
        HttpServletRequest request = ServletActionContext.getRequest();
        ChatConfiguration config = ChatManager.getChatConfiguration();
        config.setAllowAll(StringUtils.isNotEmpty(request.getParameter("allowAll")));
        config.setShowWhereIam(StringUtils.isNotEmpty(request.getParameter("showWhereIam")));

        config.setDebugMode(StringUtils.isNotEmpty(request.getParameter("debugMode")));
        config.setHideInEditMode(StringUtils.isNotEmpty(request.getParameter("hideInEditMode")));
        config.setPlaySound(StringUtils.isNotEmpty(request.getParameter("playSound")));
        config.setGroups(ChatUtils.stringToList(request.getParameter("groups")));
        ChatManager.setChatConfiguration(config);
        return SUCCESS;
    }
}
