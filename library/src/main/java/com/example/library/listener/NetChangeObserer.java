package com.example.library.listener;

import com.example.library.type.NetType;

//接口监听
public interface NetChangeObserer {

    //网络连接时
    void onConnect(NetType netType);

    //网络断开时
     void onDisConnect();

}
