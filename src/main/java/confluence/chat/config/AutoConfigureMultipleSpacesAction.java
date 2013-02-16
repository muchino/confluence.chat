/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.xwork.RequireSecurityToken;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author oli
 */
public class AutoConfigureMultipleSpacesAction extends ViewConfigurationAction {

    private SpaceManager spaceManager;

    public AutoConfigureMultipleSpacesAction(SpaceManager spaceManager, ChatManager chatManager) {
        super(chatManager);
        this.spaceManager = spaceManager;
    }

    
    @Override
    public String execute() throws Exception {

        int configured = 0;

        HttpServletRequest request = ServletActionContext.getRequest();
        if (request.getParameterValues("space") != null) {
            boolean allowAll = StringUtils.isNotEmpty(request.getParameter("allowAll"));
            List<String> groups = ChatUtils.stringToList(request.getParameter("groups"));
            String[] spaces = request.getParameterValues("space");
            for (int i = 0; i < spaces.length; i++) {
                Space space = spaceManager.getSpace(spaces[i]);
                if (space != null) {
                    configured++;
                    ChatSpaceConfiguration config = getChatManager().getChatSpaceConfiguration(space.getKey());
                    config.setAllowAll(allowAll);
                    config.setGroups(groups);
                    getChatManager().setChatSpaceConfiguration(config, space.getKey());
                }
            }

        }
        if (configured > 0) {
            addActionMessage(getText("chat.config.import.success", Arrays.asList(configured)));
        } else {
            addActionError(getText("chat.config.import.nospace"));
        }
        return super.execute();
    }

    @Override
    public String getActiveTab() {
        return "tools";
    }
}
