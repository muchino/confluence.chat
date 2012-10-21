package confluence.chat.config;

import confluence.chat.manager.ChatManager;

public class ViewConfigurationAction extends AbstractChatConfigAction {

    public ViewConfigurationAction(ChatManager chatManager) {
        super(chatManager);
    }

    @Override
    public String execute() throws Exception {
        return super.execute();
    }

    @Override
    public String getActiveTab() {
        return "chatconfig";
    }
}
