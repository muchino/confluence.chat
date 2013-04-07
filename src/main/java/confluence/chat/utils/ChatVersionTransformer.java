/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.utils;

import confluence.chat.ChatVersion;
import confluence.chat.manager.ChatManager;

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

    public Boolean transformationNeeded() {
        return savedVersion.compareTo(currentChatVersion) < 0;
    }

    public final void transform() {
        /**
         * savedVersion ist kleiner gleich der �bergebenen
         */
        if (isOlderThan("1.2.0")) {
        }


        /*
         *  es ist ein downgrade passiert
         */
        if (isNewerThan(currentChatVersion.getVersion())) {
        }

        /**
         * Setze chatversion zum schluss
         */
        chatManager.getChatConfiguration().setChatVersionPlain(currentChatVersion.getVersion());

    }

    private boolean isOlderThan(String versionNumber) {
        return savedVersion.compareTo(new ChatVersion(versionNumber)) <= 0;
    }

    private boolean isNewerThan(String versionNumber) {
        return savedVersion.compareTo(new ChatVersion(versionNumber)) > 0;
    }
}
