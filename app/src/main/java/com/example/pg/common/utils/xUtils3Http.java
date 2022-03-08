package com.example.pg.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.pg.activity.LoginActivity;
import com.example.pg.bean.baseModel;
import com.example.pg.common.dialog.LoadingDialog;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

public class xUtils3Http {
    private static LoadingDialog mDialog;
    private static String dialogMessage="";
    //    public static final String BASE_URL = "https://qa-dtt-mobile.pg.com.cn";
    public static final String SSO_BASE_URL = "https://dtt-mobile.pg.com.cn";
    public static String TOKEN = "token";
    public static String Data = "data";//用户短名
    public static String UserData = "user";//用户信息

    public static final String QA_Marken_URL = "https://qa-dtt-mobile.pg.com.cn/bff/api/markenVerify";//马肯QA地址
    public static final String PRD_Marken_URL = "https://dtt-mobile.pg.com.cn/bff/api/markenVerify";//马肯PRO地址
    public static final String BASE_URL = "https://api-b2b-prd.cn-pgcloud.com/dtt-application/dtt/zzsy";//地址
    public static final String SSO_PingID = "https://dtt-mobile.pg.com.cn/app/#/"; //pingID
    public static final String SSO_Login = "/bff/api/v1/sso/v3Login"; //获取 pingID后sso登录
    public static final String User_By = "/bff/user/get-by-code/"; //获取 用户信息
    public static final String Product = "/product"; //生产信息
    public static final String Transport = "/transport"; //运输信息
    public static final String Receipt = "/receipt"; //收货
    public static final String ACF = "/acf"; //acf码图
    public static final String Customer = "/customer"; //预警客户编码
    public static final String CustomerAlarm = "/customerAlarm"; //业务编码
    public static final String Tickets = "/tickets"; //告警信息
    public static final String Dcreport = "/dcreport"; //dc履行率
    public static final String Dcreport_Detail = "/dcreportDetail"; //dc履行率详情
    public static final String Customer_Scan = "/customer-scan-report"; //客户


    public static void get(Context mContext, String url, Map<String, Object> parms, final GetDataCallback callback) {
        RequestParams params = new RequestParams(url);
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
                    if (TextUtils.equals(code, "0") || TextUtils.equals(state, "1") || TextUtils.equals(message, "true") || TextUtils.equals(msg, "true")) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
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
        showDialog(mContext);
        RequestParams params = new RequestParams(url);
        if (parms != null) {
            for (String key : parms.keySet()) {
                params.addBodyParameter(key, parms.get(key));
            }
        }
        MMKV mmkv = MMKV.defaultMMKV();
        if (!TextUtils.isEmpty(mmkv.decodeString(TOKEN))) {
//            params.addHeader("Content-Type", "application/json;charset=UTF-8");
//            String s = "eyJhbGciOiJSUzUxMiIsImtpZCI6IjEifQ.eyJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwiR0RQUiJdLCJjbGllbnRfaWQiOiJCMkIgUFJEIFNTTyIsIlVpZCI6IkRaODY3MiIsIlNob3J0TmFtZSI6ImxpdS5sLjQ5IiwiTGFzdE5hbWUiOiJsaXUiLCJVc2VybmFtZSI6InVpZD1EWjg2NzIsb3U9cGVvcGxlLG91PXBnLG89d29ybGQiLCJGaXJzdE5hbWUiOiJsaW5hIiwiZXhwIjoxNjQ2NDU5MDY5fQ.ZANwjmNLkugd8S3nc_1uK0QNnlvWqDQi0yvLuXUZw3wCJHzSFlWjCHZ-OKm-EvuxpCRSsSG2-ZY7t8OG20WE_RTd5ZcMY4lklQZ0E4lyxVshF_c56Z82R6Wq0AvMuQ1BG2I-xVfyAzSuuu3aeFP__V5uXEqO7JmVmMgYbfaHCVx2yvIwQUj6nt7qcSMIely6KAuW1azrZSnop92eK-tAWgeROUUQNqVA1yjSUcsK8VSksUKavUo0mFvpre2K6KbV0aH0FytrH4o797BePH2h6RVYcD2CezuekVpsodjDve6P7HQsXmN1FpKCI3lwqyBnQrWcvHaF8OljqHzEFIr3KA";
            params.addHeader("Authorization", mmkv.decodeString(TOKEN));
            params.addHeader("Ocp-Apim-Subscription-Key", "0143fb1763a0478cba1379682a52e077");
            params.addHeader("Auth-Type", "ssofed");
        }
        params.setBodyContentType("application/json;charset=UTF-8");
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dismissDialog(mContext);
                if (result != null) {
                    Log.d("PG", url + "接口返回" + result);
                    baseModel baseModel = GsonUtil.getInstance().json2Bean(result, baseModel.class);
                    String message = baseModel.getMessage();
                    String msg = baseModel.getMsg();
                    String code = baseModel.getCode();
                    String state = baseModel.getState();
                    if (TextUtils.equals(code, "0") || TextUtils.equals(state, "1") || TextUtils.equals(message, "true") || TextUtils.equals(msg, "success")) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog(mContext);
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 401) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } else {

                    String errorMes = "Failed to connect to /139.9.121.19:8088";
                    if (TextUtils.equals(errorMes, ex.getMessage())) {
                        Toast.makeText(mContext, "无法连接到服务器，请检查网络连接", Toast.LENGTH_LONG).show();
                    }
                }
                if (callback != null) {
                    callback.failed();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
                //网络请求结束后关闭对话框
//                dismissDialog(mContext);
            }

            @Override
            public void onFinished() {
                //网络请求结束后关闭对话框
                dismissDialog(mContext);
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

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }


    /**
     * 显示等待对话框
     */
    private static void showDialog(Context context) {
        if (context == null) {
            return;
        }
        if (mDialog == null) {
            mDialog = new LoadingDialog(context,dialogMessage);
        }
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        if (!mDialog.isShowing()){
            if (context instanceof FragmentActivity) {
                if (!((FragmentActivity) context).isDestroyed()) {
                    mDialog.show();
                }

            } else if (context instanceof Activity) {
                if (!((Activity) context).isDestroyed()) {
                    mDialog.show();
                }

            }
        }
    }

    /**
     * 关闭等待对话框
     */
    private static void dismissDialog(Context context) {
        if (mDialog != null && mDialog.isShowing()) {
            if (context instanceof FragmentActivity) {
                if (!((FragmentActivity) context).isDestroyed()) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            } else if (context instanceof Activity) {
                if (!((Activity) context).isDestroyed()) {
                    mDialog.dismiss();
                    mDialog = null;
                }

            }

        }

    }



    public interface GetDataCallback {
        void success(String result);

        void failed(String... args);
    }
}
