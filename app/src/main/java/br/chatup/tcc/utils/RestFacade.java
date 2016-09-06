package br.chatup.tcc.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    private static HttpEntity buildJsonHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", Constants.SERVER_SECRET_KEY);
        headers.add("Accept","application/json");
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        return entity;
    }

    private static HttpEntity buildBasicHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        return entity;
    }

    public static ResponseEntity<String> post(String url, MultiValueMap<String, String> params) {
        return restTemplate.postForEntity(url, params, String.class);
    }

    public static HttpStatus post(String url, String jsonData) {
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                buildHttpEntityWithParams(jsonData),
                String.class).getStatusCode();
    }

    /*public static ResponseEntity<String> get(String url, MultiValueMap<String, String> params) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildJsonHttpEntity(),
                String.class,
                params);
    }*/

    public static ResponseEntity<String> get(String url, HttpHeaders httpHeaders) {
        HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    public static ResponseEntity<String> get(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildJsonHttpEntity(),
                String.class);
    }
}
