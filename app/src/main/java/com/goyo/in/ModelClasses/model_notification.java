package com.goyo.in.ModelClasses;

import com.google.gson.annotations.SerializedName;

public class model_notification {

    @SerializedName("state")
    public Boolean state;
    @SerializedName("error")
    public String error;
    @SerializedName("data")
    public model_notification_sub data;

}
