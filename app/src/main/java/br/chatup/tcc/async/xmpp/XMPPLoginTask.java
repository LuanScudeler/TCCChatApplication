package br.chatup.tcc.async.xmpp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.bean.OperationStatus;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;

/**
 * Created by jadson on 3/26/16.
 * Execute Login to XMPP Server
 */
public class XMPPLoginTask extends AsyncTask<User, Void, OperationStatus> {

    private Context context;

    private AsyncTaskListener listener;

    private ProgressDialog pDialog;

    private static final String TAG = Constants.CHATUP_PREFIX_TAG + XMPPLoginTask.class.getSimpleName();

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

        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                .setHost(Constants.XMPP_SERVER_IP)
                .setServiceName(Constants.XMPP_SERVER_IP)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setSendPresence(true)
                .build();

       /* SASLMechanism mechanism = new SASLDigestMD5Mechanism();
        SASLAuthentication.registerSASLMechanism(mechanism);
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");*/

        XMPPTCPConnection connection = new XMPPTCPConnection(configuration);

        try {
            connection.connect();
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
