/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author oli
 */
public class ChatCloseAction extends AbstractChatAction {

    public ChatCloseAction(ChatManager chatManager) {
        super(chatManager);
    }

    @Override
    public final String execute() throws Exception {
        super.close();
        return SUCCESS;
    }
}
