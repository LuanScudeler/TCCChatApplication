package br.chatup.tcc.xmpp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import br.chatup.tcc.activity.LoginActivity;
import br.chatup.tcc.activity.MainActivity;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.AsyncTaskResult;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 3/12/2016.
 */
public class XmppManager extends App {

    private static final String TAG = Util.getTagForClass(XmppManager.class);

    private static AbstractXMPPConnection conn;
    private static final Integer REPLAY_TIMEOUT = 20000;
    private User user;
    private Activity currActivity;
    private ProgressDialog pDialog;
    private AlertDialog alert;

    public XmppManager(User user) {
        this.user = user;
    }

    public void init() {
        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(user.getUsername(), user.getPassword())
                .setHost(Constants.XMPP_SERVER_IP)
                .setPort(Constants.XMPP_SERVER_PORT)
                .setServiceName(Constants.XMPP_SERVER_IP)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setSendPresence(true)
                .setDebuggerEnabled(true)
                .build();

        //Try to avoid SASL error when authenticating user in the server
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

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
                Log.i(TAG, "[connectionClosed]: " + user);
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.i(TAG, "[connectionClosedOnError] " + user);
                currActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(App.getCurrentActivity(), R.string.connection_error, R.string.try_to_reconnect);
                    }
                });
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

    public void showAlertDialog (Activity currActivity, int title, int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currActivity);
        builder.setTitle(currActivity.getResources().getString(title))
                .setMessage(currActivity.getResources().getString(msg))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        backToLogin();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void backToLogin() {
        disconnect();

        Intent i = new Intent(App.getCurrentActivity(), LoginActivity.class);

        App.getCurrentActivity().startActivity(i);
        App.getCurrentActivity().finish();
    }

    public void disconnect() {
        Log.i(TAG, "[DISCONNECTING] Disconnecting from server" );
        conn.disconnect();
    }

    public void connect() {
        AsyncTask<Void, Void, AsyncTaskResult> connThread = new AsyncTask<Void, Void, AsyncTaskResult>() {

            @Override
            protected void onPreExecute() {
                currActivity = App.getCurrentActivity();
                Log.d(TAG, "[CurrActivity] " + currActivity.getLocalClassName());
                pDialog = new ProgressDialog(currActivity);
                pDialog.setMessage(currActivity.getResources().getString(R.string.please_wait));
                pDialog.show();
            }
            @Override
            protected AsyncTaskResult doInBackground(Void... voids) {
                try {
                    conn.connect();
                    conn.login();
                } catch (SmackException e) {
                    e.printStackTrace();
                    return new AsyncTaskResult(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new AsyncTaskResult(e);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    return new AsyncTaskResult(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(AsyncTaskResult result) {
                if(result!=null){
                    pDialog.cancel();
                    Toast.makeText(currActivity, currActivity.getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }else{
                    XMPPRetrievingUserDataTask xmppTask = new XMPPRetrievingUserDataTask();
                    xmppTask.execute(user);
                }
            }
        };
        connThread.execute();
    }

    public class XMPPRetrievingUserDataTask extends AsyncTask<User, Void, User> {

        @Override
        protected void onPreExecute() {
            pDialog.setMessage(currActivity.getResources().getString(R.string.retrieving_data));
        }

        @Override
        protected User doInBackground(User... params) {

            User user = params[0];
            try {
                String url = Constants.RESTAPI_USERS_URL + "/" + user.getUsername();
                ResponseEntity<String> resp = RestFacade.get(url);
                if(resp.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return null;
                }
                else {
                    Log.i(TAG, "doInBackground: " + resp.getBody());
                    User u = JsonParser.fromJson(User.class, resp.getBody());
                    user.setEmail(u.getEmail());
                    user.setProperties(u.getProperties());
                    user.setName(u.getName());
                    return user;
                }

            }
            catch(Exception ex) {
                Log.e(TAG, "doInBackground: ", ex);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            pDialog.cancel();
            Log.d(TAG, "[CurrActivity] " + currActivity.getLocalClassName());
            if (currActivity.getClass().getSimpleName().equals("MainActivity")){
                Log.d(TAG, "Connected");
            }else{
                CacheStorage.activateUser(currActivity, user);
                Intent i = new Intent(currActivity, MainActivity.class);
                currActivity.startActivity(i);
                currActivity.finish();
            }
        }
    }

    public User getUser() {return user;}

    public static AbstractXMPPConnection  getConn(){ return conn; }
}
