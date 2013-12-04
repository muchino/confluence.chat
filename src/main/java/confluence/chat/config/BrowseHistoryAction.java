/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.user.User;
import confluence.chat.manager.ChatManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oli
 */
public class BrowseHistoryAction extends SearchUsersAction {

    private ChatManager chatManager;
    private PaginationSupport paginationSupportNew;

    @Override
    public String doUserSearch() {
        super.doUserSearch();
        List chatResults = new ArrayList();
        List results = super.getPaginationSupport().getItems();
        for (int i = 0; i < results.size(); i++) {
            Object object = results.get(i);
            if (object instanceof User) {
                List<String> usersWithChats = chatManager.getUsersWithChats((User) object);
                if (!usersWithChats.isEmpty()) {
                    chatResults.add(object);
                }
            }

        }
        paginationSupportNew = new PaginationSupport(chatResults, 10);
        paginationSupportNew.setStartIndex(getStartIndex());
        return SUCCESS;
    }

    /**
     * @param chatManager the chatManager to set
     */
    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    /**
     * @return the paginationSupportNew
     */
    @Override
    public PaginationSupport getPaginationSupport() {
        return paginationSupportNew;
    }
}
