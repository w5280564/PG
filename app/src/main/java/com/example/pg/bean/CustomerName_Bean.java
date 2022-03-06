package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CustomerName_Bean {

    @SerializedName("data")
    private List<DataDTO> data;
    @SerializedName("state")
    private Integer state;
    @SerializedName("msg")
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @SerializedName("shiptoCode")
        private String shiptoCode;
        @SerializedName("shiptoName")
        private String shiptoName;
    }
}
