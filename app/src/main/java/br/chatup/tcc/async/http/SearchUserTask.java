package br.chatup.tcc.async.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;

/**
 * Created by jadson on 3/27/16.
 */
public class SearchUserTask extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog pDialog;
    private AsyncTaskListener listener;

    public SearchUserTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.wait));
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        //Checking user
        String resp = RestFacade.get(String.format(Constants.FIND_USER_PATH, params[0]));

        return resp;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onTaskCompleted(s, this);
        pDialog.cancel();
    }
}
