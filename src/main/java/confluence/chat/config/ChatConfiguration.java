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
public class ChatConfiguration implements Serializable {

    private List<String> groups = new ArrayList<String>();
    private Boolean allowAll = true;
    private Boolean showWhereIam = true;
    private String chatVersionPlain = "0";

    public ChatConfiguration() {
    }

    /**
     * @return the groups
     */
    public List<String> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /**
     * @return the allowAll
     */
    public Boolean getAllowAll() {
        return allowAll;
    }

    /**
     * @param allowAll the allowAll to set
     */
    public void setAllowAll(Boolean allowAll) {
        this.allowAll = allowAll;
    }

    /**
     * @return the showWhereIam
     */
    public Boolean getShowWhereIam() {
        return showWhereIam;
    }

    /**
     * @param showWhereIam the showWhereIam to set
     */
    public void setShowWhereIam(Boolean showWhereIam) {
        this.showWhereIam = showWhereIam;
    }

    /**
     * @return the chatVersionPlain
     */
    public String getChatVersionPlain() {
        return chatVersionPlain;
    }

    /**
     * @param chatVersionPlain the chatVersionPlain to set
     */
    public void setChatVersionPlain(String chatVersionPlain) {
        this.chatVersionPlain = chatVersionPlain;
    }
    }
