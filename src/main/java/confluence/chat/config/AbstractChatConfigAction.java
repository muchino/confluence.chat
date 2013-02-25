package confluence.chat.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class AbstractChatConfigAction extends ConfluenceActionSupport {

    private final ChatManager chatManager;
    private String accessGroupsCSV;
    private String activeTab;

    public AbstractChatConfigAction(ChatManager chatManager) {
        this.chatManager = chatManager;

    }

    @Override
    public String execute() throws Exception {
        activeTab = getActiveTab();
        this.accessGroupsCSV = getGroups(getChatManager().getChatConfiguration());
        return SUCCESS;
    }

    public String getGroups(Configuration config) {
        return ChatUtils.listToString(config.getGroups());
    }

    public Boolean getAllowAllUsers() {
        return getChatManager().getChatConfiguration().getAllowAll();
    }

    public Boolean getDebugMode() {
        return getChatManager().getChatConfiguration().getDebugMode();
    }

    public Boolean getHideInEditMode() {
        return getChatManager().getChatConfiguration().getHideInEditMode();
    }

    public Boolean getShowWhereIam() {
        return getChatManager().getChatConfiguration().getShowWhereIam();
    }

    public Boolean getPlaySound() {
        return getChatManager().getChatConfiguration().getPlaySound();
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
            Integer count = getChatManager().countChatBoxes(username);
            if (count > 0) {
                boxes.put(username, count);
            }
        }
        return boxes;
    }

    abstract public String getActiveTab();

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }
}
