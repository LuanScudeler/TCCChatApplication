package br.chatup.tcc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jnde on 09/03/2016.
 */
public class JsonParser {

    private static final Gson gson = new Gson();

    public static <T> T fromJson(Class<T> clazz, String json) {
        return gson.fromJson(json, clazz);
    }

    public static <T> List<T> arrfromJson(Class<T> clazz, String json) {
        Type listType = new TypeToken<List<T>>(){}.getType();
        List<T> lista = new Gson().fromJson(json,listType);
        return lista;
    }

    public static <T> String toJson(Object obj) {
        return gson.toJson(obj);
    }

}
