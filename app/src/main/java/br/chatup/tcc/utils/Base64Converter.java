package br.chatup.tcc.utils;

import android.util.Base64;

/**
 * Created by jnde on 09/03/2016.
 */
public class Base64Converter {

    public static String encode(byte[] bytes){

        String resultado = null;

        resultado = Base64.encodeToString(bytes,0);

        return resultado;
    }

    public static byte[] decode(String base64){

        long tamanho = base64.length();

        byte[] bytes = new byte[(int) tamanho];

        bytes = Base64.decode(base64, 0);

        return bytes;
    }
}
