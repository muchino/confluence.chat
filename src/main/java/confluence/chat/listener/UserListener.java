package confluence.chat.listener;

import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import confluence.chat.manager.ChatManager;
import org.springframework.beans.factory.DisposableBean;

public class UserListener implements DisposableBean {

	private final ChatManager chatManager;
	private final EventPublisher eventPublisher;

	public UserListener(ChatManager chatManager,
			EventPublisher eventPublisher) {
		this.chatManager = chatManager;
		this.eventPublisher = eventPublisher;
		this.eventPublisher.register(this);
	}

	@EventListener
	public void onLoginEvent(UserRemoveEvent event) {
		chatManager.deleteChatBoxesOfUser(event.getUser());
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
