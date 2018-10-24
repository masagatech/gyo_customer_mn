package com.goyo.in.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.in.ModelClasses.MyKidsTrips;
import com.goyo.in.ModelClasses.chktrip;
import com.goyo.in.R;
import com.goyo.in.Utils.Global;
import com.goyo.in.school.clnt_tripview;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;


/**
 * Created by mTech on 02-May-2017.
 */
public class clnt_mykids_listAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    List<MyKidsTrips> list = new ArrayList<MyKidsTrips>();
    LayoutInflater inflater;
    Context context;
    String _drop, _pickup,selectedstd;
    private static String headerText = "";
    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public clnt_mykids_listAdapter(Context context, List<MyKidsTrips> lst, Resources rs, String _selectedstd) {
        this.list = lst;
        this.context = context;
        this.selectedstd = _selectedstd;
        this.inflater = LayoutInflater.from(context);
        _drop = rs.getString(R.string.drop);
        _pickup = rs.getString(R.string.pickup);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_clnt_mykids_list, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        final MyKidsTrips mykid = list.get(position);

        String header = mykid.pd + " - " + mykid.btch + " - " + mykid.time;

        if (mykid.Type == SECTION) {

            headerText = header;
            mViewHolder.header.setVisibility(View.VISIBLE);
//            mViewHolder.header.setBackgroundColor(parent.getResources().getColor(Color.GRAY));
            mViewHolder._item.setVisibility(View.GONE);
            mViewHolder.titleTxt.setText(mykid.btch);
            //Log.e("date",mykid.get(Tables.tbl_driver_info.createon));
            mViewHolder.Date.setText(mykid.date + " " + mykid.time);
            if (mykid.stsi.equals("1")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_play);
            } else if (mykid.stsi.equals("2")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_done);
            } else if (mykid.stsi.equals("0")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_wait);
            }
            //mViewHolder.txtMargin.setVisibility(View.VISIBLE);
            mViewHolder.btnTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTripIsStarted(mykid);
                }
            });
            mViewHolder.header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTripIsStarted(mykid);
                }
            });


        } else {
            //mViewHolder.txtMargin.setVisibility(View.GONE);
            mViewHolder.header.setVisibility(View.GONE);
            if (list.size() - 1 != position) {
                if (list.get(position + 1).Type == SECTION) {
                    mViewHolder.itembottom.setVisibility(View.VISIBLE);
                }
            }


        }
        if (mykid.pd.equalsIgnoreCase("p")) {
            mViewHolder.txtSideColor.setBackgroundColor(Color.parseColor("#f1cd8c"));
            mViewHolder.povTitle.setText(_pickup);
            mViewHolder.povTitle.setTextColor(Color.parseColor("#18b400"));
        } else {
            mViewHolder.txtSideColor.setBackgroundColor(Color.RED);
            mViewHolder.povTitle.setText(_drop);

            mViewHolder.povTitle.setTextColor(Color.RED);
        }
        if (mykid.stdsi.equals("1")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_done);
        } else if (mykid.stdsi.equals("2")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        } else if (mykid.stdsi.equals("0")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_wait);
        } else {
            //mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        }

        mViewHolder.txtkidName.setText(mykid.nm);


        return convertView;
    }


    private void checkTripIsStarted(final MyKidsTrips mykid) {


        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(context));
        json.addProperty("pdid", mykid.id);
        json.addProperty("flag", "chktrpsts");
        Ion.with(context)
                .load(Global.urls.getmykids.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            Gson g = new Gson();
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            Type listType = new TypeToken<List<chktrip>>() {
                            }.getType();
                            List<chktrip> j =  g.fromJson(result.get("data").getAsJsonArray(), listType);
                            if (j.size() > 0) {
                                chktrip k = j.get(0);
                                if (k.isstarttrip) {
                                    Intent in = new Intent(context, clnt_tripview.class);
                                    in.putExtra("tripid", mykid.tripid);
                                    in.putExtra("vhid", mykid.vhid);
                                    in.putExtra("status", mykid.stsi);
                                    in.putExtra("stdid", selectedstd);
                                    context.startActivity(in);
                                } else {
                                    Toast.makeText(context, "Trip is not started! Once started you will be notify!", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Toast.makeText(context, "Trip is not started! Once started you will be notify!", Toast.LENGTH_SHORT).show();
                                return;
                            }


                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }

                    }
                });


    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).Type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == SECTION;
    }


    private class MyViewHolder {
        private TextView titleTxt, povTitle, uploadonRes, Date, txtSideColor, txtkidName, txtKidStatus, itembottom;
        private RelativeLayout header, _item;
        private ImageView icoImages;
        private ImageButton btnTrack;

        public MyViewHolder(View item) {
            titleTxt = (TextView) item.findViewById(R.id.titleTxt);
            povTitle = (TextView) item.findViewById(R.id.povTitle);
            uploadonRes = (TextView) item.findViewById(R.id.uploadonRes);
            Date = (TextView) item.findViewById(R.id.Date);
            txtSideColor = (TextView) item.findViewById(R.id.txtSideColor);
            txtkidName = (TextView) item.findViewById(R.id.txtkidName);
            header = (RelativeLayout) item.findViewById(R.id.header);
            _item = (RelativeLayout) item.findViewById(R.id.item);
            txtKidStatus = (TextView) item.findViewById(R.id.txtKidStatus);
            btnTrack = (ImageButton) item.findViewById(R.id.btnStartTrack);
            itembottom = (TextView) item.findViewById(R.id.itembottom);



        }
    }
}
