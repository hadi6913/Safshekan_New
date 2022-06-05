package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class BooleanResultDto extends ResultDto {

    @SerializedName("Values")
    private boolean value;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
