package br.chatup.tcc.utils;

/**
 * Created by jnde on 10/03/2016.
 */
public class Constants {

    public static final String XMPP_SERVER_IP = "10.161.20.136";
    public static final int XMPP_SERVER_PORT = 5222;
    public static final int XMPP_SERVER_PORT_REGISTER = 9090;
    public static final String XMPP_SERVER_REGISTER_PATH = "/plugins/restapi/v1/users";
    public static final String CHATUP_PREFIX_TAG = "CHATUP-";
    public static final String FULL_SERVER_ADDR = "http://" + Constants.XMPP_SERVER_IP
            .concat(":")
            .concat(String.valueOf(Constants.XMPP_SERVER_PORT_REGISTER))
            .concat(Constants.XMPP_SERVER_REGISTER_PATH);

    public static final String SERVER_SECRET_KEY = "9pbD8kcBKNzXYNPT";

    public static final String FIND_USER_PATH = "http://" + XMPP_SERVER_IP + ":" + XMPP_SERVER_PORT_REGISTER + XMPP_SERVER_REGISTER_PATH + "/%sr";

    // Messages

}
