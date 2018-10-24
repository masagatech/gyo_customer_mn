package com.goyo.in;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;
import com.goyo.in.logger.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class RateThisRide extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title, tv_pickup_from, tv_drop_location, tv_ride_time, tv_amount;
    private Button bt_rate_now;
    private RatingBar rating_bar;
    private EditText et_comment;
    private float mAmount = 0;
    private JSONObject objData;
    private AlertDialog.Builder builder;
    ImageView img_rate_ride_vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rate_this_ride);
        initUI();
        builder = new AlertDialog.Builder(RateThisRide.this, R.style.MyAlertDialogStyle);
        if (Constant.isOnline(getApplicationContext())) {
            completeRideAPI();
        }


        /*hector*/
        android.util.Log.e("Session Imange", "onCreate: Final" + Preferences.getValue_String(getApplicationContext(), Preferences.VEHICLES_IMG));
        Glide.with(getApplicationContext()).load(Preferences.getValue_String(getApplicationContext(), Preferences.VEHICLES_IMG)).into(img_rate_ride_vehicle);
    }

    private void completeRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_RIDE_PAYMENT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getApplicationContext(), Preferences.RIDE_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(RateThisRide.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        //Preferences.setValue(getApplicationContext());
                        JSONObject data = response.getJSONObject("data");
                        JSONObject ride = data.getJSONObject("ride");
                        JSONObject l_data = ride.getJSONObject("l_data");
                        JSONArray pay_data = data.getJSONArray("payment_data");
                        for (int i = 0; i < pay_data.length(); i++) {
                            objData = pay_data.getJSONObject(i);
                            float totalAmount = Float.parseFloat(objData.getString("f_amount"));
                            mAmount = mAmount + totalAmount;
                        }
                        tv_amount.setText("\u20B9" + " " + mAmount);
                        tv_pickup_from.setText(l_data.getString("pickup_address"));
                        Log.d("Desination Add", "dest add : " + l_data.getString("destination_address"));
                        tv_drop_location.setText(l_data.getString("destination_address"));
                        String date = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(objData.getString("d_added")), DateUtils.FORMAT_SHOW_DATE);
                        String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(objData.getString("d_added")), DateUtils.FORMAT_SHOW_TIME);
                        tv_ride_time.setText("" + date + " " + time);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void rateRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_RIDE_RATE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getApplicationContext(), Preferences.RIDE_ID));
        urlBuilder.addQueryParameter("i_rate", String.valueOf(rating_bar.getRating()));
        urlBuilder.addQueryParameter("l_comment", et_comment.getText().toString());
        urlBuilder.addQueryParameter("v_type", "user");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(RateThisRide.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(RateThisRide.this, message, Toast.LENGTH_LONG).show();
                        Preferences.setValue(getApplicationContext(),Preferences.IS_RATED,"0");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RateThisRide.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        bt_rate_now = (Button) findViewById(R.id.bt_rate_now);
        tv_pickup_from = (TextView) findViewById(R.id.tv_pickup_from);
        tv_drop_location = (TextView) findViewById(R.id.tv_drop_location);
        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        et_comment = (EditText) findViewById(R.id.et_comment);
        tv_ride_time = (TextView) findViewById(R.id.tv_ride_time);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        bt_rate_now.setOnClickListener(this);
        actionbar_title.setText(R.string.actionbar_rate_ride);
        img_rate_ride_vehicle = (ImageView) findViewById(R.id.img_rate_ride_vehicle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rate_now:
                if (Constant.isOnline(getApplicationContext())) {
                    rateRideAPI();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
