package br.chatup.tcc.utils;

import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by jadson on 3/26/16.
 */
public class RestFacade {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String TAG = Util.getTagForClass(RestFacade.class);

    private static final String basicAuth = "Basic " + Constants.SERVER_SECRET_KEY;

    private static final String GET = "GET";
    private static final String APPLICATION_JSON = "application/json";
    private static final String POST = "POST";

    private static HttpEntity buildHttpEntityWithParams(String json) {

        // set the Content-Type header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json"));
        headers.add("Authorization", Constants.SERVER_SECRET_KEY);
        headers.add("Accept","application/json");
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        return entity;

    }

    private static HttpEntity buildHttpEntity() {

        // set the Content-Type header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", Constants.SERVER_SECRET_KEY);
        headers.add("Accept","application/json");
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        return entity;

    }

    public static String post(String url, String jsonData) {

        URL urlSrv = null;
        HttpURLConnection httpConn = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            urlSrv = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //TODO throw e
        }

        try {
            httpConn = (HttpURLConnection) urlSrv.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setChunkedStreamingMode(0);
            httpConn.setRequestProperty("Authorization", basicAuth);
            httpConn.setRequestProperty("Content-Type", APPLICATION_JSON);
            httpConn.setRequestProperty("Accept", APPLICATION_JSON);
            httpConn.setRequestMethod(POST);

        } catch (IOException e) {
            e.printStackTrace();
            //TODO throw e
        }

        try {
            out = new DataOutputStream(httpConn.getOutputStream());
            out.writeUTF(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in = new DataInputStream(httpConn.getInputStream());
            return in.readUTF();
        } catch (Exception e) {
            //TODO
        } finally {
            httpConn.disconnect();
        }

        return null;

    }

    public static ResponseEntity<String> get(String url, Map<String, String> params) {

        Log.i(TAG, String.format(
                "get: Requesting data from %s address. Query params: %s",
                url,
                params.toString()
        ));
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildHttpEntity(),
                String.class,
                params);
    }

    public static String get(String url) {

        URL urlSrv = null;
        HttpURLConnection httpConn = null;
        DataInputStream in = null;

        try {
            urlSrv = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //TODO throw e
        }

        try {
            httpConn = (HttpURLConnection) urlSrv.openConnection();
            httpConn.setRequestProperty("Authorization", basicAuth);
            httpConn.setRequestProperty("Accept", APPLICATION_JSON);
            httpConn.setRequestMethod(GET);

        } catch (IOException e) {
            e.printStackTrace();
            //TODO throw e
        }

        try {
            in = new DataInputStream(httpConn.getInputStream());
            return in.readUTF();
        } catch (Exception e) {
            //TODO
        } finally {
            httpConn.disconnect();
        }

        return null;

    }
}
