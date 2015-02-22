/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.config;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.core.db.JDBCUtils;
import com.atlassian.hibernate.PluginHibernateSessionFactory;
import com.atlassian.user.User;
import confluence.chat.manager.ChatManager;
import confluence.chat.utils.ChatUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.sf.hibernate.HibernateException;

/**
 *
 * @author oli
 */
public class BrowseHistoryAction extends SearchUsersAction {

	private PaginationSupport paginationSupportNew;
	private PluginHibernateSessionFactory pluginHibernateSessionFactory;

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setPluginHibernateSessionFactory(PluginHibernateSessionFactory sessionFactory) {
		this.pluginHibernateSessionFactory = sessionFactory;
	}

	@Override
	public String doUserSearch() {
		super.doUserSearch();
		List<String> allkeysWithBoxes = getAllKeysWithChatBoxes();
		if (allkeysWithBoxes.isEmpty()) {
			addActionMessage("There are no chats stored in the systen yet");
		}
		List chatResults = new ArrayList();
		List results = super.getPaginationSupport().getItems();
		for (Object object : results) {
			if (object instanceof User) {
				String userKey = ChatUtils.getCorrectUserKey(((User) object).getName());
				if (allkeysWithBoxes.contains(userKey)) {
					chatResults.add(object);
				}
			}
		}
		paginationSupportNew = new PaginationSupport(chatResults, 10);
		paginationSupportNew.setStartIndex(getStartIndex());
		return SUCCESS;
	}

	private List<String> getAllKeysWithChatBoxes() {
		List<String> usernames = new ArrayList<String>();
		Statement ps = null;
		try {
			ps = pluginHibernateSessionFactory.getSession().connection().createStatement();
			ResultSet rs = ps.executeQuery(ChatManager.QUERY_HISTORIES);
			while (rs.next()) {
				String key = rs.getString(1);
				String replace = key.replace(ChatManager.KEY_HISTORY, "");
				usernames.add(replace);
			}
		} catch (HibernateException ex) {
			addActionError("Error while Hibernate execution: " + ex.getMessage());
			LOG.error("Error while Hibernate execution: ", ex);
		} catch (SQLException ex) {
			addActionError("Error while SQL execution: " + ex.getMessage());
			LOG.error("Error while SQL execution: ", ex);
		} finally {
			JDBCUtils.close(ps);
		}
		return usernames;
	}

	/**
	 * @return the paginationSupportNew
	 */
	@Override
	public PaginationSupport getPaginationSupport() {
		return paginationSupportNew;
	}
}
