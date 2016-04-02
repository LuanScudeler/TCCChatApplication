package br.chatup.tcc.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Luan on 4/2/2016.
 */
public class App extends Application {
    public static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
