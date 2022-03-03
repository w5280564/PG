package com.example.pg.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 个人资料
 */

public class MyInfo {
    private static final String SP_NAME_KEY = "userName";
    private static final String SP_PASSWORD_KEY = "password";
    SharedPreferences share = null; //本地存储类

    public MyInfo(Context context) {
        share = context.getSharedPreferences("myInfo", Context.MODE_PRIVATE);
    }

    //存储本地数据
    public void setUserInfo(String username, String password) {
        SharedPreferences.Editor editor = share.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public void getUserInfo(String username, String password) {
        SharedPreferences.Editor editor = share.edit();
        editor.putString("username", username);
        editor.putString("password", password);
//        return myInfo;
    }


    //修改存储的一条数据
    public void setOneData(String keyStr, String valueStr) {
        SharedPreferences.Editor editor = share.edit();
        editor.putString(keyStr, valueStr);
        editor.apply();
    }

    public void setOneIntData(String keyStr, int valueStr) {
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(keyStr, valueStr);
        editor.apply();
    }


    //删除本地个人数据
    public void clearInfoData(Context context) {
        share = context.getSharedPreferences("myInfo", Context.MODE_PRIVATE);
        share.edit().clear().apply();
    }


    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    public void putBean(Context context, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
//                SharedPreferences.Editor editor = getSharedPreferences(context).edit();
                SharedPreferences.Editor editor = share.edit();
                editor.putString(key, string64).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    public Object getBean(Context context, String key) {
        Object obj = null;
        try {
//            String base64 = getSharedPreferences(context).getString(key, "");
            String base64 = share.getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


}