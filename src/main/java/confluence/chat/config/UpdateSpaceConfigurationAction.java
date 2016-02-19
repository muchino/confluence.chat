package confluence.chat.config;

import com.atlassian.gzipfilter.org.apache.commons.lang.StringEscapeUtils;
import com.atlassian.user.EntityException;
import com.atlassian.user.GroupManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.utils.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class UpdateSpaceConfigurationAction extends ViewSpaceConfigurationAction {

	private String groups = null;
	private List<String> groupList = new ArrayList<String>();
	private GroupManager groupManager;

	@Override
	public void validate() {
		super.validate();

		List<String> stringToList = ChatUtils.stringToList(groups);
		for (int i = 0; i < stringToList.size(); i++) {
			String group = stringToList.get(i);
			try {
				if (groupManager.getGroup(group) == null) {
					addActionError(getText("chat.config.import.error.group", new String[]{StringEscapeUtils.escapeHtml(group)}));
				} else {
					groupList.add(group);
				}
			} catch (EntityException ex) {
				addActionError(getText("chat.config.import.error.group", new String[]{StringEscapeUtils.escapeHtml(group)}));
			}

		}

	}

	@Override
	public String execute() throws Exception {
		super.execute();
		HttpServletRequest request = ServletActionContext.getRequest();
		ChatSpaceConfiguration config = getChatManager().getChatSpaceConfiguration(getSpaceKey());
		config.setAllowAll(StringUtils.isNotEmpty(request.getParameter("allowAll")));
		config.setGroups(groupList);
		getChatManager().setChatSpaceConfiguration(config, getSpaceKey());
		return SUCCESS;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(String groups) {
		this.groups = groups;
	}

	/**
	 * @param groupManager the groupManager to set
	 */
	public void setGroupManager(GroupManager groupManager) {
		this.groupManager = groupManager;
	}
}
