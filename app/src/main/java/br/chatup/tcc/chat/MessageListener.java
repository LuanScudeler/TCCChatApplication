package br.chatup.tcc.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
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
public class MessageListener implements ChatMessageListener {

    private static final String TAG = Constants.LOG_TAG + MessageListener.class.getSimpleName();
    private ChatMessage chatMessage;
    private Context context;
    private MessageService messageService;
    private static boolean serviceConnected;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            messageService = ((LocalBinder<MessageService>) iBinder).getService();
            serviceConnected = true;
            Log.d(TAG, "[MessageService] onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "[MessageService] onServiceDisconnected: ");
        }
    };

    public MessageListener(Context context) {
        this.context = context;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.d(TAG, "MESSAGE RECEIVED: Body: " + message.getBody() + " - User: " + chat.getParticipant() + " - ThreadID: " + chat.getThreadID());

        //Gets from who the message came from
        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            Intent i = new Intent(context, MessageService.class);
            context.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            chatMessage = new ChatMessage(message.getBody(), chat.getParticipant(), false, XmppDateTime.DateFormatType.XEP_0082_TIME_PROFILE.format(new Date()));
            while (!serviceConnected);//empty loop to give time for service binding
            //TODO: Find how to map each contact with its own chat screen
            messageService.notifyMessage(chatMessage);
        }
    }

}