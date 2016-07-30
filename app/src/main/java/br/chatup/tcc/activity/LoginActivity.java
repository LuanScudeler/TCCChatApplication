package br.chatup.tcc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressDialog pDialog;
    private Button btnLogin;
    private XmppManager xmppManager;

    private static final String TAG = Constants.LOG_TAG + LoginActivity.class.getSimpleName();

    /*@Override
    public void onTaskCompleted(Object result, Object caller) {

        //TODO XGH logic -- XGH in progress...

        if(caller instanceof RegisterTask) {
            if( (OperationStatus) result == OperationStatus.ERROR ){
                Toast.makeText(this, "Cannot connect", Toast.LENGTH_SHORT).show();
            }
            else if((OperationStatus) result == OperationStatus.SUCESS ) {
                //Intent i = new Intent(this, )
            }
        }
        else if(caller instanceof SearchUserTask) {
            Toast.makeText(this, (String) result, Toast.LENGTH_SHORT).show();
        }
        else if (caller instanceof XMPPLoginTask) {
            if( (OperationStatus) result == OperationStatus.ERROR ){
                Toast.makeText(this, "Login Error =/", Toast.LENGTH_SHORT).show();
            }
            else if((OperationStatus) result == OperationStatus.SUCESS ) {
                Toast.makeText(this, "Login Successfull =)", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("user","{\"username\": \"Gambeta Monstra\"}");
                i.putExtras(b);
                startActivity(i);
                finish();
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername_Login);
        edtPassword = (EditText) findViewById(R.id.edtPassword_Login);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }

    public void btnRegisterClick(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void btnLoginClick(View v) {

        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        User user = new User(username, password, null, null);

        if(username.isEmpty() || password.isEmpty()) {
            String advice = this.getResources().getString(R.string.please_type_email_pass);
            Toast.makeText(this, advice, Toast.LENGTH_SHORT).show();
        }
        else {
            XMPPLoginTask xmppTask = new XMPPLoginTask();
            xmppTask.execute(user);

            /*Intent i = new Intent(this, GlobalActivity.class);

            try {
                CacheStorage.storeUserInfo(user, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            startActivity(i);

            finish();*/
        }
    }
    public class XMPPLoginTask extends AsyncTask<User, Void, User> {
        private XMPPTCPConnection connection = null;
        private User user;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getResources().getString(R.string.conn_to_srv));
            pDialog.show();
        }

        @Override
        protected User doInBackground(User... params) {

            XmppManager xmppManager = new XmppManager();

            try {
                connection = xmppManager.initConnection();
                connection.login(params[0].getUsername(), params[0].getPassword());
                user = params[0];
            }
            catch(Exception ex) {
                Log.e(TAG, "doInBackground: ", ex);
                user = null;
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            pDialog.cancel();

            if(user != null) {
                try {
                    CacheStorage.storeUserInfo(user, LoginActivity.this);
                    Intent i = new Intent(LoginActivity.this, GlobalActivity.class);
                    startActivity(i);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(LoginActivity.this, "Erro no login", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(LoginActivity.this, "Erro no login", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
