
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class VehicleTariff6 {

    @SerializedName("durationStep1")
    @Expose
    private Long durationStep1;
    @SerializedName("costStep1")
    @Expose
    private Long costStep1;
    @SerializedName("durationStep2")
    @Expose
    private Long durationStep2;
    @SerializedName("costStep2")
    @Expose
    private Long costStep2;
    @SerializedName("durationStep3")
    @Expose
    private Long durationStep3;
    @SerializedName("costStep3")
    @Expose
    private Long costStep3;
    @SerializedName("durationStep4")
    @Expose
    private Long durationStep4;
    @SerializedName("costStep4")
    @Expose
    private Long costStep4;
    @SerializedName("durationStep5")
    @Expose
    private Long durationStep5;
    @SerializedName("costStep5")
    @Expose
    private Long costStep5;
    @SerializedName("durationNight1")
    @Expose
    private Long durationNight1;
    @SerializedName("costNight1")
    @Expose
    private Long costNight1;
    @SerializedName("durationNight2")
    @Expose
    private Long durationNight2;
    @SerializedName("costNight2")
    @Expose
    private Long costNight2;
    @SerializedName("entranceCost")
    @Expose
    private Long entranceCost;
    @SerializedName("isReceiveUponEntrance")
    @Expose
    private Boolean isReceiveUponEntrance;
    @SerializedName("vehicleName")
    @Expose
    private String vehicleName;

    /**
     * No args constructor for use in serialization
     * 
     */
    public VehicleTariff6() {
    }

    /**
     * 
     * @param isReceiveUponEntrance
     * @param vehicleName
     * @param durationStep1
     * @param durationStep2
     * @param durationStep3
     * @param durationStep4
     * @param durationNight2
     * @param durationStep5
     * @param durationNight1
     * @param costStep1
     * @param costNight1
     * @param costStep2
     * @param costNight2
     * @param entranceCost
     * @param costStep3
     * @param costStep4
     * @param costStep5
     */
    public VehicleTariff6(Long durationStep1, Long costStep1, Long durationStep2, Long costStep2, Long durationStep3, Long costStep3, Long durationStep4, Long costStep4, Long durationStep5, Long costStep5, Long durationNight1, Long costNight1, Long durationNight2, Long costNight2, Long entranceCost, Boolean isReceiveUponEntrance, String vehicleName) {
        super();
        this.durationStep1 = durationStep1;
        this.costStep1 = costStep1;
        this.durationStep2 = durationStep2;
        this.costStep2 = costStep2;
        this.durationStep3 = durationStep3;
        this.costStep3 = costStep3;
        this.durationStep4 = durationStep4;
        this.costStep4 = costStep4;
        this.durationStep5 = durationStep5;
        this.costStep5 = costStep5;
        this.durationNight1 = durationNight1;
        this.costNight1 = costNight1;
        this.durationNight2 = durationNight2;
        this.costNight2 = costNight2;
        this.entranceCost = entranceCost;
        this.isReceiveUponEntrance = isReceiveUponEntrance;
        this.vehicleName = vehicleName;
    }

    public Long getDurationStep1() {
        return durationStep1;
    }

    public void setDurationStep1(Long durationStep1) {
        this.durationStep1 = durationStep1;
    }

    public Long getCostStep1() {
        return costStep1;
    }

    public void setCostStep1(Long costStep1) {
        this.costStep1 = costStep1;
    }

    public Long getDurationStep2() {
        return durationStep2;
    }

    public void setDurationStep2(Long durationStep2) {
        this.durationStep2 = durationStep2;
    }

    public Long getCostStep2() {
        return costStep2;
    }

    public void setCostStep2(Long costStep2) {
        this.costStep2 = costStep2;
    }

    public Long getDurationStep3() {
        return durationStep3;
    }

    public void setDurationStep3(Long durationStep3) {
        this.durationStep3 = durationStep3;
    }

    public Long getCostStep3() {
        return costStep3;
    }

    public void setCostStep3(Long costStep3) {
        this.costStep3 = costStep3;
    }

    public Long getDurationStep4() {
        return durationStep4;
    }

    public void setDurationStep4(Long durationStep4) {
        this.durationStep4 = durationStep4;
    }

    public Long getCostStep4() {
        return costStep4;
    }

    public void setCostStep4(Long costStep4) {
        this.costStep4 = costStep4;
    }

    public Long getDurationStep5() {
        return durationStep5;
    }

    public void setDurationStep5(Long durationStep5) {
        this.durationStep5 = durationStep5;
    }

    public Long getCostStep5() {
        return costStep5;
    }

    public void setCostStep5(Long costStep5) {
        this.costStep5 = costStep5;
    }

    public Long getDurationNight1() {
        return durationNight1;
    }

    public void setDurationNight1(Long durationNight1) {
        this.durationNight1 = durationNight1;
    }

    public Long getCostNight1() {
        return costNight1;
    }

    public void setCostNight1(Long costNight1) {
        this.costNight1 = costNight1;
    }

    public Long getDurationNight2() {
        return durationNight2;
    }

    public void setDurationNight2(Long durationNight2) {
        this.durationNight2 = durationNight2;
    }

    public Long getCostNight2() {
        return costNight2;
    }

    public void setCostNight2(Long costNight2) {
        this.costNight2 = costNight2;
    }

    public Long getEntranceCost() {
        return entranceCost;
    }

    public void setEntranceCost(Long entranceCost) {
        this.entranceCost = entranceCost;
    }

    public Boolean getIsReceiveUponEntrance() {
        return isReceiveUponEntrance;
    }

    public void setIsReceiveUponEntrance(Boolean isReceiveUponEntrance) {
        this.isReceiveUponEntrance = isReceiveUponEntrance;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

}
