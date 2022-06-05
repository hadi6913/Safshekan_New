package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class LongResultDto extends ResultDto {

    @SerializedName("Values")
    private long values;

    public long getValues() {
        return values;
    }

    public void setValues(long values) {
        this.values = values;
    }
}
