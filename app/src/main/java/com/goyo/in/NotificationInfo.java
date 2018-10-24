package com.goyo.in;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClassNew;
import com.goyo.in.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class NotificationInfo extends AppCompatActivity {

    private TextView tv_title, tv_detail, actionbar_title;
    private String notifId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_notification_info);

        initUI();

        if (getIntent().getExtras() != null) {
            notifId = getIntent().getStringExtra("notifId");
        } else {

        }
        if (Constant.isOnline(getApplicationContext())) {
            getNotifInfoAPI();
        }
    }

    private void getNotifInfoAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_NOTIF_INFO).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("notification_id", notifId);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClassNew.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject l_data = data.getJSONObject("l_data");
                        tv_title.setText(l_data.getString("title"));
                        tv_detail.setText(l_data.getString("content"));
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);

        actionbar_title.setText("Notification info");
    }
}
