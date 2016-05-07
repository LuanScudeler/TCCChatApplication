package br.chatup.tcc.async.xmpp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.bean.OperationStatus;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by jadson on 3/26/16.
 * Execute Login to XMPP Server
 */
public class XMPPLoginTask extends AsyncTask<User, Void, OperationStatus> {

    private Context context;
    private AsyncTaskListener listener;
    private ProgressDialog pDialog;
    private XMPPTCPConnection connection = null;

    private static final String TAG = Constants.LOG_TAG + XMPPLoginTask.class.getSimpleName();

    public XMPPLoginTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.conn_to_srv));
        pDialog.show();
    }

    @Override
    protected OperationStatus doInBackground(User... params) {

        XmppManager xmppManager = new XmppManager();

        try {
            connection = xmppManager.initConnection();
            Log.d(TAG, "------------User: " + params[0].getUsername() + "  /  Pass: " + params[0].getPassword());
            connection.login(params[0].getUsername(), params[0].getPassword());
        } catch (SmackException e) {
            Log.e(TAG, "doInBackground: ", e);
            return OperationStatus.ERROR;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
            return OperationStatus.ERROR;
        } catch (XMPPException e) {
            Log.e(TAG, "doInBackground: ", e);
            return OperationStatus.ERROR;
        }

        return OperationStatus.SUCESS;
    }

    @Override
    protected void onPostExecute(OperationStatus status) {
        pDialog.cancel();
        listener.onTaskCompleted(status, this);
    }
}
