package confluence.chat.config;

import confluence.chat.manager.ChatManager;

public class RemoveAllChatHistoryAction extends AbstractChatConfigAction {

    private final ChatManager chatManager;

    public RemoveAllChatHistoryAction(ChatManager chatManager) {
        super(chatManager);
        this.chatManager = chatManager;
    }

    @Override
    public String execute() throws Exception {
        super.execute();
        chatManager.deleteAllMessages();
        return SUCCESS;
    }

    @Override
    public String getActiveTab() {
        return "history";
    }
}
