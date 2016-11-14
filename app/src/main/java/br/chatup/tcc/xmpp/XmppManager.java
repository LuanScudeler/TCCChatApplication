package br.chatup.tcc.xmpp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
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
import br.chatup.tcc.service.LocalBinder;
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
    private Intent xmppServiceIntent;
    private static XmppService xmppService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppService = ((LocalBinder<XmppService>) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    //TODO: Test this better
/*    {
        Log.d(TAG, "Bind to service");
        Log.d(TAG, "[CurrentActivity]: " + App.getCurrentActivity().getLocalClassName());
        xmppServiceIntent = new Intent(App.getCurrentActivity(), XmppService.class);
        App.getCurrentActivity().bindService(xmppServiceIntent, mConnection, 0);
    }*/

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
                //If logoutRequest is false that means that the user didn't want to close the connection
                if(!App.isLogoutRequested()) {
                    currActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlertDialog(App.getCurrentActivity(), R.string.connection_error, R.string.try_to_reconnect);
                        }
                    });
                }else {
                    //Set LogoutRequested flag back to false
                    App.setLogoutRequested(false);
                }
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.i(TAG, "[connectionClosedOnError]: " + user);
                currActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(App.getCurrentActivity(), R.string.connection_error, R.string.try_to_reconnect);
                    }
                });
            }

            @Override
            public void reconnectionSuccessful() {
                Log.i(TAG, "[******* reconnectionSuccessful() *******] ");
            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.i(TAG, "[******* reconnectingIn() *******] ");
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
        xmppService.stopSelf();

        Intent i = new Intent(App.getCurrentActivity(), LoginActivity.class);

        App.getCurrentActivity().startActivity(i);
        App.getCurrentActivity().finish();
    }

    public void disconnect() {
        Log.i(TAG, "[DISCONNECTING] Disconnecting from server");
        conn.disconnect();
    }

    public void connect() {
        AsyncTask<Void, Void, AsyncTaskResult> connThread = new AsyncTask<Void, Void, AsyncTaskResult>() {

            @Override
            protected void onPreExecute() {
                currActivity = App.getCurrentActivity();
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
                    Log.i(TAG, "Fail on logging");
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
                    Log.i(TAG, "Fail on retrieving users data");
                    return null;
                }
                else {
                    Log.i(TAG, "User data retrieved: " + resp.getBody());
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
            if (currActivity.getClass().getSimpleName().equals("MainActivity")){
                Log.d(TAG, "User logged (User already in cache)");
            }else{
                Log.d(TAG, "User logged (Adding user to cache)");
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
