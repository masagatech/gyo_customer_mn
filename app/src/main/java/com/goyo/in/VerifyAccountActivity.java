package com.goyo.in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.FileUtils;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;


public class VerifyAccountActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 3434;
    private ImageView mBack;
    private TextView mMobileNo;
    private TextView mVarifyCode;
    private Button mVeryfy;
    private Button mResendOtp;
    private ProgressBar mProgressBar;
    private String id = "";
    private String mobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verify_account);
        if (!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission();
        }
        mBack = (ImageView) findViewById(R.id.img_back);
        mMobileNo = (TextView) findViewById(R.id.txt_mobile);
        mVarifyCode = (TextView) findViewById(R.id.txt_varify_code);
        mVeryfy = (Button) findViewById(R.id.btn_veryfy);
        mResendOtp = (Button) findViewById(R.id.btn_resend_otp);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getString("id", "");
            mobile = getIntent().getExtras().getString("phone", "");

            mMobileNo.setText(mobile);
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mVeryfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(VerifyAccountActivity.this)) {
                    verify_mobileno_api(mMobileNo.getText().toString(), mVarifyCode.getText().toString());
                }
            }
        });
        mResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(VerifyAccountActivity.this)) {
                    resend_otp_api(id, mobile);
                }
            }
        });

        mVarifyCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    mVeryfy.setEnabled(false);
                } else {
                    mVeryfy.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        mVarifyCode.setText("");


        disableButtonFor30secs();

    }

    int ct = 30;
    CountDownTimer ctime;

    private void disableButtonFor30secs() {
        mResendOtp.setEnabled(false);
        ct = 30;
        ctime = new CountDownTimer((ct * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                ct -= 1;
                mResendOtp.setText("RESEND OTP ALLOW AFTER (" + ct + ")");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                ctime = null;
                mResendOtp.setText("RESEND OTP");
                mResendOtp.setEnabled(true);
            }

        }.start();
    }


    private void signOutGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        } catch (Exception ex) {

        }
    }


    private void signOutFaceBook() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ex) {

        }
    }

    private void verify_mobileno_api(String mMobileNo, String mOtp) {
        FileUtils.showProgressBar(VerifyAccountActivity.this, mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.VERIFY_ACCOUNT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("v_username", mMobileNo);
        urlBuilder.addQueryParameter("v_otp", mOtp);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, true);
                    } else {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    private void resend_otp_api(String mId, String mMobileNo) {
        FileUtils.showProgressBar(VerifyAccountActivity.this, mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.RESEND_OTP).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("type", "user");
        urlBuilder.addQueryParameter("v_phone", mMobileNo);
        urlBuilder.addQueryParameter("id", mId);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    } else {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    }
                    disableButtonFor30secs();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    private void showAlertDialog(String message, final boolean isSuccess) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyAccountActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isSuccess) {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.putExtra("isreg", true);
                            startActivity(intent);
                            dialog.cancel();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
        AlertDialog alertDialogg = builder1.create();
        alertDialogg.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter("com.goyo.in.smsveryfy");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, iff);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain anydata
            if (intent.getExtras() != null) {
                String otp = intent.getExtras().getString("otp");
                mVarifyCode.setText(otp);
            }
        }
    };


    // Activity

    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
