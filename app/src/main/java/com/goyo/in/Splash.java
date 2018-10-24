package com.goyo.in;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;
import okhttp3.HttpUrl;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private String mRideId, mType;
    private String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.goyo.in", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }



        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext(),65206);
        AppEventsLogger.activateApp(this);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {
            mRideId = getIntent().getExtras().getString("i_ride_id", "");
            mType = getIntent().getExtras().getString("type", "");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {

                    if (Preferences.getValue_String(getApplicationContext(), Preferences.CHECK_WRONGNOTID).isEmpty()) {
                        user_logout();
                    } else {
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    }


                }
                if (getIntent().getExtras() != null) {
                    if (mType.equals("user_ride_start")) {
                        Intent intent = new Intent(Splash.this, StartRideActivity.class);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    } else if (mType.equals("user_ride_complete")) {
                        Intent intent = new Intent(Splash.this, CompleteRide.class);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    }
                }
                if (Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }

    private void user_logout() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_LOGOUT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(Splash.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }


}
