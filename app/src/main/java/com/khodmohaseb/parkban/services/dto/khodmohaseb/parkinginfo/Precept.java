
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Precept {

    @SerializedName("parkingId")
    @Expose
    private Long parkingId;
    @SerializedName("beginDate")
    @Expose
    private String beginDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("maximumOfflineTime")
    @Expose
    private Long maximumOfflineTime;
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
    public Precept() {
    }

    /**
     * 
     * @param beginDate
     * @param maximumOfflineTime
     * @param endDate
     * @param parkingId
     * @param description
     * @param id
     * @param persistOn
     */
    public Precept(Long parkingId, String beginDate, String endDate, Long maximumOfflineTime, Long id, String persistOn, String description) {
        super();
        this.parkingId = parkingId;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.maximumOfflineTime = maximumOfflineTime;
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

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getMaximumOfflineTime() {
        return maximumOfflineTime;
    }

    public void setMaximumOfflineTime(Long maximumOfflineTime) {
        this.maximumOfflineTime = maximumOfflineTime;
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
