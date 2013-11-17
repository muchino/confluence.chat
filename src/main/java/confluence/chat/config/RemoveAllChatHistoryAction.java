package confluence.chat.config;

public class RemoveAllChatHistoryAction extends AbstractChatConfigAction {

    @Override
    public String execute() throws Exception {
        super.execute();
        getChatManager().deleteAllMessages();
        return SUCCESS;
    }

    @Override
    public String getActiveTab() {
        return "history";
    }
}
