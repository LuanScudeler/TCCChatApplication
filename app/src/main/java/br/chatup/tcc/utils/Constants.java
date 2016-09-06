package br.chatup.tcc.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jnde on 10/03/2016.
 */
public class Constants {

    public static final String XMPP_SERVER_IP = "52.67.111.6";
    public static final int XMPP_SERVER_RESTAPI_PORT = 9090;
    public static final int XMPP_SERVER_PORT = 5222;
    public static final String XMPP_SERVER_RESTAPI_PATH = "/plugins/restapi/v1";
    public static final String RESTAPI_USER_URL = "http://"
            .concat(XMPP_SERVER_IP)
            .concat(":")
            .concat(""+XMPP_SERVER_RESTAPI_PORT)
            .concat(XMPP_SERVER_RESTAPI_PATH)
            .concat("/users/%s");
    public static final String RESTAPI_USERS_URL = "http://"
                    .concat(XMPP_SERVER_IP)
                    .concat(":")
                    .concat(""+XMPP_SERVER_RESTAPI_PORT)
                    .concat(XMPP_SERVER_RESTAPI_PATH)
                    .concat("/users");
    public static final String LOG_TAG = "CHATUP-";
    public static final String SERVER_SECRET_KEY = "chatupsecretkey";
    // Translation service
    public static final String TOKEN_SERVICE_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/";
    public static final MultiValueMap<String, String> TOKEN_SERVICE_URI_PARAMS;
    static {
        TOKEN_SERVICE_URI_PARAMS = new LinkedMultiValueMap<String, String>();
        TOKEN_SERVICE_URI_PARAMS.add("client_id", "chatup_2016_0_0_1");
        TOKEN_SERVICE_URI_PARAMS.add("client_secret", "5rraQUu5LijmPNjQIoSiVcYmiRDwAGcKk5VwQ0xpL3M=");
        TOKEN_SERVICE_URI_PARAMS.add("scope", "http://api.microsofttranslator.com");
        TOKEN_SERVICE_URI_PARAMS.add("grant_type", "client_credentials");
    }
    public static final String TRANSLATION_URL =
            "http://api.microsofttranslator.com/v2/Http.svc/Translate?text=%s&from=%s&to=%s";
}
