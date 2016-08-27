package br.chatup.tcc.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Luan on 5/13/2016..
 */

public class ChatMessage implements Serializable{

    private String body;
    private String receiver;
    private boolean isMe;
    private String date;

    /*
    * @param body message body.
    * @param receiver contact which the message came from or will go for.
    * */

    public ChatMessage(String body, String receiver, boolean isMe, String date) {
        this.body = body;
        this.receiver = receiver;
        this.isMe = isMe;
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
