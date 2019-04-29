package com.example.library;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import com.example.library.core.NetworkCallbackImpl;
import com.example.library.listener.NetChangeObserer;
import com.example.library.utils.Constants;

import java.util.List;
import java.util.Map;

public class NetworkManager {

    //    volatile 轻量级同步块 保证内存不被修改
    //单例
    private static volatile NetworkManager instance;
    private Application application;
    private NetStateReceiver receiver;

    private NetworkManager() {
        receiver = new NetStateReceiver();
    }

//    public void setListener(NetChangeObserer listener) {
//        receiver.setListener(listener);
//    }

    public static  NetworkManager getDefault(){
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void init(Application application) {
        if (application == null) {
            return;
        }
        this.application = application;
        //动态广播注册
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.ANDROID_NET_CHANG_ACTION);
//        application.registerReceiver(receiver, filter);

        //第二种方法，不通过广播

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager.NetworkCallback networkCallback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager cmgr = (ConnectivityManager) NetworkManager.getDefault().getApplication()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cmgr != null) {
                cmgr.registerNetworkCallback(request, networkCallback);
            }
        } else {

        }
    }

    public Application getApplication() {
        if (application == null) {
            throw new RuntimeException(" NetworkManager need init()");
        }
        return application;
    }

    public void unRegisterAllObserver() {
        receiver.unRegisterAllObserver();
    }

    public void unRegisterObserver(Object register) {
        receiver.unRegisterObserver(register);
    }

    public void registerObserver(Object register) {
        receiver.registerObserver(register);
    }
}
