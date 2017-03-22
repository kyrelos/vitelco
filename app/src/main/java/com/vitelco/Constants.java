package com.vitelco;

import okhttp3.MediaType;

/**
 * Created by paulmuriithi on 21/03/2017.
 */

public class Constants {

    public static final String NORMAL_NOTIFICATION_TYPE = "normal";
    public static final String PUSH_NOTIFICATION_TYPE = "push";
    public static final String MESSAGE_KEY = "message_type";
    public static final String MESSAGE_TYPE_KEY = "message";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String NOTIFICATION_ID_KEY = "notification_id";
    public static final String TOKEN_KEY = "token";
    public static final String MSISDN_KEY = "msisdn";
    public static final String WALLET_ID_KEY = "wallet_id";
    public static final String REQUEST_TYPE_KEY = "request_type";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //public static final String API_URL = "https://ec2-54-154-229-112.eu-west-1.compute.amazonaws.com/api/v1";
    public static final String API_URL = "https://vitelco-demo.jumo.world/api/v1";
    public static final String SHARED_PREFS_NAME = "vitelco";
    public static final String TOKEN_REFRESH_REQUEST = "token_refresh";
    public static final String PUSH_RESPONSE_REQUEST = "push_response";
    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";
    public static final String LOGGED_IN_KEY = "loggedIn";
    public static final String TEAM_NAME_KEY = "team_name";
    public static final String DEFAULT_COUNTRY_CODE = "KE";

}
