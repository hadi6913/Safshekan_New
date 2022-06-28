
package com.khodmohaseb.parkban.services.dto.khodmohaseb.changepass;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ChangePasswordRequestDto {

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("operatorUsers")
    @Expose
    private List<OperatorUser> operatorUsers = null;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<OperatorUser> getOperatorUsers() {
        return operatorUsers;
    }

    public void setOperatorUsers(List<OperatorUser> operatorUsers) {
        this.operatorUsers = operatorUsers;
    }

}
