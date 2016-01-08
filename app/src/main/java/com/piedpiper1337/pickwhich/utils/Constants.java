package com.piedpiper1337.pickwhich.utils;

import com.piedpiper1337.pickwhich.processors.LoginProcessor;

/**
 * Created by cary on 1/4/16.
 */
public class Constants {

    public interface ApiRequestId {
        int AUTH_BASE_VALUE = 1000;
        int LOGIN = AUTH_BASE_VALUE + 1;
        int SIGN_UP = AUTH_BASE_VALUE + 2;

        int MESSAGE_BASE_VALUE = 2000;

        int ACCOUNTS_BASE_VALUE = 3000;
    }

    public interface ApiKeys {
        // String CLIENT_SECRET = "abcdefg-123456-hijklmnop";
    }

    public interface SharedPreferenceKeys {

    }

    public interface IntentExtras {
        String JSON_RESPONSE = "com.PiedPiper1337.appIntentExtras.JSON_RESPONSE";
        String MESSAGE = "com.PiedPiper1337.appIntentExtras.MESSAGE";
        String REQUEST_ID = "com.PiedPiper1337.appIntentExtras.REQUEST_ID";
        String USERNAME = "com.PiedPiper1337.appIntentExtras.USERNAME";
        String PASSWORD = "com.PiedPiper1337.appIntentExtras.PASSWORD";
        String EMAIL = "com.PiedPiper1337.appIntentExtras.EMAIL";
        String PHONE_NUMBER = "com.PiedPiper1337.appIntentExtras.PHONE_NUMBER";
        String ERROR_NO_NETWORK = "com.PiedPiper1337.appIntentExtras.ERROR_NO_NETWORK";
    }

    public interface IntentActions {
        String ACTION_ERROR = "com.PiedPiper1337.appIntentActions.ACTION_ERROR";
        String ACTION_SUCCESS = "com.PiedPiper1337.appIntentActions.ACTION_ERROR";

        String AUTHENTICATION = LoginProcessor.class.getName();
    }

    public interface JsonKeys {
        // String ACCESS_TOKEN = "access_token";
    }
}
