package com.example.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.library.NetworkManager;
import com.example.library.type.NetType;

public class NetwrokUtils {

    /**
     * 网络是否可用
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) NetworkManager.getDefault().getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr == null) {
            return false;
        }
        NetworkInfo[] info = connMgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取当前网络类型
     *
     * @return -1 没有网络 ；1 WIFI网络 ； 2 wap网络 ； 3 net网络；
     */
    @SuppressLint("MissingPermission")
    public static NetType GetNetType() {
        ConnectivityManager connMgr = (ConnectivityManager) NetworkManager.getDefault().getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return NetType.NONE;
        }
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.NONE;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                return NetType.CMNET;
            } else {
                return NetType.CMWAP;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;
        }
        return NetType.NONE;
    }

    /**
     * 打开网络设置界面
     *
     * @param context
     * @param requestCode
     */
    public static void openSetting(Context context, int requestCode) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
