package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

/**
 *
 * Classe responsável pelo controle da tela de login
 * @author <a href="mailto:jadson.oli@hotmail.com">Jadson de O. Rosa</a>
 *
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private ProgressDialog pDialog;
    private XmppManager xmppManager;

    private static final String TAG = Util.getTagForClass(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername_Login);
        edtPassword = (EditText) findViewById(R.id.edtPassword_Login);
        btnLogin = (Button) findViewById(R.id.btnLogin_LoginActivity);

        App.setCurrentActivity(LoginActivity.this);

        Log.d(TAG, "[CurrentActivity]: " + App.getCurrentActivity().getLocalClassName());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.please_type_email_pass),
                            Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(username, password, null, null, null);
                    Intent xmppServiceIntent = new Intent(getBaseContext(), XmppService.class);
                    xmppServiceIntent.putExtra("user", JsonParser.toJson(user));
                    /*Starting services, it will be kept started through the whole application. Activities will be able
                    to bind to it when access to service is required*/
                    startService(xmppServiceIntent);
                }
            }
        });
    }

    /**
     * Método invocado no evento click do botão btnLogin_LoginActivity
     * em activity_login.xml
     * @param v view de origem do evento
     */
    public void btnRegisterClick(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

}
