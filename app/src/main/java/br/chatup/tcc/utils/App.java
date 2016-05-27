package br.chatup.tcc.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Luan on 4/2/2016.
 */
public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext(){
        //return instance;
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
