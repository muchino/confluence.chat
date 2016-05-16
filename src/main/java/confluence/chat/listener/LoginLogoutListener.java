package confluence.chat.listener;

import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import confluence.chat.manager.ChatManager;
import org.springframework.beans.factory.DisposableBean;

public class LoginLogoutListener implements DisposableBean {

	private final ChatManager chatManager;
	private final EventPublisher eventPublisher;

	public LoginLogoutListener(ChatManager chatManager,
			EventPublisher eventPublisher) {
		this.chatManager = chatManager;
		this.eventPublisher = eventPublisher;
		this.eventPublisher.register(this);
	}

	@EventListener
	public void onLoginEvent(LoginEvent event) {
		chatManager.doLogin(event.getUsername());
	}

	@EventListener
	public void onLogoutEvent(LogoutEvent event) {
		chatManager.doLogout(event.getUsername());
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
