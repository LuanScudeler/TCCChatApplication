package br.chatup.tcc.utils;

import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by jadson on 3/26/16.
 */
public class RestFacade {

    private static final RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
    }

    private static final String TAG = Util.getTagForClass(RestFacade.class);

    private static final String GET = "GET";
    private static final String APPLICATION_JSON = "application/json";
    private static final String POST = "POST";


    private static HttpEntity buildHttpEntityWithParams(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json"));
        headers.add("Authorization", Constants.SERVER_SECRET_KEY);
        headers.add("Accept","application/json");
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        return entity;
    }

    private static HttpEntity buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", Constants.SERVER_SECRET_KEY);
        headers.add("Accept","application/json");
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        return entity;
    }

    public static HttpStatus post(String url, String jsonData) {
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                buildHttpEntityWithParams(jsonData),
                String.class).getStatusCode();
    }

    public static ResponseEntity<String> get(String url, Map<String, String> params) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildHttpEntity(),
                String.class,
                params);
    }

    public static ResponseEntity<String> get(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildHttpEntity(),
                String.class);
    }
}
