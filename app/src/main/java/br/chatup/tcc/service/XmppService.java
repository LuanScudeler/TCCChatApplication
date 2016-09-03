package br.chatup.tcc.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;

import java.io.IOException;

import br.chatup.tcc.activity.MainActivity;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.chat.ChatListener;
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

    @Override
    public void onCreate() {
        super.onCreate();
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
        super.onDestroy();
        Log.i(TAG, "onDestroy");
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

}