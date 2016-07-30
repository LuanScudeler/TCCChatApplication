package br.chatup.tcc.activity;

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

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

public class RegisterActivity extends AppCompatActivity {

	private ProgressDialog pDialog;
	private XMPPTCPConnection connection = null;
	private EditText edtName;
	private EditText edtUsername;
	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtRepeatPassword;
	private User user;
	private static final String TAG = Util.getTagForClass(RegisterActivity.class);

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
		} else if(!edtPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
			Toast.makeText(this, getResources().getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show();
		} else {

			name = edtName.getText().toString();
			username = edtUsername.getText().toString();
			email = edtEmail.getText().toString();
			password = edtPassword.getText().toString();

			user = new User(username, password, name, email);

			userJson = JsonParser.toJson(user);

			RegisterTask rAsync = new RegisterTask();

			rAsync.execute(userJson);

		}
	}

	private boolean validForm() {

		if(edtName.getText().toString().isEmpty()) {
			edtName.requestFocus();
			return false;
		}

		if(edtUsername.getText().toString().isEmpty()) {
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

	public class RegisterTask extends AsyncTask<String, Void, HttpStatus> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(RegisterActivity.this);
			String message = getResources().getString(R.string.wait);
			pDialog.setMessage(message);
			pDialog.show();
		}

		@Override
		protected HttpStatus doInBackground(String... params) {
			HttpStatus status = null;
			try {
				status = RestFacade.post(Constants.FULL_SERVER_ADDR, params[0]);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(HttpStatus s) {
			pDialog.cancel();
			if(s != null && s.equals(HttpStatus.CREATED)) {
				Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registered_successfully), Toast.LENGTH_SHORT).show();

				try {
					CacheStorage.storeUserInfo(user, RegisterActivity.this);
					//connection = xmppManager.initConnection();
				} catch(IOException e) {
					Log.e(TAG, "onPostExecute: ", e);
					//e.printStackTrace();
				} /*catch (SmackException e) {
	                e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }*/

				Intent i = new Intent(RegisterActivity.this, GlobalActivity.class);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(RegisterActivity.this, getResources().getString(R.string.sorry), Toast.LENGTH_SHORT).show();
			}
		}


	}

}
