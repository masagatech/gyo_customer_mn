package com.goyo.in.ModelClasses;

/**
 * Created by mTech on 12-May-2017.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by mTech on 02-May-2017.
 */
public class chktrip {

    @SerializedName("frmtm")
    public String frmtm;

    @SerializedName("totm")
    public String totm;

    @SerializedName("isstarttrip")
    public boolean isstarttrip;


    public chktrip()
    {}
}
