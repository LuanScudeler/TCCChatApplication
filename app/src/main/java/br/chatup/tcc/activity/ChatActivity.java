package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnSendMessage;
    private EditText edtMessageBody;
    private static final String TAG = Constants.LOG_TAG + ChatActivity.class.getSimpleName();
    private Chat newChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSendMessage = (ImageButton) findViewById(R.id.btnSendMessage);
        edtMessageBody = (EditText) findViewById(R.id.edtMessage);

    }

    public void btnSendMessageClick (View v) {
        sendMessage();
    }

    public void sendMessage(){
        String messageBody = edtMessageBody.getText().toString();

        //TODO receiver (exemplo "luan@luanpc") must come from list of contacts (when contact is selected to start a conversation or to reply a received message)
        if(!messageBody.equalsIgnoreCase("")){
            final ChatMessage chatMessage = new ChatMessage(messageBody, "luan@luanpc");
            final Message message = new Message();
            ChatManager chatManager = ChatManager.getInstanceFor(XmppManager.getConn());
            String messageReceiver = chatMessage.getReceiver();

            //For tests:: change "luanpc" for your xmpp.domain value. Can be found in openfire at Server Manager > System Properties
            if(!CacheStorage.getInstanceCachedChats().containsKey(messageReceiver)){
                newChat = chatManager.createChat(messageReceiver, new br.chatup.tcc.chat.MessageListener());
                CacheStorage.addChatContact(messageReceiver, newChat.getThreadID());

                Log.d(TAG, "CHAT CREATED - Receiver not found in contacts cache, ADDING TO CACHE: Contact: " + messageReceiver  + " ThreadID:"+ newChat.getThreadID());
            }else{
                message.setThread(CacheStorage.getInstanceCachedChats().get("luan@luanpc").toString());

                Log.d(TAG, "CONTACT CHAT ALREADY OPEN: Setting threadID for reply: CACHED_THREAD-ID: " + CacheStorage.getInstanceCachedChats().get("luan@luanpc").toString());
            }

            message.setBody(messageBody);
            message.setType(Message.Type.chat);

            try {
                newChat.sendMessage(message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: Handle message receivement, allowing user to answer the first message (GUI stuffs, list activity and user notification for incoming messages)
}


