package com.goyo.in;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.goyo.in.FCM.MyFirebaseInstanceIDService;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.CustomDialog;
import com.goyo.in.Utils.Global;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import okhttp3.HttpUrl;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "_Login";
    private static final int RC_SIGN_IN = 41212;
    private TextView actionbar_title, tv_forgot_password;
    private Button bt_login, bt_signup;
    private EditText et_password, et_email;
    private CustomDialog dialog;
    private Button btnFacebook_main;
    private Button btnGoogle;
    private LoginButton btnFb;
    private String source = "login";
    /*GPSTracker gps;
    double latitude, longitude;*/
    int REQUEST_INTERNET = 100;
    String imei;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    private static final int FACEBOOK_SIGN_IN = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        /*hector*/
        if (!isNetworkAvailable()) {
            //Show Information about why you need the permission
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Internet Permission");
            builder.setMessage("Internet Permission Needed.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));*/

                    dialog.cancel();
                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                    finish();
                }
            });
            builder.show();
        }

        initUI();

        if (ActivityCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE}, 05);

            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPermissionDialog();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPermissionDialog();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
            showPermissionDialog();
            return;
        }

        Bundle b = getIntent().getExtras();
        RegistrationSuccess(b);



    }


    private void RegistrationSuccess(Bundle b){

        if(b != null){
            if(b.getBoolean("isreg")){
                String email =  Preferences.getValue_String(getApplicationContext(), Preferences.USER_LOGIN_EMAIL);
                source = "google";
                isManual = false;
                userLoginAPI(email);
            }
        }
        Preferences.setValue(getApplicationContext(), Preferences.USER_LOGIN_EMAIL, "");
    }


    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Allow permissions!");
        builder.setMessage("Please allow the required permissions to use this application.");
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void initUI() {
        Constant.CHECK_GPS = true;
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        bt_login = (Button) findViewById(R.id.btn_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btnFacebook_main = (Button) findViewById(R.id.btnFacebook_main);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnFb = (LoginButton) findViewById(R.id.btnFb);
        actionbar_title.setText(R.string.app_name);
        actionbar_title.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_taxi), null, null, null);
