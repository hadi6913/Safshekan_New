package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class IntResultDto extends ResultDto {

    @SerializedName("Values")
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
