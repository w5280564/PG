package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CustomerAlarm_Bean {

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @SerializedName("data")
    public List<String> data;
    @SerializedName("state")
    private Integer state;
    @SerializedName("msg")
    private String msg;
}
