/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author oli
 */
public class ChatStartAction extends AbstractChatAction {

    public ChatStartAction(ChatManager chatManager) {
        super(chatManager);
    }

    @Override
    public final String execute() throws Exception {
        super.start();
        return SUCCESS;
    }
}
