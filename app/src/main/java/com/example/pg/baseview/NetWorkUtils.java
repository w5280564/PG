package com.example.pg.baseview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetWorkUtils {

    public static String getIPAddress(ConnectivityManager mConnectivityManager) {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI)) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        } else { //当前无网络连接,请在设置中打开网络
            return null;
        }
        return null;
    }

    public static String setUpInfo(NetworkInfo mActiveNetInfo, ConnectivityManager mConnectivityManager) {
        String ipAddress = "";
        if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            nameTextView.setText("网络类型：WIFI");
//            ipTextView.setText("IP地址："+getIPAddress());
            ipAddress = getIPAddress(mConnectivityManager);
        } else if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//            nameTextView.setText("网络类型：3G/4G");
//            ipTextView.setText("IP地址："+getIPAddress());
            ipAddress = getIPAddress(mConnectivityManager);
        } else {
            ipAddress = "网络类型：未知";
//            nameTextView.setText("网络类型：未知");
//            ipTextView.setText("IP地址：");
        }
        return ipAddress;
    }


    public static void myDialog(Context context) {
        AlertDialog mDialog = new AlertDialog.Builder(context)
                .setTitle("注意")
                .setMessage("当前网络不可用，请检查网络！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();//创建这个对话框
        mDialog.show();//显示这个对话框
    }


}
