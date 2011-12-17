/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confluence.chat.actions;

import java.util.Date;

/**
 *
 * @author Dev
 */
public class ChatMessage {

    private String from;
    private String message;
    private Date senddate = new Date();
    private Boolean seen = false;

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
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
     * @return the seen
     */
    public Boolean getSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }
}
