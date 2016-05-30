package br.chatup.tcc.utils;

import java.nio.charset.Charset;

/**
 * Created by jadson on 3/27/16.
 */
public class Util {

    public static String getTagForClass(Class clazz) {
        return Constants.LOG_TAG + clazz.getClass().getSimpleName();
    }

    public static String parseByteArrayToStr(byte[] bytes) {
        String str = new String(bytes, Charset.defaultCharset());
        return str;
    }

    public static String toCapital(String str) {
        String cap = "";

        for(int cont = 0; cont < str.length(); cont++) {
            if(cont == 0)
                cap += Character.toUpperCase(str.charAt(cont));
            else
                cap += str.charAt(cont);
        }

        return cap;
    }

}
