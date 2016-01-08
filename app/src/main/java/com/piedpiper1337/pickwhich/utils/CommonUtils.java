package com.piedpiper1337.pickwhich.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Some utilities to perform commonly needed actions
 * Created by cary on 1/7/16.
 */
public class CommonUtils {
    private static final String TAG = CommonUtils.class.getCanonicalName();

    /**
     * Get the phone number of this phone, return null if one doesn't exist
     * */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    public static String readLocalResourceAsString(Context context,
                                                   int resourceId) {
        Log.d(TAG, "Reading resource with id:" + resourceId);

        StringBuffer returnData = new StringBuffer();
        BufferedReader file = null;

        try {
            file = new BufferedReader(new InputStreamReader(context
                    .getResources().openRawResource(resourceId), "UTF-8"));
            String line;
            while ((line = file.readLine()) != null) {
                returnData.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG,
                    "readLocalResourceAsString: ERROR: " + e.getMessage());

        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        Log.d(TAG, "readLocalResourceAsString: END");
        return returnData.toString();
    }

    /**
     * Check whether we are connected to the Internet
     * */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, " isOnline : True");
            return true;
        } else {
            Log.d(TAG, " isOnline : False");
            return false;
        }
    }

    /**
     * Get the unique device id for this device
     * */
    public static String deviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId != null ? deviceId : "";
    }

    /**
     * Build a string of stars (for hiding passwords)
     * <p/>
     * No need for StringBuilder as the compiler does it for us
     *
     * @param length the number of stars
     * @return A string of stars..
     */
    public static String stars(int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += '*';
        }
        return ret;
    }

    /**
     * Build a string of stars (for hiding passwords)
     *
     * @param password the password to create stars from
     * @return A string of stars..
     */
    public static String stars(String password) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < password.length(); i++) {
            stringBuilder.append('*');
        }
        return stringBuilder.toString();
    }

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    /**
     * Return whether a package is installed on the system
     *
     * @param uri     The name of the package e.g. com.comcast.app
     * @param context The context
     * @return Whether the app is installed or not (boolean)
     */
    public static boolean appIsInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * Save a String to the Clipboard
     *
     * @param context The context
     * @param text    The String to save
     * @return Whether the save was successful
     */
    public static boolean saveToClipboard(Context context, String text) {

        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText(TAG, text);
            clipboard.setPrimaryClip(clip);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Convert Density Independent Pixels (dp) to pixels (px)
     * */
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * Convert Scale Independent Pixel (sp) to pixels (px)
     * */
    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}

