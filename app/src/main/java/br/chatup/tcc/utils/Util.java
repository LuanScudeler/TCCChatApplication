package br.chatup.tcc.utils;

import android.app.Activity;
import android.view.View;

import java.nio.charset.Charset;

/**
 * Created by jadson on 3/27/16.
 */
public class Util {

    public static String getTagForClass(Class clazz) {
        return Constants.LOG_TAG + clazz.getSimpleName();
    }

    public static String getStringResource(View v, int id) {
        return v.getResources().getString(id);
    }

    public static String getStringResource(Activity activity, int id) {
        return activity.getResources().getString(id);
    }

    public static String parseByteArrayToStr(byte[] bytes) {
        String str = new String(bytes, Charset.defaultCharset());
        return str;
    }

    public static String toCapital(String str) {
        String cap = "";

        for (int cont = 0; cont < str.length(); cont++) {
            if (cont == 0)
                cap += Character.toUpperCase(str.charAt(cont));
            else
                cap += str.charAt(cont);
        }
        return cap;
    }

    public static String parseContactName(String contactJID) {
        String[] split = contactJID.split("@");
        String pJID = split[0];
        pJID = pJID.toUpperCase();

        return pJID;
    }

    public static boolean anyNull(Object[] fields) {
        for (Object o : fields)
            if (o == null) return true;
        return false;
    }


}
