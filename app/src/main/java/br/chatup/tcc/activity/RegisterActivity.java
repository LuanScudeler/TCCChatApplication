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

import org.springframework.http.HttpStatus;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

public class RegisterActivity extends AppCompatActivity {

	private ProgressDialog pDialog;
	private EditText edtName;
	private EditText edtUsername;
	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtRepeatPassword;
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

		String name = null, username = null, email = null, password = null, repeatPassword = null;

		if(!validForm()) {
			Toast.makeText(this, getResources().getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show();
		} else if(!edtPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
			Toast.makeText(this, getResources().getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show();
		} else {

			name = edtName.getText().toString();
			username = edtUsername.getText().toString();
			email = edtEmail.getText().toString();
			password = edtPassword.getText().toString();

			User user = new User(username, password, name, email, null);

			RegisterTask rAsync = new RegisterTask();

			rAsync.execute(user);

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

	public class RegisterTask extends AsyncTask<User, Void, User> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(RegisterActivity.this);
			String message = getResources().getString(R.string.please_wait);
			pDialog.setMessage(message);
			pDialog.show();
		}

		@Override
		protected User doInBackground(User... params) {
			HttpStatus status = null;
			User newUser = params[0];
			try {
				String json =
						"{" +
							"\"username\": \"%s\", " +
								"\"password\": \"%s\", " +
								"\"name\": \"%s\", " +
								"\"email\": \"%s\" " +
						"}";
				status = RestFacade.post(Constants.RESTAPI_USER_URL, String.format(json,
						newUser.getUsername(),
						newUser.getPassword(),
						newUser.getName(),
						newUser.getEmail()));
				if(status == null || !status.equals(HttpStatus.CREATED)) {
					newUser = null;
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				newUser = null;
			}
			return newUser;
		}

		@Override
		protected void onPostExecute(User user) {
			pDialog.cancel();
			if(user != null) {
				try {
					Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
					Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registered_successfully), Toast.LENGTH_SHORT).show();
					startActivity(i);
					finish();
				} catch(Exception e) {
					Log.e(TAG, "onPostExecute: ", e);
				}

				Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(RegisterActivity.this, getResources().getString(R.string.sorry_an_error_occured), Toast.LENGTH_SHORT).show();
			}
		}


	}

}
