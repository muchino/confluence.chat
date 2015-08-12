package confluence.chat.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import confluence.chat.actions.LoginLogoutListener;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author oli
 */
public class CanViewHistoryCondition extends BaseConfluenceCondition {

    private static final Logger logger = Logger.getLogger(LoginLogoutListener.class);
    private PermissionManager permissionManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext wic) {

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
