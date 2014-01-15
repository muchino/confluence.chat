package confluence.chat.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.util.List;

abstract class AbstractChatConfigAction extends ConfluenceActionSupport {

    private ChatManager chatManager;
    private String accessGroupsCSV;

    @Override
    public String execute() throws Exception {
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

        return this.getAccessGroupsCSV().replaceAll(",", "\n");
    }

    public Integer getHeartBeat() {
        return getChatManager().getChatConfiguration().getHeartBeat();
    }

    public String getAccessGroupsCSV() {

        if (this.accessGroupsCSV == null) {
            this.accessGroupsCSV = getGroups(getChatManager().getChatConfiguration());
        }

        return this.accessGroupsCSV;
    }

    public List<String> getKeysOfChats() {
        return chatManager.getKeysOfChats(null);
    }

    /**
     * @return the chatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * @param chatManager the chatManager to set
     */
    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }
}
