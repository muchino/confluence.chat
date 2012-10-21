package confluence.chat.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import confluence.chat.manager.ChatManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class AbstractChatConfigAction extends ConfluenceActionSupport {

    private final ChatManager chatManager;
    private String accessGroupsCSV;
    private String activeTab ;

    public AbstractChatConfigAction(ChatManager chatManager) {
        this.chatManager = chatManager;
        
    }

    @Override
    public String execute() throws Exception {
        activeTab = getActiveTab();
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

    public Boolean getDebugMode() {
        return chatManager.getChatConfiguration().getDebugMode();
    }

    public Boolean getHideInEditMode() {
        return chatManager.getChatConfiguration().getHideInEditMode();
    }

    public Boolean getShowWhereIam() {
        return chatManager.getChatConfiguration().getShowWhereIam();
    }

    public Boolean getPlaySound() {
        return chatManager.getChatConfiguration().getShowWhereIam();
    }

    public String getAccessGroupsLines() {
        return this.accessGroupsCSV.replaceAll(",", "\n");
    }

    public String getAccessGroupsCSV() {
        return this.accessGroupsCSV;
    }

    public Map<String, Integer> getChatBoxes() {
        Map<String, Integer> boxes = new TreeMap<String, Integer>();
        Iterator<String> iterator = userAccessor.getUserNames().iterator();
        while (iterator.hasNext()) {
            String username = iterator.next();
            Integer count = chatManager.countChatBoxes(username);
            if (count > 0) {
                boxes.put(username, count);
            }
        }
        return boxes;
    }
    
    abstract public String getActiveTab();
    
}
