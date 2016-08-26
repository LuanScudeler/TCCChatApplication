package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.chat.ChatManager;

import java.io.IOException;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.chat.ChatListener;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.MessageService;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Util.getTagForClass(MainActivity.class);
    private TextView txtHelloUser;
    private TextView txtUsernameNavHeader;
    private TextView txtEmailNavHeader;
    private ImageView imgViewUserPhoto;
    private ProgressDialog pDialog;
    private Intent i;
    private static boolean serviceConnected;
    private boolean created;
    private boolean connected;
    private static XmppService xmppService;
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
        Log.d(TAG, "ON_START");
        try {
            User user = CacheStorage.getActiveUser(this);
            if (user != null) {
                bindService(i, mConnection, 0);
                InitConnectionTask iconn = new InitConnectionTask();
                if (!connected) {
                    iconn.execute(user);
                    connected = true;
                }
            }
            else {
                backToLogin();
            }
        } catch (IOException e) {
            e.printStackTrace();
            backToLogin();
        }
    }

    //TODO: Manage broadcastReceiver properly throughout the activity lifecycle
    @Override
    protected void onResume() {
        // Register to receive messages.
        // Registering an observer (mMessageReceiver) to receive Intents
        Log.d(TAG, "ON_RESUME");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("receivedMessage"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "ON_PAUSE");
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
//        stopService(i); //Do not stop service here, for receiving message even with the application closed
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Starting services, it will be kept started through the whole application. Activities will be able
        to bind to it when access to service is required*/
        i = new Intent(getBaseContext(), XmppService.class);
        Intent i2 = new Intent(getBaseContext(), MessageService.class);
        //Starting XmppService
        startService(i);
        //Starting MessageService
        startService(i2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtHelloUser = (TextView) findViewById(R.id.txtHelloUser);
        txtUsernameNavHeader = (TextView) findViewById(R.id.txtUsername_NavHeader);
        txtEmailNavHeader = (TextView) findViewById(R.id.txtEmail_NavHeader);
    }

    public void backToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
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
            xmppService.disconnect();
            xmppService.stopSelf();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class InitConnectionTask extends AsyncTask<User, Void, Void> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Connecting...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(User... users) {
            while (!serviceConnected);//empty loop to give time for service binding
            try {
                xmppService.init(users[0]);
                xmppService.connect();
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void status) {
            pDialog.cancel();
            if(xmppService != null) {
                txtHelloUser.setText(xmppService.getXmppManager().getUser().getUsername());
                txtUsernameNavHeader.setText(xmppService.getXmppManager().getUser().getUsername());
                txtEmailNavHeader.setText(xmppService.getXmppManager().getUser().getEmail());
                //After connection established create chat listener for receiving incoming messages
                ChatManager.getInstanceFor(xmppService.getXmppManager().getConn()).addChatListener(new ChatListener(MainActivity.this));
            }
            else {
                CacheStorage.deactivateUser(MainActivity.this);
                MainActivity.this.backToLogin();
            }

            //After connection established create chat listener for receiving incoming messages
            if(!created) { //TODO: This can probably be removed, asyncTask execution already has a flag called "connected"
                ChatManager.getInstanceFor(xmppService.getXmppManager().getConn()).addChatListener(new ChatListener(MainActivity.this));
                created = true;
            }
        }
    }
}