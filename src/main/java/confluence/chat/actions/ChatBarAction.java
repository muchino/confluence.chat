package confluence.chat.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;

public class ChatBarAction extends ConfluenceActionSupport {

    private ChatManager chatManager;

    public ChatBarAction(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public final String execute() throws Exception {
        return SUCCESS;
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

    public Boolean hasChatAccess() {
        return chatManager.hasChatAccess(getRemoteUser());
    }
}