package br.chatup.tcc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jxmpp.util.XmppStringUtils;

import br.chatup.tcc.activity.ChatActivity;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.chat.MessageListener;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by Jadson on 10/08/2016.
 */
public class XmppService extends Service {

    private XmppManager xmppManager;
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private static final String TAG = Util.getTagForClass(XmppService.class);

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("BROADCAST_ON_SERVICE")) {
            Log.i(TAG, "BroadCast received on service");
            ChatMessage message = (ChatMessage)intent.getSerializableExtra("message");
            String contactJID = XmppStringUtils.parseBareJid(message.getReceiver());
            Util.showNotification(getApplicationContext(), ChatActivity.class,contactJID, message.getBody());
        }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Creating service");

        IntentFilter it = new IntentFilter();
        it.addAction("BROADCAST_ON_SERVICE");
        it.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(mMessageReceiver, it);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");

        connection = XmppManager.getConn();
        initializeChatListener();

        return new LocalBinder<XmppService>(this);
    }

    private void initializeChatListener() {
        Log.i(TAG, "Initializing listeners");
        if(connection!=null){
            chatManager = ChatManager.getInstanceFor(connection);
            Log.i(TAG, chatManager.toString());
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    if (!createdLocally) {
                        chat.addMessageListener(new MessageListener(getApplicationContext()));
                    }
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[onStartCommand] Starting service");

        User user = JsonParser.fromJson(User.class, intent.getExtras().getString("user"));
        Log.i(TAG, "User :" + user);

        xmppManager = new XmppManager(user);
        xmppManager.init();

        if(!XmppManager.getConn().isConnected()) {
            xmppManager.connect();
        }else {
            Log.i(TAG, "User already connected");
        }

        //Prevents application from crashing when process is killed. Service doesn't restart automatically
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "[onDestroy] Destroying service");
        xmppManager.disconnect();
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public ChatManager getChatManager(){
        return chatManager;
    }

    public void disconnect(){
        xmppManager.disconnect();
    }

    public XmppManager getXmppManager(){
        return xmppManager;
    }

}