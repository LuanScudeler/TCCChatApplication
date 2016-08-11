package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.service.LocalBinder;
import br.chatup.tcc.service.XmppService;
import br.chatup.tcc.utils.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Util.getTagForClass(MainActivity.class);
    private TextView txtHelloUser;
    private ImageView imgViewUserPhoto;
    private ProgressDialog pDialog;
    private static boolean serviceConnected;
    private static XmppService xmppService = null;
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
        try {
            User user = CacheStorage.getActiveUser(this);
            if (user != null) {
                Intent i = new Intent(getBaseContext(), XmppService.class);
                bindService(i, mConnection, Context.BIND_AUTO_CREATE);
                InitConnectionTask iconn = new InitConnectionTask();
                iconn.execute(user);
            }
            else {
                backToLogin();
            }
        } catch (IOException e) {
            e.printStackTrace();
            backToLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            } catch (XMPPException e) {
                Log.e(TAG, "doInBackground: ", e);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: ", e);
            } catch (SmackException e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void status) {
            pDialog.cancel();
            if(xmppService != null) {
                txtHelloUser.setText(xmppService.getXmppManager().getUser().getUsername());
            }
        }
    }
}