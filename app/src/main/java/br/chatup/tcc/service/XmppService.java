package br.chatup.tcc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;

import br.chatup.tcc.activity.ChatActivity;
import br.chatup.tcc.activity.MainActivity;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.chat.ChatListener;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by Jadson on 10/08/2016.
 */
public class XmppService extends Service {

    private XmppManager xmppManager;
    private ProgressDialog pDialog;
    private AbstractXMPPConnection connection;
    private boolean connected;
    private static final String TAG = Util.getTagForClass(XmppService.class);

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("BROADCAST_ON_SERVICE")) {
                Log.i(TAG, "BroadCast received on service");
                ChatMessage message = (ChatMessage)intent.getSerializableExtra("message");
                String contactJID = XmppStringUtils.parseBareJid(message.getReceiver());
                raiseNotification(contactJID, message.getBody());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter it = new IntentFilter();
        it.addAction("BROADCAST_ON_SERVICE");
        it.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mMessageReceiver, it);
        //Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        //Toast.makeText(this, "Service Binded | Initializing listener", Toast.LENGTH_LONG).show();
        connection = XmppManager.getConn();
        initializeChatListener();
        return new LocalBinder<XmppService>(this);
    }

    private void initializeChatListener() {
        Log.i(TAG, "Initializing listener");
        ChatManager.getInstanceFor(connection).addChatListener(new ChatListener(getApplicationContext()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            User user = (User)intent.getSerializableExtra("user");
            Log.i(TAG, "User :" + user);
            xmppManager = new XmppManager((User)intent.getSerializableExtra("user"));
            if(!connected){
                xmppManager.init();
                xmppManager.connect();
                connected = true;
            } else {
                Log.i(TAG, "Already connected to server");
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        xmppManager.disconnect();
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void init(User user) throws XMPPException, IOException, SmackException {
        Log.i(TAG, "init: " + user);
        xmppManager = new XmppManager(user);
        xmppManager.init();
    }

    public void connect() {
        xmppManager.connect();
    }

    public void disconnect() {
        xmppManager.disconnect();
    }

    public XmppManager getXmppManager(){return xmppManager;}

    private void raiseNotification(String contactJID, String msgBody) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon_mdpi)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon_xhdpi))
                .setTicker("New message!")
                .setContentTitle("Message from: " + XmppStringUtils.parseLocalpart(contactJID))
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
}