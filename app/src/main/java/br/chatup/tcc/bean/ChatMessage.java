package br.chatup.tcc.bean;

import java.io.Serializable;

/**
 * Created by Luan on 5/13/2016..
 */

public class ChatMessage implements Serializable {

    private String body;
    private String bodyTranslated;
    private String receiver;
    private boolean isMe;
    private String date;

    public ChatMessage() {}

    public ChatMessage(String body, String receiver, boolean isMe, String date, String bodyTranslated) {
        this.body = body;
        this.receiver = receiver;
        this.isMe = isMe;
        this.date = date;
        this.bodyTranslated = bodyTranslated;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setBodyTranslated(String bodyTranslated) {
        this.bodyTranslated = bodyTranslated;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public String getBodyTranslated() {
        return bodyTranslated;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isMe() {
        return isMe;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;

        ChatMessage that = (ChatMessage) o;

        if (isMe() != that.isMe()) return false;
        if (!getBody().equals(that.getBody())) return false;
        if (!getBodyTranslated().equals(that.getBodyTranslated())) return false;
        if (!getReceiver().equals(that.getReceiver())) return false;
        return getDate().equals(that.getDate());

    }

    @Override
    public int hashCode() {
        int result = getBody().hashCode();
        result = 31 * result + getBodyTranslated().hashCode();
        result = 31 * result + getReceiver().hashCode();
        result = 31 * result + (isMe() ? 1 : 0);
        result = 31 * result + getDate().hashCode();
        return result;
    }
}
