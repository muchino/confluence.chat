package confluence.chat.config;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.SpaceAdminActionBreadcrumb;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.util.List;

public class ViewSpaceConfigurationAction extends AbstractSpaceAction implements SpaceAware, BreadcrumbAware {

    private String accessGroupsCSV;
    private ChatManager chatManager;

    @Override
    public String execute() throws Exception {
        super.execute();
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
        return this.getAccessGroupsCSV().replaceAll(",", "\n");
    }

    public String getAccessGroupsCSV() {
        if (this.accessGroupsCSV == null) {
            List<String> groups = getChatManager().getChatSpaceConfiguration(getSpaceKey()).getGroups();
            this.accessGroupsCSV = ChatUtils.listToString(groups);
        }
        return this.accessGroupsCSV;
    }

    public Boolean getAllowAllUsers() {
        return getChatManager().getChatSpaceConfiguration(getSpaceKey()).getAllowAll();
    }

    public Boolean getGlobalAllowAll() {
        return getChatManager().getChatConfiguration().getAllowAll();
    }

    public String getGlobalGroups() {
        return ChatUtils.listToString(getChatManager().getChatConfiguration().getGroups());
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        return new SpaceAdminActionBreadcrumb(this, space);
    }

    /**
     * @param chatManager the chatManager to set
     */
    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }
}
