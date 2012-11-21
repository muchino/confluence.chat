package confluence.chat.config;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.util.List;

public class ViewSpaceConfigurationAction extends AbstractSpaceAction implements SpaceAware {
    
    private String accessGroupsCSV;
    private ChatManager chatManager;
    
    public ViewSpaceConfigurationAction(ChatManager chatManager) {
        this.chatManager = chatManager;
        
    }
    
    @Override
    public String execute() throws Exception {
        super.execute();
        this.accessGroupsCSV = "";
        List<String> groups = chatManager.getChatSpaceConfiguration(getSpaceKey()).getGroups();
        this.accessGroupsCSV = ChatUtils.listToString(groups);
        return SUCCESS;
    }
    
    @Override
    public boolean isSpaceRequired() {
        return true;
    }
    
    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
    
    public String getAccessGroupsLines() {
        return this.accessGroupsCSV.replaceAll(",", "\n");
    }
    
    public String getAccessGroupsCSV() {
        return this.accessGroupsCSV;
    }
    
    public Boolean getAllowAllUsers() {
        return chatManager.getChatSpaceConfiguration(getSpaceKey()).getAllowAll();
    }
}
