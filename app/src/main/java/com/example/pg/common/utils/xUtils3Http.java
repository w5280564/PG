package com.example.pg.common.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.pg.activity.LoginActivity;
import com.example.pg.bean.baseModel;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

public class xUtils3Http {

    //    public static final String BASE_URL = "http://pgdtt.aheadsoft.com.cn";
//    public static final String BASE_URL = "https://qa-dtt-mobile.pg.com.cn";
    public static final String BASE_URL = "https://dtt-mobile.pg.com.cn";
    public static final String SSO_PingID = "https://dtt-mobile.pg.com.cn/app/#/"; //pingID
    public static final String SSO_Login = "/bff/api/v1/sso/v3Login"; //获取 pingID后sso登录
    public static final String User_By = "/bff/user/get-by-code/"; //获取 用户信息

    public static final String SSO_URl = "https://api-b2b-qa.cn-pgcloud.com/paas-ssofed/v3/login?subscription-key=e65fca7dafb049f88591d94791ff35e7&app=dtt-mobile-portal&pfidpadapterid=ad..OAuth";
    public static String TOKEN = "token";
    public static String Data = "data";//用户短名
    public static String UserData = "user";//用户信息

    public static void get(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + url);
        if (parms != null) {
            for (String key : parms.keySet()) {
                params.addParameter(key, parms.get(key));
                Log.d("PG", key + " = " + parms.get(key));
            }
        }
        params.setAsJsonContent(true);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Log.d("PG", url + "接口返回" + result);
                    baseModel baseModel = GsonUtil.getInstance().json2Bean(result, baseModel.class);
                    String message = baseModel.getMessage();
                    String msg = baseModel.getMsg();
                    String code = baseModel.getCode();
                    String state = baseModel.getState();
                    if (TextUtils.equals(code,"0") || TextUtils.equals(state,"0") || TextUtils.equals(message,"true")|| TextUtils.equals(msg,"true")){
                        if (callback != null) {
                            callback.success(result);
                        }
                    }else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                // Failed to connect to /139.9.121.19:8088
                String errorInfo = ex.getMessage();
                String errorMes = "Failed to connect to /139.9.121.19:8088";
                if (TextUtils.equals(errorMes, ex.getMessage())) {
                    Toast.makeText(mContext, "无法连接到服务器，请检查网络连接", Toast.LENGTH_LONG).show();
                }
                if (callback != null) {
                    callback.failed();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public static void post(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(BASE_URL + url);
        if (parms != null) {
            for (String key : parms.keySet()) {
                params.addBodyParameter(key, parms.get(key).toString());
            }
        }
        MMKV mmkv = MMKV.defaultMMKV();
        if (!TextUtils.isEmpty(mmkv.decodeString(TOKEN))) {
            params.setHeader("Authorization", mmkv.decodeString(TOKEN));
        }
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Log.d("PG", url + "接口返回" + result);
//                    baseModel model = new Gson().fromJson(result, baseModel.class);
//                    if (model.getCode() == 201) {
//                        Intent intent = new Intent(mContext, MainLogin_Register.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivity(intent);
//                    } else if (model.getCode() == 200) {
//                        if (callback != null) {
//                            callback.success(result);
//                        }
//                    } else {
//                        callback.failed();
//                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                }
                String errorMes = "Failed to connect to /139.9.121.19:8088";
                if (TextUtils.equals(errorMes, ex.getMessage())) {
                    Toast.makeText(mContext, "无法连接到服务器，请检查网络连接", Toast.LENGTH_LONG).show();
                }
                if (callback != null) {
                    callback.failed();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

//    public static void postRsa(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
//        RequestParams params = new RequestParams(BASE_URL + url);
//        JSONObject obj = new JSONObject();
//        if (parms != null) {
//            try {
//                for (String key : parms.keySet()) {
//                    obj.put(key, parms.get(key));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        String rsaCode = RsaEncodeMethod.rsaEncode(obj.toString());
//        MyInfo myInfo = new MyInfo(mContext);
//        params.setHeader("Authorization", myInfo.getUserInfo().getToken());
//        params.setAsJsonContent(true);
//        params.setBodyContent(rsaCode);
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                if (result != null) {
//                    baseModel model = new Gson().fromJson(result, baseModel.class);
//                    if (model.getCode() == 201) {
//                        Intent intent = new Intent(mContext, MainLogin_Register.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivity(intent);
//                    } else if (model.getCode() == 200) {
//                        if (callback != null) {
//                            callback.success(result);
//                        }
//                    } else {
//                        callback.failed();
//                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                if (callback != null) {
//                    callback.failed();
//                }
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
//    }


//    public static void uploadFile(Context mContext, List<String> path, Map<String, Object> map, final GetDataCallback callback) {
//        RequestParams params = new RequestParams(BASE_URL + "upload");
//        params.setMultipart(true);
//        for (String key : map.keySet()) {
//            params.addBodyParameter(key, map.get(key).toString());
//        }
//        for (int i = 0; i < path.size(); i++) {
//            params.addBodyParameter("uploadFile" + i, new File(path.get(i)));
//        }
//        MyInfo myInfo = new MyInfo(mContext);
//        params.setHeader("Authorization", myInfo.getUserInfo().getToken());
//        params.setAsJsonContent(true);
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                if (result != null) {
//                    baseModel model = new Gson().fromJson(result, baseModel.class);
//                    if (model.getCode() == 201) {
//                        Intent intent = new Intent(mContext, MainLogin_Register.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivity(intent);
//                    } else if (model.getCode() == 200) {
//                        if (callback != null) {
//                            callback.success(result);
//                        }
//                    } else {
//                        Toast.makeText(mContext, model.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                if (callback != null) {
//                    callback.failed();
//                }
//                callback.failed();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
//    }


    public interface GetDataCallback {
        void success(String result);

        void failed(String... args);
    }
}
