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

import br.chatup.tcc.bean.User;
import br.chatup.tcc.utils.JsonParser;

/**
 * Created by jnde on 08/03/2016.
 */
public class CacheStorage {

    private static final String DEF_CACHE_FILE_USER_INFO = "usr_cache.json";
    private static final String DEF_CACHE_FILE_USER_PHOTO = "usr_photo.json";
    private static final String DEF_CACHE_DIR = "cache";

    private static final String TAG = CacheStorage.class.getName();

    public static void removeAllCache(Activity activity) {
        File path = activity.getDir(String.format("%s", DEF_CACHE_DIR), Context.MODE_PRIVATE);

        File cacheFile = new File(path.getAbsolutePath() + "/" + DEF_CACHE_FILE_USER_INFO);

        if(cacheFile.exists())
            cacheFile.delete();
    }


    public static void storeUserInfo(User user, Activity activity) throws IOException, FileNotFoundException {

        File path = activity.getDir(String.format("%s", DEF_CACHE_DIR), Context.MODE_APPEND);

        File cacheFile = new File(path.getAbsolutePath() + "/" + DEF_CACHE_FILE_USER_INFO);

        String json;
        json = JsonParser.toJson(user);

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

    }

    public static User readUserInfo(Activity activity) throws IOException, FileNotFoundException {

        User user = null;

        String userJson = null;

        File path = activity.getDir(String.format("%s", DEF_CACHE_DIR), Context.MODE_APPEND);

        File cacheFile = new File(path.getAbsolutePath() + "/" + DEF_CACHE_FILE_USER_INFO);

        DataInputStream dis;

        try {

            dis = new DataInputStream(new FileInputStream(cacheFile));

            userJson = dis.readUTF();

            user = JsonParser.fromJson(User.class, userJson);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Reading User info from cache file: ", e);
        } catch (IOException e) {
            Log.e(TAG, "Reading User info from cache file: ", e);
            throw e;
        }

        return user;
    }

    public static void storeUserPhoto(Bitmap photo) {
        //TODO
    }

    public static Bitmap getUserPhoto(Activity activity) {
        //TODO
        return null;
    }

}
