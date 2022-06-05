
package com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Operator {

    @SerializedName("parkingId")
    @Expose
    private Long parkingId;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userPass")
    @Expose
    private String userPass;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
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
    public Operator() {
    }

    /**
     * 
     * @param firstName
     * @param lastName
     * @param userPass
     * @param parkingId
     * @param description
     * @param id
     * @param persistOn
     * @param userName
     * @param isActive
     */
    public Operator(Long parkingId, String firstName, String lastName, String userName, String userPass, Boolean isActive, Long id, String persistOn, String description) {
        super();
        this.parkingId = parkingId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.userPass = userPass;
        this.isActive = isActive;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
