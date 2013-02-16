/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import java.util.List;

/**
 *
 * @author oli
 */
public interface Configuration {

    /**
     * @return the allowAll
     */
    Boolean getAllowAll();

    /**
     * @return the groups
     */
    List<String> getGroups();
    
}
