package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Statistical_Bean {

            @SerializedName("dcCode")
            private String dcCode;
            @SerializedName("dcName")
            private String dcName;
            @SerializedName("taskNo")
            private String taskNo;
            @SerializedName("shiptoCode")
            private String shiptoCode;
            @SerializedName("shiptoName")
            private String shiptoName;
            @SerializedName("shouldScanNumber")
            private String shouldScanNumber;
            @SerializedName("realScanNumber")
            private String realScanNumber;
            @SerializedName("scanRate")
            private String scanRate;


            //客户多出的字段
            @SerializedName("division")
         private String division;
            @SerializedName("vehicleNo")
        private String vehicleNo;
        @SerializedName("qualified")
        private Integer qualified;
        @SerializedName("market")
        private String market;
}
