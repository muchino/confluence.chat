/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.model;

import java.util.HashMap;
import org.apache.commons.lang.builder.CompareToBuilder;

/**
 *
 * @author oli
 */
public class ChatUserMapComparable extends HashMap<String, String>
        implements Comparable<ChatUserMapComparable> {

    private boolean ascending = true;

    @Override
    public int compareTo(ChatUserMapComparable bar) {
        int result;
        if (bar == null || !(bar instanceof ChatUserMapComparable)) {
            result = -1;
        }
        ChatUserMapComparable _rhs = (ChatUserMapComparable) bar;
        result = new CompareToBuilder().append( _rhs.get(ChatUser.FULLNAME).toLowerCase(), get(ChatUser.FULLNAME).toLowerCase()).toComparison();

        return (ascending ? result : -result);
    }

    public void setAscending(boolean asc) {
        ascending = asc;
    }
}
