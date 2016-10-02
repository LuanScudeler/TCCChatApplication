package br.chatup.tcc.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Luan on 4/2/2016.
 */
public class App extends Application {
    private static Activity mCurrentActivity = null;
    private static String mCurrentActiveChat = null;
    private static boolean translationEnabled;
    private static boolean logoutRequested;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity){
        mCurrentActivity = currentActivity;
    }

    public static String getCurrentActiveChat() {
        return mCurrentActiveChat;
    }

    public static void setCurrentActiveChat(String currentActiveChat){
        mCurrentActiveChat = currentActiveChat;
    }

    public static boolean isLogoutRequested() {
        return logoutRequested;
    }

    public static void setLogoutRequested(boolean logoutRequested) {
        App.logoutRequested = logoutRequested;
    }

    public static boolean isTranslationEnabled() {
        return translationEnabled;
    }

    public static void setTranslationEnabled(boolean translationEnabled) {
        App.translationEnabled = translationEnabled;
    }
}
