/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.utils;

import confluence.chat.actions.ChatUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatReplyTransformer {

    public static List<Map> chatUserListToMap(List<ChatUser> chatusers) {
        List<Map> list = new ArrayList<Map>();
        for (int i = 0; i < chatusers.size(); i++) {
            list.add(chatusers.get(i).getJSONMap());
        }
        return list;
    }
}
