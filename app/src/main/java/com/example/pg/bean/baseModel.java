package com.example.pg.bean;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class baseModel {
    @SerializedName("data")
    private String data;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("message")
    private String message;
    @SerializedName("msg")
    private String msg;
    @SerializedName("code")
    private String code;
    @SerializedName("state")
    private String state;

}
