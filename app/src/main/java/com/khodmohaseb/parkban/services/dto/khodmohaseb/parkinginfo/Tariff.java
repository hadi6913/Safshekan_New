
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tariff {

    @SerializedName("parkingId")
    @Expose
    private Long parkingId;
    @SerializedName("freeDuration")
    @Expose
    private Long freeDuration;
    @SerializedName("isCircadian")
    @Expose
    private Boolean isCircadian;
    @SerializedName("isNeedCalculateOnlyLastStep")
    @Expose
    private Boolean isNeedCalculateOnlyLastStep;
    @SerializedName("circadianCalcBase")
    @Expose
    private Long circadianCalcBase;
    @SerializedName("circadianCalcKind")
    @Expose
    private Long circadianCalcKind;
    @SerializedName("roundingBoundaryTime")
    @Expose
    private Long roundingBoundaryTime;
    @SerializedName("roundingAmountTime")
    @Expose
    private Long roundingAmountTime;
    @SerializedName("roundingBoundaryCost")
    @Expose
    private Long roundingBoundaryCost;
    @SerializedName("roundingAmountCost")
    @Expose
    private Long roundingAmountCost;
    @SerializedName("vehicleTariff1")
    @Expose
    private VehicleTariff1 vehicleTariff1;
    @SerializedName("vehicleTariff2")
    @Expose
    private VehicleTariff2 vehicleTariff2;
    @SerializedName("vehicleTariff3")
    @Expose
    private VehicleTariff3 vehicleTariff3;
    @SerializedName("vehicleTariff4")
    @Expose
    private VehicleTariff4 vehicleTariff4;
    @SerializedName("vehicleTariff5")
    @Expose
    private VehicleTariff5 vehicleTariff5;
    @SerializedName("vehicleTariff6")
    @Expose
    private VehicleTariff6 vehicleTariff6;
    @SerializedName("vehicleTariff7")
    @Expose
    private VehicleTariff7 vehicleTariff7;
    @SerializedName("vehicleTariff8")
    @Expose
    private VehicleTariff8 vehicleTariff8;
    @SerializedName("effectiveDateTime")
    @Expose
    private String effectiveDateTime;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("persistOn")
    @Expose
    private String persistOn;
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Tariff() {
    }

    /**
     * 
     * @param roundingBoundaryCost
     * @param vehicleTariff8
     * @param isNeedCalculateOnlyLastStep
     * @param vehicleTariff2
     * @param parkingId
     * @param vehicleTariff3
     * @param description
     * @param vehicleTariff1
     * @param vehicleTariff6
     * @param vehicleTariff7
     * @param vehicleTariff4
     * @param vehicleTariff5
     * @param roundingAmountTime
     * @param effectiveDateTime
     * @param freeDuration
     * @param isCircadian
     * @param circadianCalcBase
     * @param roundingBoundaryTime
     * @param circadianCalcKind
     * @param id
     * @param persistOn
     * @param roundingAmountCost
     */
    public Tariff(Long parkingId, Long freeDuration, Boolean isCircadian, Boolean isNeedCalculateOnlyLastStep, Long circadianCalcBase, Long circadianCalcKind, Long roundingBoundaryTime, Long roundingAmountTime, Long roundingBoundaryCost, Long roundingAmountCost, VehicleTariff1 vehicleTariff1, VehicleTariff2 vehicleTariff2, VehicleTariff3 vehicleTariff3, VehicleTariff4 vehicleTariff4, VehicleTariff5 vehicleTariff5, VehicleTariff6 vehicleTariff6, VehicleTariff7 vehicleTariff7, VehicleTariff8 vehicleTariff8, String effectiveDateTime, Long id, String persistOn, String description) {
        super();
        this.parkingId = parkingId;
        this.freeDuration = freeDuration;
        this.isCircadian = isCircadian;
        this.isNeedCalculateOnlyLastStep = isNeedCalculateOnlyLastStep;
        this.circadianCalcBase = circadianCalcBase;
        this.circadianCalcKind = circadianCalcKind;
        this.roundingBoundaryTime = roundingBoundaryTime;
        this.roundingAmountTime = roundingAmountTime;
        this.roundingBoundaryCost = roundingBoundaryCost;
        this.roundingAmountCost = roundingAmountCost;
        this.vehicleTariff1 = vehicleTariff1;
        this.vehicleTariff2 = vehicleTariff2;
        this.vehicleTariff3 = vehicleTariff3;
        this.vehicleTariff4 = vehicleTariff4;
        this.vehicleTariff5 = vehicleTariff5;
        this.vehicleTariff6 = vehicleTariff6;
        this.vehicleTariff7 = vehicleTariff7;
        this.vehicleTariff8 = vehicleTariff8;
        this.effectiveDateTime = effectiveDateTime;
        this.id = id;
        this.persistOn = persistOn;
        this.description = description;
    }

    public Long getParkingId() {
        return parkingId;
    }

    public void setParkingId(Long parkingId) {
        this.parkingId = parkingId;
    }

    public Long getFreeDuration() {
        return freeDuration;
    }

    public void setFreeDuration(Long freeDuration) {
        this.freeDuration = freeDuration;
    }

    public Boolean getIsCircadian() {
        return isCircadian;
    }

    public void setIsCircadian(Boolean isCircadian) {
        this.isCircadian = isCircadian;
    }

    public Boolean getIsNeedCalculateOnlyLastStep() {
        return isNeedCalculateOnlyLastStep;
    }

    public void setIsNeedCalculateOnlyLastStep(Boolean isNeedCalculateOnlyLastStep) {
        this.isNeedCalculateOnlyLastStep = isNeedCalculateOnlyLastStep;
    }

    public Long getCircadianCalcBase() {
        return circadianCalcBase;
    }

    public void setCircadianCalcBase(Long circadianCalcBase) {
        this.circadianCalcBase = circadianCalcBase;
    }

    public Long getCircadianCalcKind() {
        return circadianCalcKind;
    }

    public void setCircadianCalcKind(Long circadianCalcKind) {
        this.circadianCalcKind = circadianCalcKind;
    }

    public Long getRoundingBoundaryTime() {
        return roundingBoundaryTime;
    }

    public void setRoundingBoundaryTime(Long roundingBoundaryTime) {
        this.roundingBoundaryTime = roundingBoundaryTime;
    }

    public Long getRoundingAmountTime() {
        return roundingAmountTime;
    }

    public void setRoundingAmountTime(Long roundingAmountTime) {
        this.roundingAmountTime = roundingAmountTime;
    }

    public Long getRoundingBoundaryCost() {
        return roundingBoundaryCost;
    }

    public void setRoundingBoundaryCost(Long roundingBoundaryCost) {
        this.roundingBoundaryCost = roundingBoundaryCost;
    }

    public Long getRoundingAmountCost() {
        return roundingAmountCost;
    }

    public void setRoundingAmountCost(Long roundingAmountCost) {
        this.roundingAmountCost = roundingAmountCost;
    }

    public VehicleTariff1 getVehicleTariff1() {
        return vehicleTariff1;
    }

    public void setVehicleTariff1(VehicleTariff1 vehicleTariff1) {
        this.vehicleTariff1 = vehicleTariff1;
    }

    public VehicleTariff2 getVehicleTariff2() {
        return vehicleTariff2;
    }

    public void setVehicleTariff2(VehicleTariff2 vehicleTariff2) {
        this.vehicleTariff2 = vehicleTariff2;
    }

    public VehicleTariff3 getVehicleTariff3() {
        return vehicleTariff3;
    }

    public void setVehicleTariff3(VehicleTariff3 vehicleTariff3) {
        this.vehicleTariff3 = vehicleTariff3;
    }

    public VehicleTariff4 getVehicleTariff4() {
        return vehicleTariff4;
    }

    public void setVehicleTariff4(VehicleTariff4 vehicleTariff4) {
        this.vehicleTariff4 = vehicleTariff4;
    }

    public VehicleTariff5 getVehicleTariff5() {
        return vehicleTariff5;
    }

    public void setVehicleTariff5(VehicleTariff5 vehicleTariff5) {
        this.vehicleTariff5 = vehicleTariff5;
    }

    public VehicleTariff6 getVehicleTariff6() {
        return vehicleTariff6;
    }

    public void setVehicleTariff6(VehicleTariff6 vehicleTariff6) {
        this.vehicleTariff6 = vehicleTariff6;
    }

    public VehicleTariff7 getVehicleTariff7() {
        return vehicleTariff7;
    }

    public void setVehicleTariff7(VehicleTariff7 vehicleTariff7) {
        this.vehicleTariff7 = vehicleTariff7;
    }

    public VehicleTariff8 getVehicleTariff8() {
        return vehicleTariff8;
    }

    public void setVehicleTariff8(VehicleTariff8 vehicleTariff8) {
        this.vehicleTariff8 = vehicleTariff8;
    }

    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersistOn() {
        return persistOn;
    }

    public void setPersistOn(String persistOn) {
        this.persistOn = persistOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
