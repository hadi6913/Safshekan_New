
package com.khodmohaseb.parkban.services.dto.khodmohaseb.changepass;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperatorUser {

    @SerializedName("parkingId")
    @Expose
    private long parkingId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("password")
    @Expose
    private String password;

    public long getParkingId() {
        return parkingId;
    }

    public void setParkingId(long parkingId) {
        this.parkingId = parkingId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
