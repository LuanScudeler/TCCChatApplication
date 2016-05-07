package br.chatup.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;

public class GlobalActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG + GlobalActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User usr = null;
        String username = null;
        Intent i;

        try {
            username = CacheStorage.getActiveUser(this);

            if(username != null) {
                usr = CacheStorage.readUserInfo(this, username);
                if( usr == null) {
                    i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    i = new Intent(this, MainActivity.class);
                    String json = JsonParser.toJson(usr);

                    Bundle b = new Bundle();
                    b.putString("user",json);
                    i.putExtras(b);
                    startActivity(i);
                    finish();
                }
            } else {
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
            }

        } catch (IOException e) {
            Log.e(TAG, "onCreate: ", e);
        }

    }
}
