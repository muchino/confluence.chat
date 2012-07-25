/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author osr
 */
public class ChatConfiguration  implements Serializable{

    private String headerNavigation = "";
    private String header = "";
    private String footer = "";
    private Integer latestDocsMax = 10;
    private String maxListHeight = "300px";
    private String syncPath = "";
    
    private long contentId;
    private List<DropdownField> doctypes = new ArrayList<DropdownField>();
    private List<DropdownField> departmens = new ArrayList<DropdownField>();

    /**
     * @return the headerNavigation
     */
    public String getHeaderNavigation() {
        return headerNavigation;
    }

    /**
     * @param headerNavigation the headerNavigation to set
     */
    public void setHeaderNavigation(String headerNavigation) {
        this.headerNavigation = headerNavigation;
    }

    /**
     * @return the contentId
     */
    public long getContentId() {
        return contentId;
    }

    /**
     * @param contentId the contentId to set
     */
    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the footer
     */
    public String getFooter() {
        return footer;
    }

    /**
     * @param footer the footer to set
     */
    public void setFooter(String footer) {
        this.footer = footer;
    }

    /**
     * @return the doctypes
     */
    public List<DropdownField> getDoctypes() {
        return doctypes;
    }

    /**
     * @param doctypes the doctypes to set
     */
    public void setDoctypes(List<DropdownField> doctypes) {
        this.doctypes = doctypes;
    }

    /**
     * @return the departmens
     */
    public List<DropdownField> getDepartmens() {
        return departmens;
    }

    /**
     * @param departmens the departmens to set
     */
    public void setDepartmens(List<DropdownField> departmens) {
        this.departmens = departmens;
    }

    /**
     * @return the latestDocsMax
     */
    public Integer getLatestDocsMax() {
        return latestDocsMax;
    }

    /**
     * @param latestDocsMax the latestDocsMax to set
     */
    public void setLatestDocsMax(Integer latestDocsMax) {
        this.latestDocsMax = latestDocsMax;
    }

    /**
     * @return the maxListHeight
     */
    public String getMaxListHeight() {
        return maxListHeight;
    }

    /**
     * @param maxListHeight the maxListHeight to set
     */
    public void setMaxListHeight(String maxListHeight) {
        this.maxListHeight = maxListHeight;
    }

    /**
     * @return the syncPath
     */
    public String getSyncPath() {
        return syncPath;
    }

    /**
     * @param syncPath the syncPath to set
     */
    public void setSyncPath(String syncPath) {
        this.syncPath = syncPath;
    }
}
