package br.chatup.tcc.xmpp;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by Luan on 3/12/2016.
 */
public class XmppManager {

    private static final int packetReplyTimeout = 500; // millis

    private String server;
    private int port;
    private AbstractXMPPConnection conn = null;

    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void initConnection() throws XMPPException {

        System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();

        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(server);
        config.setHost(server);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        conn = new XMPPTCPConnection(config.build());

        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.d("CONNECTION STATUS: ", "OPENED");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {

            }

            @Override
            public void connectionClosed() {
                Log.d("CONNECTION STATUS: ", "CLOSED");
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

        try {
            conn.connect();
        } catch (SmackException e) {
            e.printStackTrace();
            Log.d("DEU RUIM:", "CONEXAO COM SERVIDOR");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        conn.disconnect();
    }

    public void login(String acc, String pass){
        if (conn == null){
            Log.d("STATUS:", "CONNECTION NULL");
        }
        try {
            conn.login(acc, pass);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
