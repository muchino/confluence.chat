package confluence.chat.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import confluence.chat.actions.ChatManager;
import java.util.List;

public class ViewConfigurationAction extends ConfluenceActionSupport {

    private final ChatManager chatManager;
    private String accessGroupsCSV;

    public ViewConfigurationAction(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public String execute() throws Exception {
        this.accessGroupsCSV = "";
        List<String> groups = chatManager.getChatConfiguration().getGroups();
        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            if (i == 0) {
                this.accessGroupsCSV = group;
            } else {
                this.accessGroupsCSV = this.accessGroupsCSV + "," + group;
            }
        }
        return SUCCESS;
    }

    public Boolean getAllowAllUsers() {
        return chatManager.getChatConfiguration().getAllowAll();
    }

    public String getAccessGroupsLines() {
        return this.accessGroupsCSV.replaceAll(",", "\n");
    }

    public String getAccessGroupsCSV() {
        return this.accessGroupsCSV;
    }
}
