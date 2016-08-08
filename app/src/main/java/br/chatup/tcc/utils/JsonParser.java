package br.chatup.tcc.utils;

import com.google.gson.Gson;

/**
 * Created by jnde on 09/03/2016.
 */
public class JsonParser {

    private static final Gson gson = new Gson();

    public static <T> T fromJson(Class<T> clazz, String json) {
        return gson.fromJson(json, clazz);
    }

    public static <T> String toJson(Object obj) {
        return gson.toJson(obj);
    }

}
