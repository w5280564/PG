package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Login_Bean {
    @SerializedName("data")
    private String data;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private Integer code;
    @SerializedName("token")
    private String token;
    @SerializedName("email")
    private Object email;
}
