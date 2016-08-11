package br.chatup.tcc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.chatup.tcc.adapters.ContactsListAdapter;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

public class ContactsActivity extends AppCompatActivity {

    private ArrayList<RosterEntry> entriesList = new ArrayList<RosterEntry>();
    private ContactsListAdapter customAdapter;
    private ListView contactsListView;
    private static final String TAG = Constants.LOG_TAG + ContactsActivity.class.getSimpleName();
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //Set subscriptionMode on "accept_all" users will automatically accept invite requests
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

       // Roster roster = Roster.getInstanceFor(XmppManager.getConn());
        //startRosterPresenceListener(roster);

        //Add a roster to the current user for testing purposes
        String userJID = "a@192.168.0.103";
        String nickName = "a";
        //addUserForTest(roster, userJID, nickName);

        //Get all rosters
        //Collection<RosterEntry> entries = roster.getEntries();
        //entriesList.addAll(entries);

        for (RosterEntry entry : entriesList) {
            Log.d(TAG, "Entry: JID: " +  entry.getUser() + " Nickname: " + entry.getName());
        }

        contactsListView = (ListView)findViewById(R.id.lv);
        // get data from the table
        customAdapter = new ContactsListAdapter(this,
                R.layout.contact_list_itemlistrow,
                entriesList);

        contactsListView.setAdapter(customAdapter);

        contactsListView.setOnItemClickListener(openChatActivity());

    }

    private AdapterView.OnItemClickListener openChatActivity() {
        return(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String contactJIDSelected = ((TextView) view.findViewById(R.id.userJID)).getText().toString();
                Intent i = new Intent(activity, ChatActivity.class);
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

            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {

            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {

            }

            @Override
            public void presenceChanged(Presence presence) {
                Log.d(TAG, "PRESENCE CHANGED: FROM: " + presence.getFrom() + " STATUS: " + presence);
                //TODO Figure out a way to update contacts presence in the listview (Low priority task for now)
            }
        });
    }
}
