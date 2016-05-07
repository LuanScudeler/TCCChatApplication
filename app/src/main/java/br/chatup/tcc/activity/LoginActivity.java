package br.chatup.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.async.http.RegisterTask;
import br.chatup.tcc.async.http.SearchUserTask;
import br.chatup.tcc.async.xmpp.XMPPLoginTask;
import br.chatup.tcc.bean.OperationStatus;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

public class LoginActivity extends AppCompatActivity implements AsyncTaskListener{

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private XmppManager xmppManager;

    private static final String TAG = Constants.LOG_TAG + LoginActivity.class.getSimpleName();

    @Override
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
                b.putString("user","{\"username\": \"Gambeta Monstra\"}"); //TODO: Find a way to pass current user instance through onTaskCompleted
                i.putExtras(b);
                startActivity(i);
                finish();
            }
        }
    }

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
            XMPPLoginTask xmppTask = new XMPPLoginTask(this, this);
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
}
