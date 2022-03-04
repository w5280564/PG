package com.example.pg.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User_Bean {

    @SerializedName("requestId")
    private String requestId;
    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("success")
    private Boolean success;
    @SerializedName("data")
    private DataDTO data;
    @SerializedName("timestamp")
    private String timestamp;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @SerializedName("displayName")
        private String displayName;
        @SerializedName("channelId")
        private Integer channelId;
        @SerializedName("disableBy")
        private String disableBy;
        @SerializedName("modifyBy")
        private String modifyBy;
        @SerializedName("disableReason")
        private String disableReason;
        @SerializedName("modifyTime")
        private String modifyTime;
        @SerializedName("shopId")
        private String shopId;
        @SerializedName("name")
        private String name;
        @SerializedName("userRole")
        private String userRole;
        @SerializedName("code")
        private String code;
        @SerializedName("disableDate")
        private String disableDate;
        @SerializedName("status")
        private String status;
        @SerializedName("userLanguage")
        private String userLanguage;
        @SerializedName("createChannelId")
        private String createChannelId;
        @SerializedName("createBy")
        private String createBy;
        @SerializedName("password")
        private String password;
        @SerializedName("enableDate")
        private String enableDate;
        @SerializedName("id")
        private String id;
        @SerializedName("createTime")
        private String createTime;
        @SerializedName("enableBy")
        private String enableBy;
        @SerializedName("remark")
        private String remark;
        @SerializedName("type")
        private String type;
        @SerializedName("staffCode")
        private String staffCode;
        @SerializedName("shopCode")
        private String shopCode;
        @SerializedName("tel")
        private String tel;
        @SerializedName("email")
        private String email;
        @SerializedName("isInitPwd")
        private String isInitPwd;
        @SerializedName("pwdModifyDate")
        private String pwdModifyDate;
        @SerializedName("pwdErrNum")
        private String pwdErrNum;
        @SerializedName("islockPwd")
        private String islockPwd;
        @SerializedName("openId")
        private String openId;
        @SerializedName("roleNames")
        private String roleNames;
        @SerializedName("deptName")
        private String deptName;
        @SerializedName("roleList")
        private List<RoleListDTO> roleList;
        @SerializedName("menuList")
        private List<MenuListDTO> menuList;

        @NoArgsConstructor
        @Data
        public static class RoleListDTO {
            @SerializedName("id")
            private Integer id;
            @SerializedName("code")
            private String code;
            @SerializedName("name")
            private String name;
            @SerializedName("status")
            private Integer status;
            @SerializedName("createBy")
            private String createBy;
            @SerializedName("createTime")
            private String createTime;
            @SerializedName("modifyBy")
            private String modifyBy;
            @SerializedName("modifyTime")
            private String modifyTime;
        }

        @NoArgsConstructor
        @Data
        public static class MenuListDTO {
            @SerializedName("id")
            private Integer id;
            @SerializedName("name")
            private String name;
            @SerializedName("nameAs")
            private String nameAs;
            @SerializedName("parentId")
            private String parentId;
            @SerializedName("url")
            private String url;
            @SerializedName("code")
            private String code;
            @SerializedName("bizVersionType")
            private String bizVersionType;
            @SerializedName("icon")
            private String icon;
            @SerializedName("grade")
            private String grade;
            @SerializedName("sqlOrder")
            private String sqlOrder;
            @SerializedName("enabled")
            private String enabled;
            @SerializedName("status")
            private String status;
            @SerializedName("remark")
            private String remark;
            @SerializedName("description")
            private String description;
            @SerializedName("lastDateUpdate")
            private String lastDateUpdate;
            @SerializedName("parent")
            private String parent;
            @SerializedName("businessObjectMetaId")
            private String businessObjectMetaId;
            @SerializedName("check")
            private String check;
            @SerializedName("displayIcon")
            private String displayIcon;
        }
    }
}
