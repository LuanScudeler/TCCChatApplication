package br.chatup.tcc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jivesoftware.smack.chat.Chat;
import org.jxmpp.util.XmppStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.chatup.tcc.adapters.ChatContainerAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG + ChatActivity.class.getSimpleName();
    private EditText edtMessageBody;
    private ListView messagesContainer;
    private ImageButton btnSendMessage;
    private Chat newChat;
    private ChatMessage chatMessage;
    private ChatContainerAdapter chatContainerAdapter;
    private AppDataSource db;

    private String currActiveChat;
    private String contactJID;
    private String token;
    private String langTo;

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

    /**
     * FullJID composed by localPart@Domain/Resource
     * <p>
     * localPart: username
     * Domain: Server address
     * Resource: "/Smack"
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        ChatMessage message = (ChatMessage) intent.getSerializableExtra("message");
        Log.d(TAG, "[BroadcastReceiver] Message received | From: " + message.getReceiver() + " | Body: " + message.getBody());

        String senderUsername = XmppStringUtils.parseLocalpart(message.getReceiver());
        if (senderUsername.equals(XmppStringUtils.parseLocalpart(contactJID)))
            displayMessage(message);
        else {
            Log.d(TAG, "[BroadcastReceiver] Raising notification");
            //For communication to work when opening a chat contactJid must be in bareJid format
            String contactJID = XmppStringUtils.parseBareJid(message.getReceiver());
            Util.showNotification(getApplicationContext(), ChatActivity.class, contactJID, message.getBody());
        }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(ChatActivity.this, XmppService.class);
        bindService(i, mConnection, 0);
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // Registering an observer (mMessageReceiver) to receive Intents
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("receivedMessage"));

        App.setCurrentActivity(this);
        App.setCurrentActiveChat(currActiveChat);

        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);

        clearReferences();

        super.onPause();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    private void clearReferences() {
        Activity currActivity = App.getCurrentActivity();
        if (this.equals(currActivity) && currActiveChat.equals(App.getCurrentActiveChat())) {
            App.setCurrentActivity(null);
            App.setCurrentActiveChat(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        edtMessageBody = (EditText) findViewById(R.id.edtMessage);
        messagesContainer = (ListView) findViewById(R.id.msgListView);
        btnSendMessage = (ImageButton) findViewById(R.id.btnSendMessage);

        db = new AppDataSource(this);

        //Get contactJID from selected user
        contactJID = getIntent().getExtras().getString("contactJID");
        //username for controlling notification behavior
        currActiveChat = XmppStringUtils.parseLocalpart(contactJID);

        String displayableUsername = Util.toCapital(XmppStringUtils.parseLocalpart(contactJID));
        ChatActivity.this.setTitle(displayableUsername);

        initChatView();
        initChatData();

        //Start Async task for retrieving receptor contact language
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                ResponseEntity<String> resp = RestFacade.get(String.format(Constants.RESTAPI_USER_URL, contactJID.split("@")[0]));
                User u = br.chatup.tcc.utils.JsonParser.fromJson(User.class, resp.getBody());
                Log.i(TAG, "Retrieved receptor properties: " + u.getProperties().get("property").getValue());
                return u.getProperties().get("property").getValue();
            }

            @Override
            protected void onPostExecute(String s) {
                langTo = s;
            }
        }.execute();
    }

    private void initChatView() {
        chatContainerAdapter = new ChatContainerAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(chatContainerAdapter);
    }

    private void initChatData() {
        //Retrieves chat history
        List<ChatMessage> chatMessageList;
        chatMessageList = db.findAllByContactJID(XmppStringUtils.parseLocalpart(contactJID));
        for (ChatMessage chatMessage : chatMessageList) {
            chatContainerAdapter.add(chatMessage);
        }
        ((ChatContainerAdapter) messagesContainer.getAdapter()).notifyDataSetChanged();
        scroll();
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
        if (edtMessageBody.getText().toString().isEmpty()) return;

        String messageBody = edtMessageBody.getText().toString();
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        chatMessage = new ChatMessage(messageBody, contactJID, true, time, messageBody);

        if (App.isTranslationEnabled()) {
            Log.i(TAG, "Sending message, translation mode is now: " + App.isTranslationEnabled());
            displayMessage(chatMessage);
            translateAndSend();
        } else {
            Log.i(TAG, "Sending message, translation mode is now: " + App.isTranslationEnabled());
            sendMessage(messageBody, messageBody);
            displayMessage(chatMessage);
            edtMessageBody.setText("");
        }
    }

    public void translateAndSend() {
        if (!langTo.equals(xmppService.getXmppManager().getUser().getProperties().get("property").getValue())) {
            new AsyncTask<String, Void, String[]>() {
                String message, messageTranslated;

                @Override
                protected void onPreExecute() {
                    edtMessageBody.setText("");
                    btnSendMessage.setEnabled(false);
                }

                @Override
                protected String[] doInBackground(String[] params) {
                    message = params[0];
                    message.replaceAll("\\s", "%20");

                    ResponseEntity<String> re = RestFacade.post(Constants.TOKEN_SERVICE_URL, Constants.TOKEN_SERVICE_URI_PARAMS);
                    String r = re.getBody();
                    JsonElement jelement = new JsonParser().parse(r);
                    JsonObject jobject = jelement.getAsJsonObject();
                    token = jobject.get("access_token").toString();

                    //sending the message to the translation service
                    HttpHeaders headers = new HttpHeaders();
                    String langFrom = xmppService.getXmppManager().getUser().getProperties().get("property").getValue();
                    String turl = String.format(Constants.TRANSLATION_URL, message, langFrom, langTo);
                    headers.add("Authorization", "Bearer " + token.replaceAll("\"", ""));
                    ResponseEntity<String> resp = RestFacade.get(
                            turl,
                            headers
                    );
                    String s = resp.getBody();
                    //response format from translation service: <string xmlns="http://schemas.microsoft.com/2003/10/Serialization/">The message</string>
                    messageTranslated = s.split(">")[1].split("<")[0];
                    return new String[]{message, messageTranslated};
                }

                @Override
                protected void onPostExecute(String[] s) {
                    btnSendMessage.setEnabled(true);
                    sendMessage(s[0], s[1]);
                }
            }.execute(edtMessageBody.getText().toString());
        } else {
            Log.d(TAG, "[Translation skipped] Users have the same language");
            sendMessage(edtMessageBody.getText().toString(), edtMessageBody.getText().toString());
        }
    }

    public void sendMessage(String messageBody, String messageBodyTranslated) {
        //Gets for whom the message will go for (retrieves a user JID: username@domain)
        String messageReceiver = chatMessage.getReceiver();

        //Update messageBodyTranslated in case sendMassage() received a translated message
        chatMessage.setBodyTranslated(messageBodyTranslated);

        newChat = xmppService.getChatManager().createChat(messageReceiver);

        Boolean success = true;
        try {
            Log.d(TAG, "[SENDING MESSAGE] Body: " + messageBodyTranslated + " | User: " + messageReceiver + " | ThreadID: " + newChat.getThreadID());
            newChat.sendMessage(messageBodyTranslated);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        //Save messages history
        if (success) {
            //"contact" in database must be only de username portion of the JID
            chatMessage.setReceiver(XmppStringUtils.parseLocalpart(chatMessage.getReceiver()));
            db.insert(chatMessage);
        }
    }
}


