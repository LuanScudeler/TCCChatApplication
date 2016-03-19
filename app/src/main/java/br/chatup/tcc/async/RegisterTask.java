package br.chatup.tcc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.chatup.tcc.activity.GlobalActivity;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;

/**
 * Created by jadson on 3/16/16.
 */
public class RegisterTask extends AsyncTask<String, String, String> {

    private ProgressDialog pDialog;
    private Context context;
    private AsyncTaskListener listener;
    private static final String TAG = "CHATUP-" + RegisterTask.class.getSimpleName();

    public RegisterTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        String message = context.getString(R.string.conn_to_srv);
        pDialog.setMessage(message);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String response = "";

        String fullAddr = Constants.XMPP_SERVER_IP
                .concat(":")
                .concat(String.valueOf(Constants.XMPP_SERVER_PORT_REGISTER))
                .concat(Constants.XMPP_SERVER_REGISTER_PATH);

        // set the Content-Type header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json"));
        headers.add("authorization", "9pbD8kcBKNzXYNPT");
        HttpEntity<String> entity = new HttpEntity<String>(params[0], headers);

        // create a new restTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String

        try {

            Log.i(TAG, "doInBackground: Sending data to server");

            ResponseEntity<String> responseEntity = restTemplate.exchange(fullAddr, HttpMethod.POST, entity, String.class);

            response = responseEntity.getStatusCode().toString();

            Log.i(TAG, "doInBackground: Response Status: " + response);

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return Constants.ERROR_CODE;
        }


        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        pDialog.cancel();
        listener.onTaskCompleted(s);
    }
}
