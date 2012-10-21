package confluence.chat.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import java.util.List;
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
        ChatConfiguration chatConfiguration = ChatManager.getChatConfiguration();
        chatConfiguration.setAllowAll(StringUtils.isNotEmpty(request.getParameter("allowAll")));
        chatConfiguration.setShowWhereIam(StringUtils.isNotEmpty(request.getParameter("showWhereIam")));
        
        chatConfiguration.setDebugMode(StringUtils.isNotEmpty(request.getParameter("debugMode")));
        chatConfiguration.setHideInEditMode(StringUtils.isNotEmpty(request.getParameter("hideInEditMode")));
        chatConfiguration.setPlaySound(StringUtils.isNotEmpty(request.getParameter("playSound")));
        
        List<String> groups = chatConfiguration.getGroups();
        groups.clear();
        String groupsParam = request.getParameter("groups");
        if(StringUtils.isNotEmpty(groupsParam)){
            String[] groupsSlit = groupsParam.split("\n");
            for (int i = 0; i < groupsSlit.length; i++) {
                groups.add(groupsSlit[i].trim());
            }
        }
        ChatManager.setChatConfiguration(chatConfiguration);
        return SUCCESS;
    }
}
