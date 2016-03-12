package br.chatup.tcc.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.chatup.tcc.utils.Constants;

/**
 * HTTPConnection.java
 * Purpose: Execute HTTP requests
 *
 * @author Jadson de Oliveira Rosa - jadson.oli@hotmail.com
 * @version 1.0 10/03/2016
 */
public class HTTPConnection {

    /**
     * Execute HTTP GET
     *
     * @param addr host address
     * @return host's response
     * @throws IOException
     */
    public static byte[] get(String addr) throws IOException {

        byte[] resp = null;
        URL url = null;
        HttpURLConnection conn = null;
        InputStream in = null;

        try {

            url = new URL(addr);

            conn = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(conn.getInputStream());

            resp = new byte[in.available()];

            in.read(resp);

        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException ex) {
            throw ex;
        } finally {
            conn.disconnect();
        }

        return resp;

    }

    /**
     * Execute HTTP POST
     *
     * @param addr host address
     * @param data JSON data
     * @return host's response
     * @throws IOException
     */
    public static byte[] post(String addr, String data) throws IOException{

        byte[] resp = null;
        URL url;
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;

        try {

            url = new URL(addr);

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0); //Used for best performance

            out = new BufferedOutputStream(conn.getOutputStream());

            out.write(data.getBytes(Constants.DEF_CHARSET));

            in = new BufferedInputStream(conn.getInputStream());

            resp = new byte[in.available()];

            in.read(resp);

        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException ex) {
            throw ex;
        }

        return resp;
    }

}
