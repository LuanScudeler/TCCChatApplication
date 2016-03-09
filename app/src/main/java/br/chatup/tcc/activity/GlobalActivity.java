package br.chatup.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.utils.JsonParser;

public class GlobalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User usr = null;
        Intent i;

        try {
            usr = CacheStorage.readUserInfo(this);
            if( usr == null) {
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
            }
            else {

                i = new Intent(this, MainActivity.class);

                Bundle b = new Bundle();
                String json = JsonParser.toJson(usr);

                b.putString("user",json);

                i.putExtras(b);

                startActivity(i);

                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
