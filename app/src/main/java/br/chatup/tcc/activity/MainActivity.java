package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.chatup.tcc.adapters.ActiveChatsListAdapter;
import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;
import br.chatup.tcc.xmpp.XmppManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Util.getTagForClass(MainActivity.class);
    private static final String TRANSLATION_MODE_DEFAULT_VAL = "0";
    private TextView txtUsernameNavHeader;
    private TextView txtEmailNavHeader;
    private TextView txtActiveChatsNotification;
    private ActiveChatsListAdapter customAdapter;
    private ListView activeChatsListView;
    private ImageView imgViewUserPhoto;
    private ProgressDialog pDialog;
    private Intent xmppServiceIntent;
    private static boolean serviceConnected;
    private static XmppService xmppService;
    private User user;
    List<String> contactsFromActiveChatsList = new ArrayList<String>();
    private AppDataSource db;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppService = ((LocalBinder<XmppService>) iBinder).getService();
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatMessage message = (ChatMessage)intent.getSerializableExtra("message");
            Log.d(TAG, "[BroadcastReceiver] Message received: " + message.getBody());

            Toast.makeText(getApplicationContext(), message.getReceiver() + ": "
                    + message.getBody(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(xmppServiceIntent, mConnection, 0);
    }

    @Override
    protected void onResume() {
        App.setCurrentActivity(this);
        // Register to receive messages.
        // Registering an observer (mMessageReceiver) to receive Intents
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("receivedMessage"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(serviceConnected)
            unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);

        txtUsernameNavHeader = (TextView) header.findViewById(R.id.txtUsername_NavHeader);
        txtEmailNavHeader = (TextView) header.findViewById(R.id.txtEmail_NavHeader);

        txtActiveChatsNotification = (TextView) findViewById(R.id.txtActiveChatsNotification);

        db = new AppDataSource(this);

        try {
            user = CacheStorage.getActiveUser(this);

            if (user != null) {
                //Initialize values on gui
                txtUsernameNavHeader.setText(user.getUsername());
                txtEmailNavHeader.setText(user.getEmail());

                initUserPreferences();
                initActiveChats();

                xmppServiceIntent = new Intent(getBaseContext(), XmppService.class);
                xmppServiceIntent.putExtra("user", JsonParser.toJson(user));
                /*Starting services, it will be kept started through the whole application. Activities will be able
                to bind to it when access to service is required*/
                //Check if conn is null, this happens in case the app was closed and reopened. And user is in cache
                if(XmppManager.getConn()==null)
                    startService(xmppServiceIntent);
            }
            else {
                backToLogin();
            }
        } catch (IOException e) {
            e.printStackTrace();
            backToLogin();
        }
    }

    private void initUserPreferences() {

        //Init translationMode preference
        int currTranslationModeValue = db.findTranslationMode("translationMode");
        if (currTranslationModeValue == -1) {
            Log.d(TAG, "Initiating translationMode preferences...");
            db.insertPreference("translationMode", TRANSLATION_MODE_DEFAULT_VAL);
            App.setTranslationEnabled(false);
        } else {
            App.setTranslationEnabled(currTranslationModeValue!=0);
        }
    }

    public void initActiveChats() {
        contactsFromActiveChatsList = db.findContactsFromActiveChats();

        if (contactsFromActiveChatsList.isEmpty()) {
            txtActiveChatsNotification.setVisibility(View.VISIBLE);
        }

        activeChatsListView = (ListView) findViewById(R.id.lvActiveChats);
        customAdapter = new ActiveChatsListAdapter(MainActivity.this,
                R.layout.active_chats_list_item,
                contactsFromActiveChatsList);
        activeChatsListView.setAdapter(customAdapter);
        activeChatsListView.setOnItemClickListener(openChatActivity());
        ((ArrayAdapter) activeChatsListView.getAdapter()).notifyDataSetChanged();
    }

    public void backToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private AdapterView.OnItemClickListener openChatActivity() {
        return (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Concatenates: username + @ + XMPP_SERVER_IP
                String contactJIDSelected = XmppStringUtils.completeJidFrom(contactsFromActiveChatsList.get(position), Constants.XMPP_SERVER_IP);
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                i.putExtra("contactJID", contactJIDSelected);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_contacts) {
            Intent i = new Intent(this, ContactsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            CacheStorage.deactivateUser(this);
            App.setLogoutRequested(true);
            //Disconnect from the server and destroy the service
            xmppService.stopSelf();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}