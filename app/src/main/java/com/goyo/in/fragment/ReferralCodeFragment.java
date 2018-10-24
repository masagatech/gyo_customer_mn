package com.goyo.in.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.goyo.in.R;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClass;
import com.goyo.in.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;


public class ReferralCodeFragment extends Fragment implements View.OnClickListener {

    private TextView actionbar_title, tv_code, tv_earn_money;
    private Button bt_invite;
    private View view;
    String referral_message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.in.in.fragment
        view = inflater.inflate(R.layout.activity_referral_code, container, false);

        initUI();

        if (Constant.isOnline(getActivity())) {
            getReferralCode();
        }

        return view;
    }


    private void initUI() {
        actionbar_title = (TextView) view.findViewById(R.id.actionbar_title);
        tv_code = (TextView) view.findViewById(R.id.tv_code);
        tv_earn_money = (TextView) view.findViewById(R.id.tv_earn_money);
        bt_invite = (Button) view.findViewById(R.id.bt_invite);
        actionbar_title.setText(R.string.actionbar_referralcode);
        bt_invite.setOnClickListener(this);
    }

    private void getReferralCode() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_REFERRAL_CODE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        if (jsonObject.getString("v_referral_code").equals("")) {
                            tv_code.setVisibility(View.GONE);
                            tv_earn_money.setText("No Referral Code Available");
                            bt_invite.setEnabled(false);
                        } else {
                            tv_code.setText(jsonObject.getString("v_referral_code"));
                            referral_message = jsonObject.getString("referral_message");
                            tv_earn_money.setText("Share your referral code and get " + "\u20B9" + " " + jsonObject.getString("earn_money") + ". So share your code and get your money.");
                        }
                    } else {
                        Log.e("Error", "onResult: Error response null");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_invite:
                /*https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en*/
                Intent intent = new Intent();
                intent.setType("text/plain");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, referral_message);
                //intent.putExtra(Intent.EXTRA_TEXT, contant);
                startActivity(Intent.createChooser(intent, "Share"));
        }
    }
    //private String contant = "Use my referral code (8160161683) to sing up for GoYo and get ₹50 on your first ride. \\r\\nDowload the App.Click here. https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en";
}