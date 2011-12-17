/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.utils;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.user.User;
import confluence.chat.actions.ChatBox;
import confluence.chat.actions.ChatMessage;
import confluence.chat.actions.ChatUser;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static Map chatBoxToMap(ChatBox chatBox) {
        return new HashMap();
    }

    public static Map chatMessageToMap(ChatMessage chatMessage, DateFormatter formatter) {
        return new HashMap();
    }
}
