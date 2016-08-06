package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.chatup.tcc.adapters.ChatContainerAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

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

    private void initChatContainer() {

        chatContainerAdapter = new ChatContainerAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(chatContainerAdapter);
    }

    public void displayMessage(ChatMessage message) {
        chatContainerAdapter.add(message);
        chatContainerAdapter.notifyDataSetChanged();
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
	}

	public void sendMessage() {


		//TODO receiver (exemplo "luan@luanpc") must come from list of contacts (when contact is selected to start a conversation or to reply a received message)
		if(!messageBody.equalsIgnoreCase("")) {
			//final ChatMessage chatMessage = new ChatMessage(messageBody, contactFULL_JID);
			final Message message = new Message();
			ChatManager chatManager = ChatManager.getInstanceFor(XmppManager.getConn());
			//Gets for whom the message will go for (retrieves a user JID)
			String messageReceiver = chatMessage.getReceiver();

			//For tests:: change "luanpc" for your xmpp.domain value. Can be found in openfire at Server Manager > System Properties
			if(!CacheStorage.getInstanceCachedChats().containsKey(messageReceiver)) {
				newChat = chatManager.createChat(messageReceiver, new br.chatup.tcc.chat.MessageListener(ChatActivity.this));
				CacheStorage.addChatContact(messageReceiver, newChat.getThreadID());

				Log.d(TAG, "CHAT CREATED - Receiver not found in contacts cache, ADDING TO CACHE: Contact: " + messageReceiver + " ThreadID:" + newChat.getThreadID());
			} else {
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

	//TODO: Handle message receivement, allowing user to answer the first message (GUI stuffs, list activity and user notification for incoming messages)
}


