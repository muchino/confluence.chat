package confluence.chat.config;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearcher;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.QueryTokenizer;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.core.db.JDBCUtils;
import com.atlassian.hibernate.PluginHibernateSessionFactory;
import com.atlassian.user.User;
import confluence.chat.manager.ChatManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.hibernate.HibernateException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class BrowseHistoryAction extends ConfluenceActionSupport {

	private static final Logger logger = Logger.getLogger(BrowseHistoryAction.class);
	private PaginationSupport paginationSupport;
	private PluginHibernateSessionFactory pluginHibernateSessionFactory;
	private QueryTokenizer contentNameQueryTokenizer;
	private ContentNameSearcher contentNameSearcher;
	private ChatManager chatManager;
	private Integer startIndex = 0;
	private String searchTerm;

	public String doUserSearch() {
		List chatResults = new ArrayList();

		List<String> allkeysWithBoxes = getAllKeysWithChatBoxes();
		if (allkeysWithBoxes.isEmpty()) {
			addActionMessage("There are no chats stored in the systen yet");
		} else {

			List<Category> searchCategories = new ArrayList<>();
			searchCategories.add(Category.PEOPLE);

			Map<Category, List<SearchResult>> results = contentNameSearcher.search(
					generateQueryTokens(searchTerm),
					generateResultTemplate(searchCategories));

			// convert search results to user objects
			List<User> users = new ArrayList<>();
			for (List<SearchResult> resultCategories : results.values()) {
				for (SearchResult resultEntry : resultCategories) {
					User user = userAccessor.getUser(resultEntry.getUsername());
					chatResults.add(user);
				}
			}

//			addActionMessage(allkeysWithBoxes.size() + "");
//			for (String userKey : allkeysWithBoxes) {
//				User user = userAccessor.getUser(ChatUtils.getUserNameByKeyOrUserName(userKey));
////				if (user != null) {
//				chatResults.add(user);
////				}
//			}
		}

		paginationSupport = new PaginationSupport(chatResults, 10);
		paginationSupport.setStartIndex(getStartIndex());
		return SUCCESS;
	}

	private List<QueryToken> generateQueryTokens(String query) {
		List<QueryToken> queryTokens = new ArrayList<>();
		if (StringUtils.isBlank(query) || query.startsWith("*")) {
			queryTokens.add(new QueryToken("", QueryToken.Type.PARTIAL));
		} else {
			queryTokens = contentNameQueryTokenizer.tokenize(query);
		}

		// check if query tokens were generated successfully
		if (queryTokens.isEmpty()) {
			// add fallback query token to prevent IllegalArgumentException
			queryTokens.add(new QueryToken(query, QueryToken.Type.PARTIAL));
		}

		return queryTokens;
	}

	private List<String> getAllKeysWithChatBoxes() {
		List<String> usernames = new ArrayList<>();
		Statement ps = null;
		try {
			ps = pluginHibernateSessionFactory.getSession().connection().createStatement();
			ResultSet rs = ps.executeQuery(ChatManager.QUERY_HISTORIES);
			while (rs.next()) {
				String key = rs.getString(1);
				String username = key.replace(ChatManager.KEY_HISTORY, "");
				if (userAccessor.getUser(username) != null) {
					chatManager.deleteChatBoxesOfUser(username);
				} else {
					usernames.add(username);
				}
			}
		} catch (HibernateException ex) {
			addActionError("Error while Hibernate execution: " + ex.getMessage());
			logger.error("Error while Hibernate execution: ", ex);
		} catch (SQLException ex) {
			addActionError("Error while SQL execution: " + ex.getMessage());
			logger.error("Error while SQL execution: ", ex);
		} finally {
			JDBCUtils.close(ps);
		}
		return usernames;
	}

	private ResultTemplate generateResultTemplate(List<Category> searchCategories) {
		ResultTemplate resultTemplate = new ResultTemplate();
		for (Category searchCategorie : searchCategories) {
			resultTemplate.addCategory(searchCategorie, 10);
		}
		return resultTemplate;
	}

	public PaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setPluginHibernateSessionFactory(PluginHibernateSessionFactory sessionFactory) {
		this.pluginHibernateSessionFactory = sessionFactory;
	}

	public void setContentNameSearcher(ContentNameSearcher contentNameSearcher) {
		this.contentNameSearcher = contentNameSearcher;
	}

	public void setContentNameQueryTokenizer(QueryTokenizer contentNameQueryTokenizer) {
		this.contentNameQueryTokenizer = contentNameQueryTokenizer;
	}

	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
