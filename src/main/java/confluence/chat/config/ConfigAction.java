package confluence.chat.config;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import javax.servlet.http.HttpServletRequest;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.actions.ChatManager;
import java.io.File;
import org.apache.commons.lang.StringUtils;

public class ConfigAction extends ConfluenceActionSupport {

    private PageManager pageManager;
    private ChatManager ChatManager;
    private ChatConfiguration config;

    public void setBandanaManager(BandanaManager bandanaManager) {
        
        
    }

    @Override
    public String execute() throws Exception {
        
        return SUCCESS;
    }

    public String save() throws Exception {
        HttpServletRequest req = ServletActionContext.getRequest();
//        String headerNavigation = req.getParameter("ohb_headerNavigation");
//        String doctype = req.getParameter("ohb_doctype");
//        String department = req.getParameter("ohb_department");
//        String header = req.getParameter("ohb_header");
//        String footer = req.getParameter("ohb_footer");
//        String latestDocsMax = req.getParameter("ohb_latestDocsMax");
//        String maxListHeight = req.getParameter("ohb_maxListHeight");
//        String syncPath = req.getParameter("ohb_syncPath");
//        System.out.println(syncPath);
//        boolean save = false;
//        if (headerNavigation != null) {
//            config.setHeaderNavigation(headerNavigation);
//            save = true;
//        }
//
//        if (doctype != null) {
//            config.setDoctypes(ChatManager.parseDropDownString(doctype));
//            save = true;
//        }
//
//        if (department != null) {
//            config.setDepartmens(ChatManager.parseDropDownString(department));
//            save = true;
//        }
//        if (StringUtils.isNumeric(latestDocsMax)) {
//            config.setLatestDocsMax(new Integer(latestDocsMax));
//            save = true;
//        }
//        if (maxListHeight != null) {
//            config.setMaxListHeight(maxListHeight);
//        }
//
//        if (footer != null) {
//            config.setFooter(footer);
//            save = true;
//        }
//
//        if (header != null) {
//            config.setHeader(header);
//            save = true;
//        }
//        if (syncPath != null) {
//            if (ChatManager.isValidPath(syncPath)) {
//                config.setSyncPath(syncPath);
//                save = true;
//            }
//        }
//
//        String contentId = req.getParameter("contentId");
//        if (StringUtils.isNumeric(contentId)) {
//            try {
//                Long aLong = new Long(contentId);
//                Page page = getPage(aLong);
//                if (page != null) {
//                    config.setContentId(aLong);
//                    save = true;
//                } else {
//                    addActionError(getText("ohb.error.pagenotexists"));
//                    return INPUT;
//                }
//            } catch (Exception e) {
//                log.info("invalid content id", e);
//            }
//
//
//        }
//        if (save) {
//            addActionMessage(getText("ohb.saved"));
//            ChatManager.saveConfig(config);
//        }
        return execute();
    }

    /**
     * @return the pageManager
     */
    public PageManager getPageManager() {
        return pageManager;
    }

    /**
     * @param pageManager the pageManager to set
     */
    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public Page getPage(long id) {

        return pageManager.getPage(id);

    }

    /**
     * @return the config
     */
    public ChatConfiguration getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(ChatConfiguration config) {
        this.config = config;
    }
}
