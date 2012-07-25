/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Dev
 */
public class ChatMessageParser {

    SmileyMap smileys = new SmileyMap();

    public ChatMessageParser() {
        smileys.add(":)", getIcon("smiley friendly"));
        smileys.add(":-)", getIcon("smiley friendly"));
        smileys.add(":*", getIcon("smiley kiss"));
        smileys.add(":-*", getIcon("smiley kiss"));
//        smileys.add(":(");
//        smileys.add(":P");
//        smileys.add(";)");
//        smileys.add("^.^");
//        smileys.add(":~(");
//        smileys.add(":-(");
//        smileys.add(":-o");
//        smileys.add(":*-/");
//        smileys.add(":-c");
//        smileys.add(":-D");
//        smileys.add(":')");
//        smileys.add(":bow:");
//        smileys.add(":whistle:");
//        smileys.add(":zzz:");
//        smileys.add(":kiss:");
//        smileys.add(":rose:");


    }

    public class SmileyMap
            extends HashMap<String, String> {
        // constructor(s) assumed  

        public void add(String s) {
            this.add(s, "");

        }

        public void add(String s, String replacement) {
            put(Pattern.quote(s), replacement);
        }
    }

    private String getIcon(String classname) {
        return "<img src='http://xenforo.com/styles/default/xenforo/clear.png' class='" + classname + "' />";
    }

    public String parseString(String input) {
        for (Map.Entry<String, String> smiley : smileys.entrySet()) {
            input = input.replaceAll(smiley.getKey(), smiley.getValue());
        }
        return input;
    }
}
