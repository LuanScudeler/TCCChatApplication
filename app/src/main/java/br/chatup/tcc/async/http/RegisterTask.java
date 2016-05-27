package br.chatup.tcc.async.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpStatus;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;

/**
 * Created by jadson on 3/16/16.
 */
public class RegisterTask extends AsyncTask<String, Void, HttpStatus> {

    private ProgressDialog pDialog;
    private Context context;
    private AsyncTaskListener listener;
    private static final String TAG = Constants.LOG_TAG + RegisterTask.class.getSimpleName();

    public RegisterTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        String message = context.getString(R.string.wait);
        pDialog.setMessage(message);
        pDialog.show();
    }

    @Override
    protected HttpStatus doInBackground(String... params) {

        Log.i(TAG,
                String.format("doInBackground: Initiating connection to register new user (%s) to server address %s",
                        params[0],
                        Constants.FULL_SERVER_ADDR));

        HttpStatus status = RestFacade.post(Constants.FULL_SERVER_ADDR, params[0]);

        Log.i(TAG, "doInBackground: Response Status: " + status.toString());

        return status;
    }

    @Override
    protected void onPostExecute(HttpStatus s) {
        pDialog.cancel();
        listener.onTaskCompleted(s, this);
    }


}
