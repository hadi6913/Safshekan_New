package com.khodmohaseb.parkban.services.dto.khodmohaseb.forgotrecord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord.DeviceIORecord;

import java.util.List;

public class ForgotEntranceRequest {






        @SerializedName("imei")
        @Expose
        private String imei;


    @SerializedName("plate")
    @Expose
    private String plate;


    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}


