
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Door {

    @SerializedName("parkingId")
    @Expose
    private Long parkingId;
    @SerializedName("doorName")
    @Expose
    private String doorName;
    @SerializedName("doorKind")
    @Expose
    private Long doorType;
    @SerializedName("id")
    @Expose
    private Long id;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Door() {
    }

    /**
     * 
     * @param doorName
     * @param parkingId
     * @param doorType
     * @param id
     */
    public Door(Long parkingId, String doorName, Long doorType, Long id) {
        super();
        this.parkingId = parkingId;
        this.doorName = doorName;
        this.doorType = doorType;
        this.id = id;

    }

    public Long getParkingId() {
        return parkingId;
    }

    public void setParkingId(Long parkingId) {
        this.parkingId = parkingId;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public Long getDoorType() {
        return doorType;
    }

    public void setDoorType(Long doorType) {
        this.doorType = doorType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}
