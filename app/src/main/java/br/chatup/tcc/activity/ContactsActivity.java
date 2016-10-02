package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;

import br.chatup.tcc.adapters.ContactsListAdapter;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Util;

public class ContactsActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    private ArrayList<RosterEntry> entriesList = new ArrayList<RosterEntry>();
    private ContactsListAdapter customAdapter;
    private ListView contactsListView;
    private static final String TAG = Util.getTagForClass(ContactsActivity.class);
    private static boolean serviceConnected;
    private static XmppService xmppService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppService = ((LocalBinder<XmppService>) iBinder).getService();
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onStart() {
        Log.d(TAG, "ON_START");
        super.onStart();
        Intent i = new Intent(ContactsActivity.this, XmppService.class);
        bindService(i, mConnection, 0);
        //Load list of contacts in the screen
        LoadRosterTask lrt = new LoadRosterTask();
        lrt.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SearchContactActivity.class);
                startActivity(i);
            }
        });
    }


    private AdapterView.OnItemClickListener openChatActivity() {
        return (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contactJIDSelected = entriesList.get(position).getUser();
                Log.d(TAG, "[contactJIDSelected]: " + contactJIDSelected);
                Intent i = new Intent(ContactsActivity.this, ChatActivity.class);
                i.putExtra("contactJID", contactJIDSelected);
                startActivity(i);
            }
        });
    }

    public void startRosterPresenceListener(Roster roster) {

        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {
                Log.i(TAG, "entriesAdded: ");
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                Log.i(TAG, "entriesUpdated: ");
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                Log.i(TAG, "entriesDeleted: ");
            }

            @Override
            public void presenceChanged(Presence presence) {
                Log.d(TAG, "PRESENCE CHANGED: FROM: " + presence.getFrom() + " STATUS: " + presence);
            }
        });
    }

    class LoadRosterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ContactsActivity.this);
            pDialog.setMessage(getString(R.string.please_wait));
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!serviceConnected) ;
            Log.i(TAG, "doInBackground: service connected...");

            //Set subscriptionMode on "accept_all" users will automatically accept invite requests
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

            Roster roster = Roster.getInstanceFor(xmppService.getXmppManager().getConn());
            startRosterPresenceListener(roster);

            //Add a roster to the current user for testing purposes
            //String userJID = "luan@10.172.32.71";
            //String nickName = "luan";
            //addUserForTest(roster, userJID, nickName);
            entriesList = new ArrayList<RosterEntry>();
            //Get all rosters
            Collection<RosterEntry> entries = roster.getEntries();
            entriesList.addAll(entries);

            for (RosterEntry entry : entriesList) {
                //Log.d(TAG, "Entry: JID: " + entry.getUser() + " Nickname: " + entry.getName());
            }
            //update the list
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contactsListView = (ListView) findViewById(R.id.lvContacts);
                    customAdapter = new ContactsListAdapter(ContactsActivity.this,
                            R.layout.contact_list_itemlistrow,
                            entriesList);
                    contactsListView.setAdapter(customAdapter);
                    contactsListView.setOnItemClickListener(openChatActivity());
                    ((ArrayAdapter) contactsListView.getAdapter()).notifyDataSetChanged();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.cancel();
        }
    }
}
