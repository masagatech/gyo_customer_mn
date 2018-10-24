package com.goyo.in.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.goyo.in.ModelClasses.LoginTransfer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;

/**
 * Created by mTech on 13-May-2017.
 */

public class Global {

//public static String DOMAIN_URL = "http://192.168.1.16:8081/goyoapi";
//    public final static String REST_URL = "http://192.168.1.110:8082/goyoapi";
//    public static final String SOCKET_URL = "http://192.168.1.110:8082/";

    //server
    public final static String REST_URL = "http://school.goyo.in:8082/goyoapi";
    public final static String IMAGES_URL = "http://school.goyo.in:8082/images";
    //public static final String SOCKET_URL = "http://35.154.230.244:8082/";
    public static final String SOCKET_URL = "http://35.154.114.229:6979/";

    public static File ExternalPath = Environment.getExternalStorageDirectory();
    public final static String Image_Path = "/goyo_images";

    public enum urls {
        getmykids("getmykids", REST_URL + "/cust/getmykids"),
        getadminlogin("getadminlogin", REST_URL + "/getadminlogin"),
        getlastknownloc("getlastknownloc", REST_URL + "/tripapi/getvahicleupdates"),
        getNotify("getNotify", REST_URL + "/getNotify"),
        getlastknownloc_new("getlastknownloc", SOCKET_URL + "tripapi/getvahicleupdates"),
        getvahicleupdates("getvahicleupdates", SOCKET_URL + "goyoapi/tripapi/getvahicleupdates"),
        activatekid("activatekid", REST_URL + "/cust/activatekid");

        public String key;
        public String value;

        private urls(String toKey, String toValue) {
            key = toKey;
            value = toValue;
        }

    }

    public final static String start = "1";
    public final static String done = "2";
    public final static String pause = "pause";
    public final static String cancel = "3";
    public final static String pending = "0";

    public final static String pickedupdrop = "1";
    public final static String absent = "2";

    //get Usrid
    public static String getUserID(Context c) {
        return Preferences.getValue_String(c, Preferences.USER_ID);
    }

    public static ProgressDialog prgdialog;

    public static void showProgress(ProgressDialog prd) {
        prd.setCancelable(false);
        if (!prd.isShowing()) prd.show();
    }

    public static void hideProgress(ProgressDialog prd) {
        prd.dismiss();
    }


    public static Object cloneObject(Object obj) {
        try {
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(obj) == null || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                if (field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)) {
                    field.set(clone, field.get(obj));
                } else {
                    Object childObj = field.get(obj);
                    if (childObj == obj) {
                        field.set(clone, clone);
                    } else {
                        field.set(clone, cloneObject(field.get(obj)));
                    }
                }
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }

    public static void OpenAppWithParams(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {

            LoginTransfer t = new LoginTransfer();
            t.USER_ID = Preferences.getValue_String(context, Preferences.USER_ID);
            t.USER_AUTH_TOKEN = Preferences.getValue_String(context, Preferences.USER_AUTH_TOKEN);

            // We found the activity now start the activity
//
            t.USER_NAME = Preferences.getValue_String(context, Preferences.USER_NAME);
            t.USER_EMAIL = Preferences.getValue_String(context, Preferences.USER_EMAIL);
            t.USER_PHONE = Preferences.getValue_String(context, Preferences.USER_MOBILE);
            t.USER_IMAGE = Preferences.getValue_String(context, Preferences.USER_NAME);
            Gson gson = new Gson();
            String str = gson.toJson(t);
            intent.putExtra(Intent.EXTRA_TEXT, str);

            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");


            context.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        }
    }

    public static void startNewActivity(Context context, Intent intent, String packageName) {
        //Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        }
    }


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


    public static String randomString(int len) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


}
