/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dev
 */
public class ChatMessage {

    public static final String FROM = "f";
    public static final String TO = "r";
    public static final String MESSAGE = "m";
    public static final String SENDDATE = "t";
    private Date senddate = new Date();
    private Map<String, Object> jsonMap = new HashMap<String, Object>();

    public ChatMessage() {
        this.jsonMap.put(FROM, "");
        this.jsonMap.put(MESSAGE, "");
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return (String) this.jsonMap.get(MESSAGE);
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.jsonMap.put(MESSAGE, message);
    }

    /**
     * @return the senddate
     */
    public Date getSenddate() {
        return senddate;
    }

    /**
     * @param senddate the senddate to set
     */
    public void setSenddate(Date senddate) {
        this.senddate = senddate;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return (String) this.jsonMap.get(FROM);
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.jsonMap.put(FROM, from);
    }
    
        /**
     * @return the from
     */
    public String getTo() {
        return (String) this.jsonMap.get(TO);
    }

    /**
     * @param from the from to set
     */
    public void setTo(String to) {
        this.jsonMap.put(TO, to);
    }

    public Map<String, Object> getJSONMap() {
        this.jsonMap.put(SENDDATE, this.senddate.getTime());
        return jsonMap;
    }
}
