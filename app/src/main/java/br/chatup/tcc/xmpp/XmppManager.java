package br.chatup.tcc.xmpp;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.chat.ChatListener;
import br.chatup.tcc.utils.Constants;

/**
 * Created by Luan on 3/12/2016.
 */
public class XmppManager {

    private static final String TAG = Constants.LOG_TAG + XmppManager.class.getSimpleName();

    static private XMPPTCPConnection conn = null;
    private static final Integer REPLAY_TIMEOUT = 20000;
    private Activity activity;

    public XMPPTCPConnection initConnection() throws XMPPException, IOException, SmackException {

        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                .setHost(Constants.XMPP_SERVER_IP)
                .setPort(Constants.XMPP_SERVER_PORT)
                .setServiceName(Constants.XMPP_SERVER_IP)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setSendPresence(true)
                .setDebuggerEnabled(true)
                .build();

        conn = new XMPPTCPConnection(configuration);
        conn.setPacketReplyTimeout(REPLAY_TIMEOUT);

        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.d(TAG, "CONN OPENED");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.d(TAG, "AUTHENTICATED!");
                // Chat listener created after user authentication, so user is ready to listen to incoming messages
                ChatManager.getInstanceFor(connection).addChatListener(new ChatListener());
            }

            @Override
            public void connectionClosed() {
                Log.d(TAG, "CONN CLOSED, CLEANNING CACHE");
                CacheStorage.deactivateUsers(activity);
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
        };

        conn.addConnectionListener(connectionListener);
        conn.connect();

        return conn;
    }

    private class OpenConnection extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (conn.isConnected())
                return false;

            try {
                conn.connect();
            } catch (SmackException e) {
                e.printStackTrace();
                Log.d(TAG, "CONN FAILED");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "CONNECTING...");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static void closeConnection() {
        conn.disconnect();
    }

    public static XMPPTCPConnection getConn(){ return conn; }
}
