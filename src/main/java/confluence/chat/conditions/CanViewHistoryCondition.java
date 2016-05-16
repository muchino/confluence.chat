package confluence.chat.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import confluence.chat.listener.LoginLogoutListener;
import confluence.chat.manager.ChatManager;
import org.apache.log4j.Logger;

public class CanViewHistoryCondition extends ChatHistoryEnabledCondition {

	private static final Logger logger = Logger.getLogger(LoginLogoutListener.class);
	private PermissionManager permissionManager;

	@Override
	protected boolean shouldDisplay(WebInterfaceContext wic) {
		if (!super.shouldDisplay(wic)) {
			return false;
		}
		if (AuthenticatedUserThreadLocal.getUser() == wic.getTargetUser()) {
			return true;
		}
		final User user = AuthenticatedUserThreadLocal.getUser();
		return permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
	}

	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

}
