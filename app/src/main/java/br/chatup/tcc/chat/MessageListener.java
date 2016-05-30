package br.chatup.tcc.chat;

import android.util.Log;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;

/**
 * Created by Luan on 5/8/2016..
 */
public class MessageListener implements ChatMessageListener {

    private static final String TAG = Constants.LOG_TAG + MessageListener.class.getSimpleName();

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.d(TAG, "MESSAGE RECEIVED --- Body: " + message.getBody() + " --- User: " + chat.getParticipant() + " --- ThreadID: " + chat.getThreadID());

        //Gets from who the message came from
        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            final ChatMessage chatMessage = new ChatMessage(message.getBody(), chat.getParticipant());
            //TODO add these informations to a list of opened chats
            //TODO If this method is executed before the message is sent we can implement the translation process here
        }
    }
}