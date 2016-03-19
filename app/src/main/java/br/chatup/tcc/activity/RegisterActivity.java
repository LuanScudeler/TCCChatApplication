package br.chatup.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.chatup.tcc.async.AsyncTaskListener;
import br.chatup.tcc.async.RegisterTask;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;

public class RegisterActivity extends AppCompatActivity implements AsyncTaskListener {

    private RegisterTask rAsync;

    private EditText edtName;
    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRepeatPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = (EditText) findViewById(R.id.edtName_Register);
        edtUsername = (EditText) findViewById(R.id.edtUsername_Register);
        edtEmail = (EditText) findViewById(R.id.edtEmail_Register);
        edtPassword = (EditText) findViewById(R.id.edtPassword_Register);
        edtRepeatPassword = (EditText) findViewById(R.id.edtRepeatPassword_Register);

    }

    public void btnSubmitClick(View v) {

        String name = null, username = null, email = null, password = null, repeatPassword = null, userJson = null;

        if(!validForm()) {
            Toast.makeText(this, getResources().getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show();
        }
        else if(!edtPassword.getText().toString().equals(edtRepeatPassword.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show();
        }
        else {

            name = edtName.getText().toString();
            username = edtUsername.getText().toString();
            email = edtEmail.getText().toString();
            password = edtPassword.getText().toString();

            User user = new User(username, password, name, email);

            userJson = JsonParser.toJson(user);

            rAsync = new RegisterTask(this, this);

            rAsync.execute(userJson);

        }
    }

    private boolean validForm() {

        if(edtName.getText().toString().isEmpty()){
            edtName.requestFocus();
            return false;
        }

        if(edtUsername.getText().toString().isEmpty()){
            edtUsername.requestFocus();
            return false;
        }

        if(edtEmail.getText().toString().isEmpty()) {
            edtEmail.requestFocus();
            return false;
        }

        if(edtPassword.getText().toString().isEmpty()) {
            edtPassword.requestFocus();
            return false;
        }

        if(edtRepeatPassword.getText().toString().isEmpty()) {
            edtRepeatPassword.requestFocus();
            return false;
        }

        return true;

    }

    @Override
    public void onTaskCompleted(Object result) {

        String res = (String) result;

        if(res.equals(Constants.HTTP_SUCCESS_CODE)) {
            Toast.makeText(this, this.getResources().getString(R.string.user_registered_successfully), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, GlobalActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, this.getString(R.string.sorry), Toast.LENGTH_SHORT).show();
        }
    }
}
