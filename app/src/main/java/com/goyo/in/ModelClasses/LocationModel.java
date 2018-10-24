package com.goyo.in.ModelClasses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mis on 06-Jun-18.
 */

public class LocationModel {

    @SerializedName("x")
    public double lat;


    @SerializedName("y")
    public double lon;

}
