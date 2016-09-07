package br.chatup.tcc.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.chatup.tcc.adapters.ChatContainerAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

//TODO: Retrieve chat history when loading chatActivity
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG + ChatActivity.class.getSimpleName();
    private EditText edtMessageBody;
    private Chat newChat;
    private ListView messagesContainer;
    private ChatContainerAdapter chatContainerAdapter;
    private ProgressDialog pDialog;
    private String contactJID;
    private AppDataSource db;
    private String currActiveChat;

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
     * <p/>
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
                raiseNotification(contactJID, message.getBody());
            }
        }
    };

    //TODO: Move raiseNotification method to Utils class
    private void raiseNotification(String contactJID, String msgBody) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon_mdpi)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon_xhdpi))
                .setTicker("New message!")
                .setContentTitle(XmppStringUtils.parseLocalpart(contactJID))
                .setContentText(msgBody)
                .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("contactJID", contactJID);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Get Android notification service
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Configure
        Notification n = mBuilder.build();
        n.vibrate = new long[]{150, 300, 150, 600};

        // First parameter refers to notification id, notification can be modified later
        mNotificationManager.notify(R.drawable.notification_icon_mdpi, n);
    }

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

        db = new AppDataSource(this);
        edtMessageBody = (EditText) findViewById(R.id.edtMessage);
        messagesContainer = (ListView) findViewById(R.id.msgListView);

        contactJID = getIntent().getExtras().getString("contactJID").toString();
        //username for controlling notification behavior
        currActiveChat = XmppStringUtils.parseLocalpart(contactJID);

        String displayableUsername = Util.toCapital(XmppStringUtils.parseLocalpart(contactJID));
        ChatActivity.this.setTitle(displayableUsername);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                pDialog = new ProgressDialog(ChatActivity.this);
                pDialog.setMessage(Util.getStringResource(ChatActivity.this, R.string.please_wait));
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ResponseEntity<String> resp = RestFacade.get(String.format(Constants.RESTAPI_USER_URL, contactJID.split("@")[0]));
                User u = br.chatup.tcc.utils.JsonParser.fromJson(User.class, resp.getBody());
                Log.i(TAG, "doInBackground: " + u);
                return u.getProperties().get("property").getValue();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.cancel();
                langTo = s;
                initChatView();
                initChatData();
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
        chatMessageList = db.findAllByContact(XmppStringUtils.parseLocalpart(contactJID));
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
        if (!langTo.equals(xmppService.getXmppManager().getUser().getProperties().get("property").getValue())) {
            new AsyncTask<String, Void, String[]>() {
                String message, messageTranslated;

                @Override
                protected void onPreExecute() {
                    edtMessageBody.setEnabled(false);
                }

                @Override
                protected String[] doInBackground(String[] params) {
                    message = params[0];
                    message.replaceAll(" ", "%20");

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
                    edtMessageBody.setEnabled(true);
                    edtMessageBody.setText("");
                    sendMessage(s[0], s[1]);
                }
            }.execute(edtMessageBody.getText().toString());
        } else {
            sendMessage(edtMessageBody.getText().toString(), edtMessageBody.getText().toString());
        }
    }

    public void sendMessage(String messageBody, String messageBodyTranslated) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        ChatMessage chatMessage = new ChatMessage(messageBody, contactJID, true, time, messageBodyTranslated);
        final Message message = new Message();
        ChatManager chatManager = ChatManager.getInstanceFor(xmppService.getXmppManager().getConn());
        //Gets for whom the message will go for (retrieves a user JID: username@domain)
        String messageReceiver = chatMessage.getReceiver();
        if (!CacheStorage.getInstanceCachedChats().containsKey(messageReceiver)) {
            newChat = chatManager.createChat(messageReceiver);
            CacheStorage.addChatContact(messageReceiver, newChat.getThreadID());
            //Log.d(TAG, "[CHAT CREATED] Receiver not found in contacts cache. ADDING TO CACHE -> Contact: " + messageReceiver + " | ThreadID: " + newChat.getThreadID());
        } else {
            //Get chat threadID from cachedChats for the current contact that the user is chatting with
            newChat = chatManager.getThreadChat(CacheStorage.getInstanceCachedChats().get(contactJID));
            //Set on message the chat threadID that already exist in the cachedChats
            message.setThread(CacheStorage.getInstanceCachedChats().get(contactJID).toString());
            //Log.d(TAG, "[CHAT ALREADY OPENED] Setting threadID for reply. CACHED_THREAD-ID: " + CacheStorage.getInstanceCachedChats().get(contactJID).toString());
        }
        message.setBody(messageBodyTranslated);
        message.setType(Message.Type.chat);
        Boolean success = true;
        try {
            newChat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            success = false;
            e.printStackTrace();
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        //Save messages history
        if (success) {
            //"contact" in database must be only de username portion of the JID
            chatMessage.setReceiver(XmppStringUtils.parseLocalpart(chatMessage.getReceiver()));
            db.insert(chatMessage);
            displayMessage(chatMessage);
        }
    }

    //TODO: Allow user to answer the first message (GUI stuffs, list activity and user notification for incoming messages)
}


