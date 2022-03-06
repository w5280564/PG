package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CustomerAlarm_Bean {

    @SerializedName("data")
    private List<String> data;
    @SerializedName("state")
    private Integer state;
    @SerializedName("msg")
    private String msg;
}
