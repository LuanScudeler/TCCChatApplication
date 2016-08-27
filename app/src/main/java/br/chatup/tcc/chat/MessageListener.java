package br.chatup.tcc.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jxmpp.util.XmppDateTime;

import java.util.ArrayList;
import java.util.Date;

import br.chatup.tcc.activity.ChatActivity;
import br.chatup.tcc.adapters.ChatContainerAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.MessageService;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;

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
            chatMessage = new ChatMessage(message.getBody(), chat.getParticipant(), false, XmppDateTime.DateFormatType.XEP_0082_TIME_PROFILE.format(new Date()));

            Log.d(TAG, "NOTIFYING MESSAGE RECEIVED");
            Intent intent = new Intent("receivedMessage");
            intent.putExtra("message", chatMessage);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

}