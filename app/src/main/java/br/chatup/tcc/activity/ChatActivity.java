package br.chatup.tcc.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppDateTime;

import java.util.ArrayList;
import java.util.Date;

import br.chatup.tcc.adapters.ChatContainerAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.cache.CacheStorage;

import br.chatup.tcc.chat.MessageListener;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;


public class ChatActivity extends AppCompatActivity {

	private static final String TAG = Constants.LOG_TAG + ChatActivity.class.getSimpleName();
	private static final String FULL_JID_APPEND = Constants.FULL_JID_APPEND;

	private EditText edtMessageBody;
	private TextView tvContact;
	private Chat newChat;
    private ListView messagesContainer;
    private ChatContainerAdapter chatContainerAdapter;

    private String contactJID;
    private String contactFULL_JID;
    private String messageBody;
    private ChatMessage chatMessage;

    private static boolean serviceConnected;
    private static XmppService xmppService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppService = ((LocalBinder<XmppService>) iBinder).getService();
            serviceConnected = true;
            Log.d(TAG, "onServiceConnected: " + xmppService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatMessage message = (ChatMessage)intent.getSerializableExtra("message");
            Log.d(TAG, "[BroadcastReceiver] Message received: " + message.getBody());
            displayMessage(message);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(getBaseContext(), XmppService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		edtMessageBody = (EditText) findViewById(R.id.edtMessage);
        tvContact = (TextView) findViewById(R.id.tvContact);
        messagesContainer = (ListView) findViewById(R.id.msgListView);

		contactJID = getIntent().getExtras().getString("contactJID").toString();
		contactFULL_JID = contactJID.concat(FULL_JID_APPEND);
		Log.d(TAG, "Opening chat with: " + contactFULL_JID);

		tvContact.setText(Util.parseContactName(contactFULL_JID));
        initChatContainer();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // Registering an observer (mMessageReceiver) to receive Intents
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("receivedMessage"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    private void initChatContainer() {
        chatContainerAdapter = new ChatContainerAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(chatContainerAdapter);
    }

    public void displayMessage(ChatMessage message) {
        chatContainerAdapter.add(message);
        ((ChatContainerAdapter) messagesContainer.getAdapter()).notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    public void btnSendMessageClick(View v) {

        messageBody = edtMessageBody.getText().toString();
        chatMessage = new ChatMessage(messageBody, contactFULL_JID, true, XmppDateTime.DateFormatType.XEP_0082_TIME_PROFILE.format(new Date()));

        sendMessage();
        displayMessage(chatMessage);
		edtMessageBody.setText("");
	}

	public void sendMessage() {
		if(!messageBody.equalsIgnoreCase("")) {
			final Message message = new Message();
			ChatManager chatManager = ChatManager.getInstanceFor(xmppService.getXmppManager().getConn());
			//Gets for whom the message will go for (retrieves a user JID)
			String messageReceiver = chatMessage.getReceiver();

			if(!CacheStorage.getInstanceCachedChats().containsKey(messageReceiver)) {
				newChat = chatManager.createChat(messageReceiver, new MessageListener(ChatActivity.this));

                CacheStorage.addChatContact(messageReceiver,newChat.getThreadID());
                Log.d(TAG,"CHAT CREATED - Receiver not found in contacts cache, ADDING TO CACHE: Contact: "+messageReceiver+" ThreadID:"+newChat.getThreadID());
            }else {
				//Get chat threadID from cachedChats for the current contact that the user is chatting with
				newChat = chatManager.getThreadChat(CacheStorage.getInstanceCachedChats().get(contactFULL_JID));
				//Set on message the chat threadID that already exist in the cachedChats
				message.setThread(CacheStorage.getInstanceCachedChats().get(contactFULL_JID).toString());

				Log.d(TAG, "CONTACT CHAT ALREADY OPEN: Setting threadID for reply: CACHED_THREAD-ID: " + CacheStorage.getInstanceCachedChats().get(contactFULL_JID).toString());
			}

			message.setBody(messageBody);
			message.setType(Message.Type.chat);

			try {
				newChat.sendMessage(message);
			} catch(SmackException.NotConnectedException e) {
				e.printStackTrace();
			}
		}
	}

	//TODO: Allow user to answer the first message (GUI stuffs, list activity and user notification for incoming messages)
}


