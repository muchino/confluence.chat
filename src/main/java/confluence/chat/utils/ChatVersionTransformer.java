/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.utils;

import confluence.chat.ChatVersion;
import confluence.chat.actions.ChatManager;

/**
 *
 * @author oli
 */
public class ChatVersionTransformer {

    private ChatManager chatManager;
    private ChatVersion currentChatVersion = new ChatVersion();
    private ChatVersion savedVersion;

    public ChatVersionTransformer(ChatManager chatManager) {
        this.chatManager = chatManager;
        savedVersion = new ChatVersion(chatManager.getChatConfiguration().getChatVersionPlain());
    }

    public  Boolean transformationNeeded() {
        return savedVersion.compareTo(currentChatVersion) < 0;
    }
    
    
    public final void transform() {
        /**
         * savedVersion ist kleiner gleich der übergebenen
         */
        
        
        if (savedVersion.compareTo(new ChatVersion("0")) <= 0) {
        }
        
        
    }
}
