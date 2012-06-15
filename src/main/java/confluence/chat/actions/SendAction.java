/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author oli
 */
public class SendAction extends AbstractChatAction {

    public SendAction(ChatManager chatManager) {
        super(chatManager);
    }

    @Override
    public final String execute() throws Exception {
        super.send();
        return SUCCESS;
    }
}
