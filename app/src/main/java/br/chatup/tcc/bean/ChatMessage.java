package br.chatup.tcc.bean;

/**
 * Created by Luan on 5/13/2016..
 */
public class ChatMessage {

    private String body;

    private String receiver;

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
}
