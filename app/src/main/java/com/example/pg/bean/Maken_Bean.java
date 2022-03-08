package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Maken_Bean {

    @SerializedName("data")
    private String data;
    @SerializedName("state")
    private String state;
    @SerializedName("msg")
    private String msg;
}
