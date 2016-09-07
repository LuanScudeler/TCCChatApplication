package br.chatup.tcc.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Luan on 4/2/2016.
 */
public class App extends Application {
    public void onCreate() {
        super.onCreate();
    }

    public static Activity mCurrentActivity = null;
    public static String mCurrentActiveChat = null;
    public static boolean translationEnabled;

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

    public static boolean isTranslationEnabled() {
        return translationEnabled;
    }

    public static void setTranslationEnabled(boolean translationEnabled) {
        App.translationEnabled = translationEnabled;
    }
}
