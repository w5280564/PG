package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Transport_Bean {


    @SerializedName("data")
    private DataDTO data;
    @SerializedName("state")
    private String state;
    @SerializedName("msg")
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @SerializedName("taskNo")
        private String taskNo;
        @SerializedName("shiptoCode")
        private String shiptoCode;
        @SerializedName("shiptoName")
        private String shiptoName;
        @SerializedName("vehicleNo")
        private String vehicleNo;
        @SerializedName("deliveryCode")
        private String deliveryCode;
        @SerializedName("shipmentReceivedTime")
        private String shipmentReceivedTime;
        @SerializedName("shipmentOrderStatus")
        private String shipmentOrderStatus;
        @SerializedName("sendTime")
        private String sendTime;
        @SerializedName("sendCode")
        private String sendCode;
        @SerializedName("sendName")
        private String sendName;
    }
}
