package com.goyo.in;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goyo.in.AdapterClasses.MedStatusAdapter;
import com.goyo.in.ModelClasses.MyTicketsModel;
import com.goyo.in.Utils.Constant;
import com.goyo.in.Utils.FileUtils;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.VolleyLibrary.RequestInterface;
import com.goyo.in.VolleyLibrary.VolleyRequestClassNew;
import com.goyo.in.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.HttpUrl;

public class StatusMedicine extends AppCompatActivity {

    private TextView mNoStatusLbl;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private MedStatusAdapter medStatusAdapter;
    private TextView actionbar_title;

    public static ArrayList<MyTicketsModel> myTicketsModelArrayList = new ArrayList<>();
    private ArrayList<String> mSpineerItemsArray = new ArrayList<>();
    private ArrayList<String> mSpineerItemsArrayKey = new ArrayList<>();
    private HashMap<String,String> hashMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_status_medicine);

        initView();
    }

    private void initView(){

        mNoStatusLbl = (TextView) findViewById(R.id.txt_lable);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.med_status);

        mNoStatusLbl.setVisibility(View.GONE);
        actionbar_title.setText("Prescription Status");

        if(Constant.isOnline(this))
        {
            getSupportTypes();

            setMyTicketsAdapter();

            getMyTicketApi();
        }


    }

    private void setMyTicketsAdapter(){
        medStatusAdapter = new MedStatusAdapter(this,myTicketsModelArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(medStatusAdapter);
    }

    private void getSupportTypes() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_SUPPORT_TYPE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(this, Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(this,Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONArray data = response.getJSONArray("data");

                        for(int i=0;i<data.length();i++){
                            mSpineerItemsArray.add( data.getJSONObject(i).getString("j_title"));
                            hashMap.put(data.getJSONObject(i).getString("j_title"),
                                    data.getJSONObject(i).getString("id"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMyTicketApi() {
        FileUtils.showProgressBar(this,mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_MY_TICKET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(this, Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(this,Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        mNoStatusLbl.setVisibility(View.GONE);
                        FileUtils.hideProgressBar(StatusMedicine.this,mProgressBar);
                        JSONArray data = response.getJSONArray("data");

                        if(!myTicketsModelArrayList.isEmpty())
                            myTicketsModelArrayList.clear();

                        for(int i=0;i<data.length();i++){
                            MyTicketsModel myTicketsModel = new MyTicketsModel(data.getJSONObject(i).getString("v_support_id"),
                                    data.getJSONObject(i).getString("e_status"),
                                    data.getJSONObject(i).getString("d_added"),
                                    data.getJSONObject(i).getString("title"),
                                    data.getJSONObject(i).getString("text"));
                            myTicketsModelArrayList.add(myTicketsModel);
                        }
                        medStatusAdapter.notifyDataSetChanged();

                    }else {
                        FileUtils.hideProgressBar(StatusMedicine.this,mProgressBar);
                        mNoStatusLbl.setText(message);
                        mNoStatusLbl.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FileUtils.hideProgressBar(StatusMedicine.this,mProgressBar);
                }
            }
        });
    }

}
