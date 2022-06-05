
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetParkingInfoResponse {

    @SerializedName("deviceId")
    @Expose
    private Long deviceId;
    @SerializedName("parkingId")
    @Expose
    private Long parkingId;
    @SerializedName("parkingName")
    @Expose
    private String parkingName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("cardKind")
    @Expose
    private Long cardKind;
    @SerializedName("precepts")
    @Expose
    private List<Precept> precepts = null;
    @SerializedName("operators")
    @Expose
    private List<Operator> operators = null;
    @SerializedName("doors")
    @Expose
    private List<Door> doors = null;
    @SerializedName("tariffs")
    @Expose
    private List<Tariff> tariffs = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetParkingInfoResponse() {
    }

    /**
     * 
     * @param doors
     * @param address
     * @param operators
     * @param parkingId
     * @param cardKind
     * @param precepts
     * @param deviceId
     * @param parkingName
     * @param tariffs
     */
    public GetParkingInfoResponse(Long deviceId, Long parkingId, String parkingName, String address, Long cardKind, List<Precept> precepts, List<Operator> operators, List<Door> doors, List<Tariff> tariffs) {
        super();
        this.deviceId = deviceId;
        this.parkingId = parkingId;
        this.parkingName = parkingName;
        this.address = address;
        this.cardKind = cardKind;
        this.precepts = precepts;
        this.operators = operators;
        this.doors = doors;
        this.tariffs = tariffs;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getParkingId() {
        return parkingId;
    }

    public void setParkingId(Long parkingId) {
        this.parkingId = parkingId;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCardKind() {
        return cardKind;
    }

    public void setCardKind(Long cardKind) {
        this.cardKind = cardKind;
    }

    public List<Precept> getPrecepts() {
        return precepts;
    }

    public void setPrecepts(List<Precept> precepts) {
        this.precepts = precepts;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public void setDoors(List<Door> doors) {
        this.doors = doors;
    }

    public List<Tariff> getTariffs() {
        return tariffs;
    }

    public void setTariffs(List<Tariff> tariffs) {
        this.tariffs = tariffs;
    }

}
