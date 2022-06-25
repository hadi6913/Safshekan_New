
package com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceIORecord {

    @SerializedName("doorId")
    @Expose
    private long doorId;
    @SerializedName("operatorId")
    @Expose
    private long operatorId;
    @SerializedName("deviceId")
    @Expose
    private long deviceId;
    @SerializedName("trafficDateTime")
    @Expose
    private String trafficDateTime;
    @SerializedName("ioKind")
    @Expose
    private long ioKind;
    @SerializedName("plate")
    @Expose
    private String plate;
    @SerializedName("cost")
    @Expose
    private long cost;
    @SerializedName("paymentKind")
    @Expose
    private long paymentKind;
    @SerializedName("electronicPaymentTracingCode")
    @Expose
    private String electronicPaymentTracingCode;
    @SerializedName("sourceKindTraffic")
    @Expose
    private long sourceKindTraffic;

    public long getDoorId() {
        return doorId;
    }

    public void setDoorId(long doorId) {
        this.doorId = doorId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrafficDateTime() {
        return trafficDateTime;
    }

    public void setTrafficDateTime(String trafficDateTime) {
        this.trafficDateTime = trafficDateTime;
    }

    public long getIoKind() {
        return ioKind;
    }

    public void setIoKind(long ioKind) {
        this.ioKind = ioKind;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getPaymentKind() {
        return paymentKind;
    }

    public void setPaymentKind(long paymentKind) {
        this.paymentKind = paymentKind;
    }

    public String getElectronicPaymentTracingCode() {
        return electronicPaymentTracingCode;
    }

    public void setElectronicPaymentTracingCode(String electronicPaymentTracingCode) {
        this.electronicPaymentTracingCode = electronicPaymentTracingCode;
    }

    public long getSourceKindTraffic() {
        return sourceKindTraffic;
    }

    public void setSourceKindTraffic(long sourceKindTraffic) {
        this.sourceKindTraffic = sourceKindTraffic;
    }

}
