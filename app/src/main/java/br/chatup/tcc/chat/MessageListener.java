package br.chatup.tcc.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jxmpp.util.XmppStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.utils.Constants;

/**
 * Created by Luan on 5/8/2016..
 */
//TODO: Investigate if MessageListener can be instantiated multiple times
public class MessageListener implements ChatMessageListener, ChatStateListener {

    private static final String TAG = Constants.LOG_TAG + MessageListener.class.getSimpleName();
    private ChatMessage chatMessage;
    private Context context;

    public MessageListener(Context context) {
        this.context = context;
    }

    @Override
    public void stateChanged(Chat chat, ChatState state) {
        //Can be used to broadcast to gui state changes of the chat
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.d(TAG, "[MESSAGE RECEIVED] Body: " + message.getBody() + " | User: " + chat.getParticipant() + " | ThreadID: " + chat.getThreadID());

        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            String time = new SimpleDateFormat("HH:mm").format(new Date());
            chatMessage = new ChatMessage(message.getBody(),
                    chat.getParticipant(),
                    false,
                    time);

            Log.d(TAG, "NOTIFYING MESSAGE RECEIVED");
            Intent intent = new Intent("receivedMessage");
            intent.putExtra("message", chatMessage);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            //TODO: Filter when it should and should not send broadCast to the service
            //Sending broadCast to service
            intent.setAction("BROADCAST_ON_SERVICE");
            context.sendBroadcast(intent);
            //Save received message to chat history
            AppDataSource db = new AppDataSource(context);
            //"contact" in database must be only de username portion of the JID
            ChatMessage storableChatMessage = new ChatMessage(message.getBody(),
                    XmppStringUtils.parseLocalpart(chat.getParticipant()),
                    false,
                    time);
            db.insert(storableChatMessage);
        }
    }
}