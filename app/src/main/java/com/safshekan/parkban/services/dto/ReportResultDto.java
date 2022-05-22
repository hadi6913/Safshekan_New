package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportResultDto extends ResultDto {

    @SerializedName("Values")
    private ReportResult value;

    public ReportResult getValue() {
        return value;
    }

    public void setValue(ReportResult value) {
        this.value = value;
    }

    public static class ReportResult{


        @SerializedName("TotalAmount")
        private long totalAmount ;
        @SerializedName("Activities")
        private List<ReportDetailDto> reports ;


        public long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(long totalAmount) {
            this.totalAmount = totalAmount;
        }

        public List<ReportDetailDto> getReports() {
            return reports;
        }

        public void setReports(List<ReportDetailDto> reports) {
            this.reports = reports;
        }
    }


}
