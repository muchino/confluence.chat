/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.actions.ChatBoxId;
import confluence.chat.actions.ChatManager;
import confluence.chat.actions.ChatUser;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class ChatUserLink implements Macro {

    private ChatManager chatManager;
    private UserManager userManager;
    private static final String PARAM_USERNAME = "username";

    public ChatUserLink(ChatManager chatManager, UserManager userManager) {
        this.chatManager = chatManager;
        this.userManager = userManager;
    }

    @Override
    public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
        StringBuilder builder = new StringBuilder();
        String username = parameters.get(PARAM_USERNAME);
        if (StringUtils.isNotBlank(username)) {
            try {
                User user = userManager.getUser(username);
                if (user != null) {
                    String status;
                    ChatUser chatUser = chatManager.getChatUser(user);
                    if (chatUser != null) {
                        status = chatUser.getJSONMap().get(ChatUser.STATUS);
                        ChatBoxId chatBoxId = new ChatBoxId(chatUser);
                        builder.append("<span class=\"chatuser-link-holder\" chatboxId=\"");
                        builder.append(chatBoxId.toString());
                        builder.append("\">");
                        builder.append("<span class=\"");
                        builder.append(status);
                        builder.append("\"> <a href=\"");
                        builder.append(GeneralUtil.getGlobalSettings().getBaseUrl());
                        HttpServletRequest request = ServletActionContext.getRequest();
                        if (request != null) {
                            builder.append(request.getContextPath());
                        }
                        builder.append("/display/~").append(user.getName());
                        builder.append("\" class=\"userLogoLink chatuser-link\" data-username=\"");
                        builder.append(user.getName()).append("\" ");
                        builder.append(" chatboxId=\"");
                        builder.append(chatBoxId.toString());
                        builder.append("\">");
                        builder.append(user.getFullName());
                        builder.append("</a></span></span>");
                    }
                }
            } catch (EntityException ex) {
                Logger.getLogger(ChatUserLink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return builder.toString();


    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.INLINE;
    }
}
