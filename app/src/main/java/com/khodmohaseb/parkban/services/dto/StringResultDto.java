package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class StringResultDto extends ResultDto {

    @SerializedName("Values")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
