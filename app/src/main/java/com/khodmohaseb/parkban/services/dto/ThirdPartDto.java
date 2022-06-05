package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ThirdPartDto extends ResultDto {

    @SerializedName("Values")
    private ThirdPart value;

    public ThirdPart getValue() {
        return value;
    }

    public void setValue(ThirdPart value) {
        this.value = value;
    }

    public static class ThirdPart {

        private long Id;
        private long Amount;
        private String RRN;
        private String TraceNo;
        private String ApprovalCode;
        private String TransActionDateTime;

        public long getId() {
            return Id;
        }

        public void setId(long id) {
            Id = id;
        }

        public long getAmount() {
            return Amount;
        }

        public void setAmount(long amount) {
            Amount = amount;
        }

        public String getRRN() {
            return RRN;
        }

        public void setRRN(String RRN) {
            this.RRN = RRN;
        }

        public String getTraceNo() {
            return TraceNo;
        }

        public void setTraceNo(String traceNo) {
            TraceNo = traceNo;
        }

        public String getApprovalCode() {
            return ApprovalCode;
        }

        public void setApprovalCode(String approvalCode) {
            ApprovalCode = approvalCode;
        }

        public String getTransActionDateTime() {
            return TransActionDateTime;
        }

        public void setTransActionDateTime(String transActionDateTime) {
            TransActionDateTime = transActionDateTime;
        }
    }
}
