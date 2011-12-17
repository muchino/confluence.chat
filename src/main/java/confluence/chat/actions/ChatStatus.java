/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

/**
 *
 * @author Dev
 */
public enum ChatStatus {

    ONLINE, OFFLINE, AWAY, DO_NOT_DISTURB;

    @Override
    public String toString() {
        return this.toString().toLowerCase();

    }
}
