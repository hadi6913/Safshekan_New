package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ResultDto {

    @SerializedName("Message")
    private String message;

    @SerializedName("RealMessage")
    private String realMessage;

    @SerializedName("ResponseResultType")
    private int responseResultType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRealMessage() {
        return realMessage;
    }

    public void setRealMessage(String realMessage) {
        this.realMessage = realMessage;
    }

    public int getResponseResultType() {
        return responseResultType;
    }

    public void setResponseResultType(int responseResultType) {
        this.responseResultType = responseResultType;
    }

}