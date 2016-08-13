package br.chatup.tcc.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.Util;

/**
 * Created by jnde on 08/03/2016.
 */
public class CacheStorage {
    private static final String CURRENT_ACTIVE_USER_FILE = "active.json";
    private static final String CACHE_DIR = "cache";

    private static HashMap<String, String> openChats = new HashMap<String, String>();;

    private static final String TAG = Util.getTagForClass(CacheStorage.class);

    public static boolean activateUser(Activity activity, User user) {
        boolean ret = false;
        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);
        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);
        File activeUserFile  = new File(activeUserFilePath);
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(activeUserFile));
            dos.writeUTF(JsonParser.toJson(user));
            ret = true;
            if(dos != null) dos.close();
        } catch(Exception e) {
            Log.e(TAG, "activateUser: ", e);
            ret = false;
        }
        return ret;
    }

    public static User getActiveUser(Activity activity) throws IOException {
        User user = null;
        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);
        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);
        File activeUserFile  = new File(activeUserFilePath);
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(activeUserFile));
            String s = dis.readUTF();
            user = JsonParser.fromJson(User.class, s);
        } catch(FileNotFoundException e) {
            Log.e(TAG, "getActiveUser: No active user was found at " + e.getMessage());
            user = null;
        } finally {
            if(dis != null) dis.close();
        }
        return user;
    }

    public static boolean deactivateUser(Activity activity) {
        boolean ret = false;
        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);
        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);
        File activeUserFile  = new File(activeUserFilePath);
        try {
            if(activeUserFile.exists()) {
                ret = activeUserFile.delete();
            }
        } catch(Exception e) {
            Log.e(TAG, "activateUser: ", e);
            ret = false;
        }
        return ret;
    }

    public static void storeUserPhoto(Bitmap photo) {
        //TODO

    }

    public static Bitmap getUserPhoto(Activity activity) {
        //TODO
        return null;
    }

    public static void addChatContact(String contactJID, String threadID){
        openChats.put(contactJID, threadID);
    }

    public static HashMap<String, String> getInstanceCachedChats(){
        return openChats;
    }

}
