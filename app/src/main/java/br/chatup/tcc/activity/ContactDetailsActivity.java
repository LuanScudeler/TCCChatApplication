package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.util.XmppStringUtils;
import org.w3c.dom.Text;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.MessageService;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;

public class ContactDetailsActivity extends AppCompatActivity {

    private static final String TAG = Util.getTagForClass(ContactDetailsActivity.class);
    private ProgressDialog pDialog;
    private TextView txtUsername;
    private TextView txtEmail;
    private Button btnAddContact;
    private User contactSelected;
    private static boolean serviceConnected;
    private boolean created;
    private boolean connected;
    private static XmppService xmppService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppService = ((LocalBinder<XmppService>) iBinder).getService();
            serviceConnected = true;
            Log.d(TAG, "onServiceConnected: " + xmppService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!serviceConnected)
            bindService(new Intent(this, XmppService.class), mConnection, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        txtUsername = (TextView) findViewById(R.id.txtUsername_ContactDetails);
        txtEmail = (TextView) findViewById(R.id.txtEmail_ContactDetails);
        btnAddContact = (Button) findViewById(R.id.btnAddContact_ContactDetails);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactTask act = new AddContactTask();
                act.execute(contactSelected);
            }
        });

        if(getIntent().getExtras() != null) {
            contactSelected = JsonParser.fromJson(User.class, getIntent().getExtras().getString("contact"));
            txtUsername.setText(contactSelected.getName());
            txtEmail.setText(contactSelected.getEmail());
        }
        else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class AddContactTask extends AsyncTask<User, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ContactDetailsActivity.this);
            pDialog.setMessage(Util.getStringResource(ContactDetailsActivity.this, R.string.please_wait));
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(User... params) {
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

            Roster roster = Roster.getInstanceFor(xmppService.getXmppManager().getConn());

            User user = params[0];

            try {
                String jid = XmppStringUtils.completeJidFrom(user.getUsername(), xmppService.getXmppManager().getConn().getServiceName());
                Log.i(TAG, "doInBackground: Adding: " + user.getName() + " jid: " + jid);
                roster.createEntry(jid, user.getName(), null);
            } catch (SmackException.NotLoggedInException e) {
                Log.e(TAG, "doInBackground: ", e);
                return false;
            } catch (SmackException.NoResponseException e) {
                Log.e(TAG, "doInBackground: ", e);
                return false;
            } catch (XMPPException.XMPPErrorException e) {
                Log.e(TAG, "doInBackground: ", e);
                return false;
            } catch (SmackException.NotConnectedException e) {
                Log.e(TAG, "doInBackground: ", e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            pDialog.cancel();
            if(!b) {
                Toast.makeText(ContactDetailsActivity.this, Util.getStringResource(ContactDetailsActivity.this, R.string.sorry_an_error_occured), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ContactDetailsActivity.this, Util.getStringResource(ContactDetailsActivity.this, R.string.contact_added_successfully), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ContactDetailsActivity.this, ContactsActivity.class));
            }
        }
    }
}
