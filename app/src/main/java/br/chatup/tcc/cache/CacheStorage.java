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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.utils.JsonParser;

/**
 * Created by jnde on 08/03/2016.
 */
public class CacheStorage {

    private static final String CACHE_FILE_USER_INFO_SUFIX = "_cache.json";
    private static final String CACHE_FILE_USER_PHOTO_SUFIX = "_photo.json";
    private static final String CURRENT_ACTIVE_USER_FILE = "active.json";
    private static final String CACHE_DIR = "cache";

    private static HashMap<String, String> openChats = new HashMap<String, String>();;

    private static final String TAG = CacheStorage.class.getSimpleName();

    public static void removeAllCache(Activity activity, String userName) {

        romoveUserCacheInfo(activity, userName);

        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);

        File activeUserFile  = new File(activeUserFilePath);

        if(activeUserFile.exists()){
            activeUserFile.delete();
        }

    }

    public static void desactiveUsers(Activity activity) {
        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);

        File activeUserFile  = new File(activeUserFilePath);

        if(activeUserFile.exists()){
            activeUserFile.delete();
        }
    }

    public static void romoveUserCacheInfo(Activity activity, String userName) {

        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String cacheFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(userName.trim().toUpperCase())
                .concat(CACHE_FILE_USER_INFO_SUFIX);

        File cacheFile = new File(cacheFilePath);

        if(cacheFile.exists()) {
            cacheFile.delete();
        }
    }


    public static void storeUserInfo(User user, Activity activity) throws IOException, FileNotFoundException {

        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String cacheFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(user.getUsername().trim().toUpperCase())
                .concat(CACHE_FILE_USER_INFO_SUFIX);

        File cacheFile = new File(cacheFilePath);

        String json = JsonParser.toJson(user);

        DataOutputStream dos = null;

        try {
            dos = new DataOutputStream(new FileOutputStream(cacheFile));
            dos.writeUTF(json);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Storing User info on cache file: ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Storing User info on cache file: ", e);
            throw e;
        } finally {
            if(dos != null)
                dos.close();
        }

        activateUser(activity, user);

    }

    public static User readUserInfo(Activity activity, String username) throws IOException, FileNotFoundException {

        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String cacheFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(username.trim().toUpperCase())
                .concat(CACHE_FILE_USER_INFO_SUFIX);

        File cacheFile = new File(cacheFilePath);

        String userJson;

        User user = null;

        DataInputStream dis = null;

        if(cacheFile.exists()) {
            try {

                dis = new DataInputStream(new FileInputStream(cacheFile));

                userJson = dis.readUTF();

                user = JsonParser.fromJson(User.class, userJson);

            } catch (FileNotFoundException e) {
                Log.e(TAG, "Reading User info from cache file: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Reading User info from cache file: ", e);
                throw e;
            } finally {
                if(dis != null) {
                    dis.close();
                }
            }
        }

        return user;
    }

    public static String getActiveUser(Activity activity) throws IOException {
        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);
        DataInputStream dis;

        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);

        File activeUserFile  = new File(activeUserFilePath);

        if(activeUserFile.exists()) {

            try {
                dis = new DataInputStream(new FileInputStream(activeUserFile));
                return dis.readUTF();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "getActivrUser: ", e);
                throw e;
            } catch (IOException e) {
                Log.e(TAG, "getActivrUser: ", e);
                throw e;
            }

        }

        return null;
    }

    public static void activateUser(Activity activity, User user) throws IOException {

        File cacheDirPath = activity.getDir(CACHE_DIR, Context.MODE_PRIVATE);

        String activeUserFilePath = cacheDirPath.getAbsolutePath()
                .concat("/")
                .concat(CURRENT_ACTIVE_USER_FILE);

        File activeUserFile  = new File(activeUserFilePath);

        DataOutputStream dos = null;

        try {
            dos = new DataOutputStream(new FileOutputStream(activeUserFile));
            dos.writeUTF(user.getUsername());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Storing User info on cache file: ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Storing User info on cache file: ", e);
            throw e;
        } finally {
            if(dos != null)
                dos.close();
        }

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
