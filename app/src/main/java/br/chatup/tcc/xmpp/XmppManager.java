package br.chatup.tcc.xmpp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import br.chatup.tcc.activity.MainActivity;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.chat.ChatListener;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 3/12/2016.
 */
public class XmppManager {

    private static final String TAG = Util.getTagForClass(XmppManager.class);

    private static AbstractXMPPConnection conn;
    private static final Integer REPLAY_TIMEOUT = 20000;
    private static User user;

    public XmppManager(User user) {
        this.user = user;
    }

    public void init() throws XMPPException, IOException, SmackException {
        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(user.getUsername(), user.getPassword())
                .setHost(Constants.XMPP_SERVER_IP)
                .setPort(Constants.XMPP_SERVER_PORT)
                .setServiceName(Constants.XMPP_SERVER_IP)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setSendPresence(true)
                .setDebuggerEnabled(true)
                .build();

        conn = new XMPPTCPConnection(configuration);
        conn.setPacketReplyTimeout(REPLAY_TIMEOUT);
        conn.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.i(TAG, "User connected: " + user);
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.i(TAG, "User authenticated: " + user);
            }

            @Override
            public void connectionClosed() {

            }

            @Override
            public void connectionClosedOnError(Exception e) {

            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int seconds) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        });
    }

    public void disconnect() {
        conn.disconnect();
    }

    public void connect() {
        AsyncTask<Void, Void, Void> connThread = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                //TODO: Raise dialog to inform that connection is in progress (How to get context from here)
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    conn.connect();
                    conn.login();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        connThread.execute();
    }

    public User getUser() {return user;}

    public static AbstractXMPPConnection  getConn(){ return conn; }
}
