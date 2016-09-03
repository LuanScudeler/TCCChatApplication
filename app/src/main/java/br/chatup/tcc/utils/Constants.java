package br.chatup.tcc.utils;

/**
 * Created by jnde on 10/03/2016.
 */
public class Constants {

    public static final String XMPP_SERVER_IP = "52.67.111.6";
    public static final int XMPP_SERVER_RESTAPI_PORT = 9090;
    public static final String XMPP_SERVER_RESTAPI_PATH = "/plugins/restapi/v1";
    public static final String RESTAPI_USER_URL = "http://"
                    .concat(XMPP_SERVER_IP)
                    .concat(":")
                    .concat(""+XMPP_SERVER_RESTAPI_PORT)
                    .concat(XMPP_SERVER_RESTAPI_PATH)
                    .concat("/users");
    public static final int XMPP_SERVER_PORT = 5222;
    public static final String LOG_TAG = "CHATUP-";
    public static final String FULL_JID_APPEND = "/Smack";

    public static final String SERVER_SECRET_KEY = "chatupsecretkey";
}
