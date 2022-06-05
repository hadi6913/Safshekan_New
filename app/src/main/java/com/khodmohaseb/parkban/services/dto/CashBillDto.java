package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class CashBillDto extends ResultDto {


    @SerializedName("Values")
    private RecipeCash value;

    public RecipeCash getValue() {
        return value;
    }

    public void setValue(RecipeCash value) {
        this.value = value;
    }

    public static class RecipeCash {

        @SerializedName("ReceiptCode")
        private String receiptCode;

        public String getUserName() {
            return receiptCode;
        }

        public void setUserName(String receiptCode) {
            this.receiptCode = receiptCode;
        }

    }
}
