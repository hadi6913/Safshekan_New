
package com.khodmohaseb.parkban.services.dto.khodmohaseb.sendtraffikrecord;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TrafficRecord {

    @SerializedName("deviceId")
    @Expose
    private long deviceId;
    @SerializedName("enterDateTime")
    @Expose
    private String enterDateTime;
    @SerializedName("exitDateTime")
    @Expose
    private String exitDateTime;
    @SerializedName("plate")
    @Expose
    private String plate;
    @SerializedName("enterDoorId")
    @Expose
    private long enterDoorId;
    @SerializedName("exitDoorId")
    @Expose
    private long exitDoorId;
    @SerializedName("enterOperatorId")
    @Expose
    private long enterOperatorId;
    @SerializedName("exitOperatorId")
    @Expose
    private long exitOperatorId;
    @SerializedName("cost")
    @Expose
    private long cost;
    @SerializedName("paymentKind")
    @Expose
    private long paymentKind;
    @SerializedName("electronicPaymentTracingCode")
    @Expose
    private String electronicPaymentTracingCode;
    @SerializedName("vehicleName")
    @Expose
    private String vehicleName;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getEnterDateTime() {
        return enterDateTime;
    }

    public void setEnterDateTime(String enterDateTime) {
        this.enterDateTime = enterDateTime;
    }

    public String getExitDateTime() {
        return exitDateTime;
    }

    public void setExitDateTime(String exitDateTime) {
        this.exitDateTime = exitDateTime;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public long getEnterDoorId() {
        return enterDoorId;
    }

    public void setEnterDoorId(long enterDoorId) {
        this.enterDoorId = enterDoorId;
    }

    public long getExitDoorId() {
        return exitDoorId;
    }

    public void setExitDoorId(long exitDoorId) {
        this.exitDoorId = exitDoorId;
    }

    public long getEnterOperatorId() {
        return enterOperatorId;
    }

    public void setEnterOperatorId(long enterOperatorId) {
        this.enterOperatorId = enterOperatorId;
    }

    public long getExitOperatorId() {
        return exitOperatorId;
    }

    public void setExitOperatorId(long exitOperatorId) {
        this.exitOperatorId = exitOperatorId;
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

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

}
