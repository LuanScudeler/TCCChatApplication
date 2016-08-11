package br.chatup.tcc.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by Jadson on 10/08/2016.
 */
public class XmppService extends Service {

    private XmppManager xmppManager;
    private ProgressDialog pDialog;
    private boolean connected;
    private static final String TAG = Util.getTagForClass(XmppService.class);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return new LocalBinder<XmppService>(this);
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
