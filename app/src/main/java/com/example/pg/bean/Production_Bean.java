package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Production_Bean {

    @SerializedName("data")
    private DataDTO data;
    @SerializedName("state")
    private Integer state;
    @SerializedName("msg")
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @SerializedName("productNo")
        private String productNo;
        @SerializedName("productName")
        private String productName;
        @SerializedName("PlantCode")
        private String plantCode;
        @SerializedName("PlantName")
        private String plantName;
        @SerializedName("lineCode")
        private String lineCode;
        @SerializedName("aggregationLevel")
        private String aggregationLevel;
        @SerializedName("gtin")
        private String gtin;
        @SerializedName("batchNo")
        private String batchNo;
        @SerializedName("productDate")
        private String productDate;
        @SerializedName("acFeature")
        private String acFeature;
        @SerializedName("parentSn")
        private String parentSn;
    }
}
