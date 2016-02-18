package confluence.chat.config;

import com.atlassian.user.EntityException;
import com.atlassian.user.GroupManager;
import com.opensymphony.webwork.ServletActionContext;
import confluence.chat.utils.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class UpdateConfigurationAction extends ViewConfigurationAction {

	private Integer heartbeat = null;
	private String groups = null;
	private List<String> groupList = new ArrayList<String>();
	private GroupManager groupManager;

	@Override
	public void validate() {
		super.validate();

		if (heartbeat == null) {
			addActionError(getText("chat.config.import.error.heartbeat"));
		}
		List<String> stringToList = ChatUtils.stringToList(groups);
		for (int i = 0; i < stringToList.size(); i++) {
			String group = stringToList.get(i);
			try {
				if (groupManager.getGroup(group) == null) {
					addActionError(getText("chat.config.import.error.group", new String[]{group}));
				} else {
					groupList.add(group);
				}
			} catch (EntityException ex) {
				addActionError(getText("chat.config.import.error.group", new String[]{group}));
			}

		}

	}

	@Override
	public String execute() throws Exception {
		super.execute();
		HttpServletRequest request = ServletActionContext.getRequest();
		ChatConfiguration config = getChatManager().getChatConfiguration();
		config.setAllowAll(StringUtils.isNotEmpty(request.getParameter("allowAll")));
		config.setShowWhereIam(StringUtils.isNotEmpty(request.getParameter("showWhereIam")));

		config.setDebugMode(StringUtils.isNotEmpty(request.getParameter("debugMode")));
		config.setHideInEditMode(StringUtils.isNotEmpty(request.getParameter("hideInEditMode")));
		config.setPlaySound(StringUtils.isNotEmpty(request.getParameter("playSound")));
		config.setShowHistory(StringUtils.isNotEmpty(request.getParameter("showHistory")));
		config.setGroups(groupList);
		config.setHeartBeat(heartbeat);
		getChatManager().setChatConfiguration(config);
		return SUCCESS;
	}

	/**
	 * @param heartbeat the heartbeat to set
	 */
	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
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
