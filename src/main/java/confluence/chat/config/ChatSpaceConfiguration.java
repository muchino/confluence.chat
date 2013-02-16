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
public class ChatSpaceConfiguration implements Serializable, Configuration {

    private List<String> groups = new ArrayList<String>();
    private Boolean allowAll = false;

    public ChatSpaceConfiguration() {
    }

    /**
     * @return the groups
     */
    @Override
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
    @Override
    public Boolean getAllowAll() {
        return allowAll;
    }

    /**
     * @param allowAll the allowAll to set
     */
    public void setAllowAll(Boolean allowAll) {
        this.allowAll = allowAll;
    }
}
