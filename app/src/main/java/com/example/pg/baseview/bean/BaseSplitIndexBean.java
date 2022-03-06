package com.example.pg.baseview.bean;


import com.example.pg.bean.Statistical_Bean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @文件描述：分页的基类
 */
@NoArgsConstructor
@Data
public class BaseSplitIndexBean<T>{
//    @SerializedName("data")
//    private DataDTO data;
//    @SerializedName("state")
//    private Integer state;
//    @SerializedName("msg")
//    private String msg;
//
//    @NoArgsConstructor
//    @Data
//    public static class DataDTO {
//        @SerializedName("pageNum")
//        private Integer pageNum;
//        @SerializedName("pageSize")
//        private Integer pageSize;
//        @SerializedName("content")
//        private List<Statistical_Bean> content;
//        @SerializedName("totalElements")
//        private Integer totalElements;
//        @SerializedName("totalPages")
//        private Integer totalPages;
//
//    }

    private String totalPages;
    private List<T> content;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }


    public String getTotalPages() {
        return totalPages;
    }






}
