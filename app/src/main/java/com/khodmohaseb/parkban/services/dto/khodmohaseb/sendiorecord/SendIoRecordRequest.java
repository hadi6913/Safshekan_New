
package com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SendIoRecordRequest {

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("deviceIORecords")
    @Expose
    private List<DeviceIORecord> deviceIORecords = null;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<DeviceIORecord> getDeviceIORecords() {
        return deviceIORecords;
    }

    public void setDeviceIORecords(List<DeviceIORecord> deviceIORecords) {
        this.deviceIORecords = deviceIORecords;
    }

}
