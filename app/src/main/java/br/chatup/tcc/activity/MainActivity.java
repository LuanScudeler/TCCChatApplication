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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.MessageService;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Util.getTagForClass(MainActivity.class);
    private TextView txtHelloUser;
    private TextView txtUsernameNavHeader;
    private TextView txtEmailNavHeader;
    private ImageView imgViewUserPhoto;
    private ProgressDialog pDialog;
    private Intent xmppServiceIntent;
    private static boolean serviceConnected;
    private boolean created;
    private boolean connected;
    private static XmppService xmppService;
    private User user;
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
        Log.d(TAG, "ON_STOP");
        super.onStop();
        if(serviceConnected)
            unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "ON_DESTROY");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHelloUser = (TextView) findViewById(R.id.txtHelloUser);
        txtUsernameNavHeader = (TextView) findViewById(R.id.txtUsername_NavHeader);
        txtEmailNavHeader = (TextView) findViewById(R.id.txtEmail_NavHeader);

        try {
            user = CacheStorage.getActiveUser(this);

            if (user != null) {
                //Initialize values on gui
                txtHelloUser.setText(user.getUsername());
                txtUsernameNavHeader.setText(user.getUsername());
                txtEmailNavHeader.setText(user.getEmail());
                xmppServiceIntent = new Intent(getBaseContext(), XmppService.class);
                xmppServiceIntent.putExtra("user", JsonParser.toJson(user));
                //Initialize values on gui
                String displayableUsername = Util.toCapital(user.getUsername());
                /*Starting services, it will be kept started through the whole application. Activities will be able
                to bind to it when access to service is required*/
                startService(xmppServiceIntent);
            }
            else {
                backToLogin();
            }
        } catch (IOException e) {
            e.printStackTrace();
            backToLogin();
        }

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
            //Disconnect from the server and destroy the service
            xmppService.stopSelf();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chats, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toggle_translation) {
            if(App.isTranslationEnabled())
                App.setTranslationEnabled(false);
            else
                App.setTranslationEnabled(true);
            String currTranslationMode = App.isTranslationEnabled()?"ON":"OFF";
            Toast.makeText(getApplicationContext(), "Translation mode: " + currTranslationMode, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}