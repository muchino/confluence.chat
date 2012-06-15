/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author oli
 */
public class HeartbeatAction extends AbstractChatAction {

    public HeartbeatAction(ChatManager chatManager) {
        super(chatManager);
    }

    @Override
    public final String execute() throws Exception {
        super.heartbeat();
        return SUCCESS;
    }
}
