package com.goyo.in.ModelClasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

/**
 * Created by llc on 10/28/2017.
 */

public class vts_vh_model {

    @SerializedName("id")
    public int id;

    @SerializedName("vhid")
    public String vhid;

    @SerializedName("ico")
    public String ico;

    @SerializedName("btrst")
    public String btrst;

    @SerializedName("btr")
    public String btr;

    @SerializedName("rng")
    public String rng;

    @SerializedName("acc")
    public String acc;

    @SerializedName("vno")
    public String vno;

    @SerializedName("sertm")
    public String sertm;

    @SerializedName("lstspdtm")
    public String lstspdtm;

    @SerializedName("lstspd")
    public int lstspd;

    @SerializedName("speed")
    public int speed;

    @SerializedName("bearing")
    public Double bearing;

    @SerializedName("sat")
    public int sat;


    @SerializedName("alwspeed")
    public int alwspeed;

    @SerializedName("loc")
    public Double[] loc;

    @SerializedName("lon")
    public Double lon;

    @SerializedName("lat")
    public Double lat;

    @SerializedName("gsmsig")
    public String gsmsig;

    @SerializedName("actvt")
    public String actvt;

    @SerializedName("gpstm")
    public String gpstm;

    @SerializedName("isp")
    public boolean isp;

    @SerializedName("d1")
    public int d1;

    @SerializedName("oe")
    public int oe;

    @SerializedName("vhtyp")
    public String vehtyp;

    @SerializedName("devtyp")
    public String devtyp;


    public transient Double[] toloc;
    public Polyline polyline;


    public transient boolean ischeck = false;

    public transient int olsts = 0;
    public transient String timeago = "...";
    public transient Marker vhmarker;
    public transient boolean islststsAvail = false;
    public transient LatLng Lastloc;
    public transient boolean isselected;
    public transient int indexid;
    public  transient String lastEvt = "";




    //public transient MarkerData markerData =  new MarkerData();

    public vts_vh_model() {
    }
}
