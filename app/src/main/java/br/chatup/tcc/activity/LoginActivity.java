package br.chatup.tcc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.xmpp.XmppManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressDialog pDialog;

    private static final String TAG = Constants.LOG_TAG + LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = (EditText) findViewById(R.id.edtUsername_Login);
        edtPassword = (EditText) findViewById(R.id.edtPassword_Login);
    }

    public void btnRegisterClick(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void btnLoginClick(View v) {

        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            String advice = this.getResources().getString(R.string.please_type_email_pass);
            Toast.makeText(this, advice, Toast.LENGTH_SHORT).show();
        }
        else {
            XMPPLoginTask xmppTask = new XMPPLoginTask();
            User user = new User(username, password, null, null);
            xmppTask.execute(user);
        }
    }

    public class XMPPLoginTask extends AsyncTask<User, Void, User> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getResources().getString(R.string.conn_to_srv));
            pDialog.show();
        }

        @Override
        protected User doInBackground(User... params) {

            User user = params[0];
            try {
                String url = Constants.RESTAPI_USER_URL + "/" + user.getUsername();
                ResponseEntity<String> resp = RestFacade.get(url);
                if(resp.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return null;
                }
                else {
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
            if(user == null) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
            else {
                CacheStorage.activateUser(LoginActivity.this, user);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

}
