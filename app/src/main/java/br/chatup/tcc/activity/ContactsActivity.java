package br.chatup.tcc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.chatup.tcc.adapters.ContactsListAdapter;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

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
            Log.d(TAG, "onServiceConnected: " + xmppService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onStart() {
        Log.d(TAG, "ON_START");
        Intent i = new Intent(ContactsActivity.this, XmppService.class);
        bindService(i, mConnection, 0);
        super.onStart();
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

        contactsListView = (ListView)findViewById(R.id.lvContacts);
        customAdapter = new ContactsListAdapter(this,
                R.layout.contact_list_itemlistrow,
                entriesList);
        contactsListView.setAdapter(customAdapter);
        contactsListView.setOnItemClickListener(openChatActivity());

        //Load list of contacts in the screen
        LoadRosterTask lrt = new LoadRosterTask();
        lrt.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search_contacts_item_menu) {
            Intent i = new Intent(getBaseContext(), SearchContactActivity.class);
            startActivity(i);
        }
        return true;
    }

    private AdapterView.OnItemClickListener openChatActivity() {
        return(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String contactJIDSelected = entriesList.get(position).getUser();
                Log.i(TAG, "onItemClick: " + contactJIDSelected);
                Intent i = new Intent(ContactsActivity.this, ChatActivity.class);
                i.putExtra("contactJID", contactJIDSelected);
                startActivity(i);
            }
        });
    }


    private void addUserForTest(Roster roster, String userJID, String nickName) {
        try {
            roster.createEntry(userJID, nickName ,null);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void startRosterPresenceListener(Roster roster){

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
            pDialog.setMessage("Loading...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(!serviceConnected);
            Log.i(TAG, "doInBackground: service connected...");

            //Set subscriptionMode on "accept_all" users will automatically accept invite requests
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

            Roster roster = Roster.getInstanceFor(xmppService.getXmppManager().getConn());
            startRosterPresenceListener(roster);

            //Add a roster to the current user for testing purposes
            //String userJID = "luan@10.172.32.71";
            //String nickName = "luan";
            //addUserForTest(roster, userJID, nickName);

            //Get all rosters
            Collection<RosterEntry> entries = roster.getEntries();
            entriesList.addAll(entries);

            for (RosterEntry entry : entriesList) {
                Log.d(TAG, "Entry: JID: " +  entry.getUser() + " Nickname: " + entry.getName());
            }
            //update the list
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
