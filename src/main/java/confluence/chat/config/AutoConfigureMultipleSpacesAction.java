/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
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



    @Override
    public String execute() throws Exception {

        int configured = 0;
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request.getParameterValues("space") != null) {
            boolean allowAll = StringUtils.isNotEmpty(request.getParameter("allowAll"));
            boolean overwriteAllowAll = StringUtils.isNotEmpty(request.getParameter("overwriteAllowAll"));
//            boolean overwriteGroups = StringUtils.isNotEmpty(request.getParameter("overwriteGroups"));
            boolean appendGroups = StringUtils.isNotEmpty(request.getParameter("appendGroups"));

            List<String> groups = ChatUtils.stringToList(request.getParameter("groups"));
            String[] spaces = request.getParameterValues("space");
            for (int i = 0; i < spaces.length; i++) {
                Space space = spaceManager.getSpace(spaces[i]);
                if (space != null) {
                    configured++;
                    ChatSpaceConfiguration config = getChatManager().getChatSpaceConfiguration(space.getKey());
                    if (overwriteAllowAll) {
                        config.setAllowAll(allowAll);
                    }
                    if (appendGroups) {
                        for (int j = 0; j < groups.size(); j++) {
                            String group = groups.get(j);
                            if (!config.getGroups().contains(group)) {
                                config.getGroups().add(group);
                            }
                        }

                    } else {
                        config.setGroups(groups);
                    }
                    getChatManager().setChatSpaceConfiguration(config, space.getKey());
                }
            }
        }
        if (configured > 0) {
            addActionMessage(getText("chat.config.import.success", Arrays.asList(configured)));
        } else {
            addActionError(getText("chat.config.import.error"));
        }
        return super.execute();
    }

    @Override
    public String getActiveTab() {
        return "spaceimporter";
    }

    /**
     * @param spaceManager the spaceManager to set
     */
    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}
