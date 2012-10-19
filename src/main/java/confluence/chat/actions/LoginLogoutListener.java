package confluence.chat.actions;

import confluence.chat.manager.ChatManager;
import confluence.chat.model.ChatUser;
import confluence.chat.model.ChatStatus;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import org.apache.log4j.Logger;

public class LoginLogoutListener implements EventListener {

    private static final Logger log = Logger.getLogger(LoginLogoutListener.class);
    private final ChatManager chatManager;
    private Class[] handledClasses = new Class[]{LoginEvent.class, LogoutEvent.class};

    public LoginLogoutListener(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof LoginEvent) {
            LoginEvent loginEvent = (LoginEvent) event;
            ChatUser chatUser = chatManager.getChatUser(loginEvent.getUsername());
            chatManager.setOnlineStatus(loginEvent.getUsername(), chatUser.getPreferences().getChatStatus());
        } else if (event instanceof LogoutEvent) {
            LogoutEvent logoutEvent = (LogoutEvent) event;
            chatManager.setOnlineStatus(logoutEvent.getUsername(), ChatStatus.OFFLINE);
        }
    }

    @Override
    public Class[] getHandledEventClasses() {
        return handledClasses;
    }
}