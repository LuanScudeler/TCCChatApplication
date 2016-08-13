package br.chatup.tcc.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;

/**
 * Created by Luan on 5/8/2016..
 */
public class MessageListener implements ChatMessageListener {

    private static final String TAG = Constants.LOG_TAG + MessageListener.class.getSimpleName();
    private ChatMessage chatMessage;
    private ListView messagesContainer;
    private ChatContainerAdapter chatContainerAdapter;
    private Handler mHandler = new Handler();

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.d(TAG, "MESSAGE RECEIVED: Body: " + message.getBody() + " - User: " + chat.getParticipant() + " - ThreadID: " + chat.getThreadID());

        //Gets from who the message came from
        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            chatMessage = new ChatMessage(message.getBody(), chat.getParticipant(), false, XmppDateTime.DateFormatType.XEP_0082_TIME_PROFILE.format(new Date()));
            //TODO Find how to update "msgListView" with received messages.
            //TODO Find how to properly get ChatContainerAdapter, Activity and ListView instances
        }
    }

}