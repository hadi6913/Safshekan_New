
package com.khodmohaseb.parkban.services.dto.khodmohaseb.sendtraffikrecord;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SendTraffikRecordRequest {

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("trafficRecords")
    @Expose
    private List<TrafficRecord> trafficRecords = null;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<TrafficRecord> getTrafficRecords() {
        return trafficRecords;
    }

    public void setTrafficRecords(List<TrafficRecord> trafficRecords) {
        this.trafficRecords = trafficRecords;
    }

}
