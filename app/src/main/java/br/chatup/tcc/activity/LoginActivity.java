package br.chatup.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }

    public void btnLoginClick(View v) {

        String email = edtEmail.getText().toString();

        String password = edtPassword.getText().toString();

        User user = new User(email, password);

        if(email == null || password == null) {
            Toast.makeText(this, "Email and Password are needed", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent i = new Intent(this, GlobalActivity.class);

            try {
                CacheStorage.storeUserInfo(user, this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            startActivity(i);

            finish();
        }
    }
}
