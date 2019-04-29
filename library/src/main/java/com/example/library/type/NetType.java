package com.example.library.type;

//网络类型
public enum NetType {

    //只要有网络，不关心类型
    AUTO,

    //WIFI网络
    WIFI,

    //CMNET CMWAP都是移动网络，根据业务区分
    //主要是PC 笔记本电脑 手持PAD设备
    CMNET,

    //手机上网
    CMWAP,

    //没有任何网络
    NONE
}
