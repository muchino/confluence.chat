/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.io.Serializable;

/**
 *
 * @author Dev
 */
public enum ChatStatus implements Serializable{

    ONLINE, OFFLINE, AWAY, DO_NOT_DISTURB, NO_CHANGE;

    @Override
    public String toString() {
        switch (this) {
            case ONLINE:
                return "chat";
            case AWAY:
                return "away";
            case DO_NOT_DISTURB:
                return "dnd";
            case NO_CHANGE:
                return "no change";                
            default:
                return "xa";
        }
    }
}