//        et_email.setText("amiee@gmail.com");
//        et_password.setText("111111");
//        et_email.setText("deven.crestinfotech@gmail.com");
//        et_email.setText("annietate.cis@gmail.com");
//        et_password.setText("12345678");

        tv_forgot_password.setOnClickListener(this);
        bt_signup.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnFacebook_main.setOnClickListener(this);
        onCerateInit();
    }

    //Code For Permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 05: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Login", "onRequestPermissionsResult: Permission Allowed");
                } else {
                    finish();
                }
                return;
            }
        }
    }


    /*hector*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgot_password:
                Intent forgot_password = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(forgot_password);

                break;
            case R.id.bt_signup:
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);

                break;
            case R.id.btn_login:

                /*gps = new GPSTracker(v.getContext(), Login.this);
                if (gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("######", "lat: " + latitude + "long :" + longitude);
                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                    userLogin();
                } else {
                    gps.showSettingsAlert();
                }*/
                source = "login";
                userLogin(et_email.getText().toString().trim());
                break;
            case R.id.btnFacebook_main:
                source = "facebook";
                btnFb.performClick();

                break;
            case R.id.btnGoogle:
                source = "google";
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    boolean isManual = false;

    private void userLogin(String email) {

        Log.e("IMEI", "onClick: " + imei);
        if (et_email.getText().toString().trim().equals("")) {
            et_email.setError("Please enter email or mobie no.");
        } else {
            if (et_password.getText().toString().equals("")) {
                et_password.setError("Please enter password.");
            } else {
                if (Constant.isOnline(getApplicationContext())) {
                    isManual = true;
                    userLoginAPI(email);
                }
            }
        }
    }

    private void userLoginAPI(String Email) {

        Preferences.setValue(getApplicationContext(), Preferences.MOKE_LOGIN, "false");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imei = telephonyManager.getDeviceId();
        String devicetoken = FirebaseInstanceId.getInstance().getToken();

        String flag = "";
        if (Email.contains("%%")) {
            String[] uid = Email.split("%%");
            email = uid[0];
            if (uid.length > 0) {
                flag = uid[1];
                if (!flag.equals("mk")) {
                    Toast.makeText(Login.this, "Invalid Login", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(Login.this, "Invalid Login", Toast.LENGTH_LONG).show();
                return;
            }
            doHardLogin(email, imei, devicetoken);

            return;
        }


        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_LOGIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_username", Email);
        urlBuilder.addQueryParameter("v_password", et_password.getText().toString().trim());
        //urlBuilder.addQueryParameter("v_device_token", devicetoken == ""? "xxxx" : devicetoken);
        urlBuilder.addQueryParameter("v_device_token", devicetoken);
        urlBuilder.addQueryParameter("v_imei_number", imei);
        urlBuilder.addQueryParameter("v_source", source);
        Log.e("Firebase Device Token", "userLoginAPI: " + devicetoken);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(Login.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = response.getJSONObject("data");
                        storeLogin(jsonObject);

                    } else if (responce_status == 2) {
                        JSONObject data = response.getJSONObject("data");
                        Intent intent = new Intent(getApplicationContext(), VerifyAccountActivity.class);
                        intent.putExtra("id", data.getString("id"));
                        intent.putExtra("phone", data.getString("phone"));
                        startActivity(intent);
                        finish();

                    } else {
                        if (response.has("data") && !isManual) {
                            JSONObject data = response.getJSONObject("data");
                            if (data.has("acc")) {
                                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                                intent.putExtra("fullname", name);
                                intent.putExtra("email", email);
                                intent.putExtra("pic", image);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }


    private void doHardLogin(String email, String v_imei_number, String devicetoken) {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("v_imei_number", v_imei_number);
        json.addProperty("devicetoken", devicetoken);
        json.addProperty("v_password", et_password.getText().toString().trim());
        Ion.with(this)
                .load(Global.urls.getadminlogin.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result == null) {
                                Log.v("result", result.toString());

                                return;

                            }
                            // JSONObject jsnobject = new JSONObject(jsond);
                            JSONObject jsonObject = new JSONObject(result.toString());


                            JSONArray j = jsonObject.getJSONArray("data");
                            if (j.length() == 0) {
                                Toast.makeText(Login.this, "Invalid Login", Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject jsonObject1 = (JSONObject) j.get(0);


                            storeLogin(jsonObject1);

                        } catch (Exception ea) {
                            // ea.printStackTrace();
                            Toast.makeText(Login.this, ea.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }



    private void storeLogin(JSONObject jsonObject1) throws JSONException {

        try {
            Preferences.setValue(getApplicationContext(), Preferences.USER_ID, jsonObject1.getString("id"));
            Preferences.setValue(getApplicationContext(), Preferences.USER_AUTH_TOKEN, jsonObject1.getString("v_token"));
            Preferences.setValue(getApplicationContext(), Preferences.USER_NAME, jsonObject1.getString("v_name"));
            Preferences.setValue(getApplicationContext(), Preferences.V_ID, jsonObject1.getString("v_id"));
            Preferences.setValue(getApplicationContext(), Preferences.CITY, jsonObject1.getString("city"));
            Preferences.setValue(getApplicationContext(), Preferences.CHECK_WRONGNOTID, "true");
            Preferences.setValue(getApplicationContext(), Preferences.MOKE_LOGIN, "true");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception ex) {
            Toast.makeText(Login.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    String name = "";
    String email = "";
    String image = "";


    /*Google sign in */
    private void onCerateInit() {


        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //********************************FACEBOOK

        callbackManager = CallbackManager.Factory.create();
        btnFb.setReadPermissions(Arrays.asList("public_profile", "email"));
        // Callback registration

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        btnFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);

                        if (bFacebookData != null) {
                            name = bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name");
                            email = bFacebookData.getString("email");
                            image = bFacebookData.getString("profile_pic");
                            isManual = false;
                            userLoginAPI(email);
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == FACEBOOK_SIGN_IN) {

            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);


        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            name = account.getGivenName() + " " + account.getFamilyName();
            email = account.getEmail();
            if (account.getPhotoUrl() != null) {
                image = account.getPhotoUrl().getPath();
            }
            isManual = false;
            userLoginAPI(email);
            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
            Toast.makeText(this, "Something went wrong. try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}

