/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.utils;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatUserMapComparable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Dev
 */
public class ChatReplyTransformer {

    private PageManager pageManager;
    private PermissionManager permissionManager;
    private String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();

    public ChatReplyTransformer(PageManager pageManager, PermissionManager permissionManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
    }

    public List<ChatUserMapComparable> chatUserListToMap(User user, List<ChatUser> chatusers) {
        List<ChatUserMapComparable> list = new ArrayList<ChatUserMapComparable>();
        for (int i = 0; i < chatusers.size(); i++) {
            ChatUserMapComparable userMap = new ChatUserMapComparable();
            Map<String, String> jsonMap = chatusers.get(i).getJSONMap();

            for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                if (ChatUser.CURRENT_CONTENT_ID.equals(entry.getKey())) {
                    if (StringUtils.isNumeric(entry.getValue())) {
                        ContentEntityObject cO = pageManager.getById(new Long(entry.getValue()));
                        if (cO != null) {
                            if (permissionManager.hasPermission(user, Permission.VIEW, cO)) {
                                userMap.put(ChatUser.CURRENT_SITE_TITLE, cO.getTitle());
                                userMap.put(ChatUser.CURRENT_CONTENT_ID, cO.getIdAsString());
                                userMap.put(ChatUser.CURRENT_SITE_URL, this.baseUrl + cO.getUrlPath());
                            }
                        }
                    }
                } else {
                    userMap.put(entry.getKey(), entry.getValue());
                }
            }
            list.add(userMap);
        }
        return list;
    }
}
