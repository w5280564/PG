package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EarlyWarning_Detail_Bean implements Serializable {

    @SerializedName("data")
    private DataDTO data;
    @SerializedName("state")
    private Integer state;
    @SerializedName("msg")
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO implements Serializable {
        @SerializedName("detials")
        private List<DetialsDTO> detials;
        @SerializedName("shiptoCode")
        private String shiptoCode;
        @SerializedName("sendCode")
        private String sendCode;
        @SerializedName("sendName")
        private String sendName;
        @SerializedName("taskNo")
        private String taskNo;
        @SerializedName("shiptoName")
        private String shiptoName;

        @NoArgsConstructor
        @Data
        public static class DetialsDTO implements Serializable {
            @SerializedName("ticketType")
            private String ticketType;
            @SerializedName("quantity")
            private Integer quantity;
        }
    }
}
