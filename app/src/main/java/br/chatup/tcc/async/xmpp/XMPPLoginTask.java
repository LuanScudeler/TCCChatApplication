package br.chatup.tcc.async.xmpp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import br.chatup.tcc.activity.GlobalActivity;
import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.bean.OperationStatus;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by jadson on 3/26/16.
 * Execute Login to XMPP Server
 */
public class XMPPLoginTask extends AsyncTask<User, Void, User> {

    private Activity activity;
    private AsyncTaskListener listener;
    private ProgressDialog pDialog;
    private XMPPTCPConnection connection = null;
    private User user;

    private static final String TAG = Constants.LOG_TAG + XMPPLoginTask.class.getSimpleName();

    public XMPPLoginTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getResources().getString(R.string.conn_to_srv));
        pDialog.show();
    }

    @Override
    protected User doInBackground(User... params) {

        XmppManager xmppManager = new XmppManager();

        try {
            connection = xmppManager.initConnection();
            Log.d(TAG, "------------User: " + params[0].getUsername() + "  /  Pass: " + params[0].getPassword());
            connection.login(params[0].getUsername(), params[0].getPassword());
            user = params[0];
        } catch (SmackException e) {
            Log.e(TAG, "doInBackground: ", e);
            //TODO logar os erros
            Toast.makeText(activity, "Erro no login", Toast.LENGTH_SHORT).show();
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
            //TODO logar os erros
            Toast.makeText(activity, "Erro no login", Toast.LENGTH_SHORT).show();
            return null;
        } catch (XMPPException e) {
            Log.e(TAG, "doInBackground: ", e);
            Toast.makeText(activity, "Erro no login", Toast.LENGTH_SHORT).show();
            //TODO logar os erros
            return null;
        }

        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        pDialog.cancel();

        if(user != null) {
            try {
                CacheStorage.storeUserInfo(user, activity);
                Intent i = new Intent(activity, GlobalActivity.class);
                activity.startActivity(i);
                activity.finish();
            } catch (IOException e) {
                Toast.makeText(activity, "Erro no login", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(activity, "Erro no login", Toast.LENGTH_SHORT).show();
        }

    }
}
