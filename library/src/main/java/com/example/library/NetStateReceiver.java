package com.example.library;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.library.annotation.Network;
import com.example.library.listener.NetChangeObserer;
import com.example.library.type.NetType;
import com.example.library.utils.Constants;
import com.example.library.utils.NetwrokUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetStateReceiver extends BroadcastReceiver {

    private NetType netType; //网络类型

//    private NetChangeObserer listener; //网络监听

    private Map<Object, List<MethodManager>> networkList;

    public NetStateReceiver() {
        netType = NetType.NONE;
        networkList = new HashMap<>();
    }

//    public void setListener(NetChangeObserer listener) {
//        this.listener = listener;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }
        //处理广播事件
        if (intent.getAction().equalsIgnoreCase(Constants.ANDROID_NET_CHANG_ACTION)) {
//            Log.e(Constants.LOG_TAG, "网络发生了改变 ");
            netType = NetwrokUtils.GetNetType(); //获取当前网络类型
            if (NetwrokUtils.isNetworkAvailable()) {  //判断有无网络
//                Log.e(Constants.LOG_TAG, "网络连接成功");
//                if (listener != null) {
//                    listener.onConnect(netType);
//                }
            } else {
//                Log.e(Constants.LOG_TAG, "没有网络连接");
//                if (listener != null) {
//                    listener.onDisConnect();
//                }
            }
            post(netType);
        }
    }

    //消息分发到所有的activity
    private void post(NetType netType) {

        //获取所有的Acitvity
        Set<Object> set = networkList.keySet();
        //比如循环MainActivity
        for (Object object: set) {
            List<MethodManager> methodList = networkList.get(object);

            if (methodList != null) {
                //循环每个方法，发送网络变更消息
                for (MethodManager method : methodList) {
                    //method的方式类型是否匹配网络枚举的类型
                    if (method.getType().isAssignableFrom(netType.getClass())) {
                        switch (method.getNetType()) {
                            case AUTO:
                                invoke(method, object, netType);
                                break;

                            case CMWAP:
                                if (netType ==NetType.CMWAP ||netType == NetType.NONE) {
                                    invoke(method, object, netType);
                                }
                                break;

                            case CMNET:
                                if (netType ==NetType.CMNET ||netType == NetType.NONE) {
                                    invoke(method, object, netType);
                                }
                                break;

                            case WIFI:
                                if (netType ==NetType.WIFI ||netType == NetType.NONE) {
                                    invoke(method, object, netType);
                                }
                                break;
                        }
                    }
                }
            }
        }

    }

    private void invoke(MethodManager method, Object object, NetType netType) {
        Method execute = method.getMethod();
        try {
            execute.invoke(object, netType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterAllObserver() {
        if (!networkList.isEmpty()) {
            networkList.clear();
        }
        //注销广播
        NetworkManager.getDefault().getApplication().unregisterReceiver(this);

        networkList = null;
    }

    public void unRegisterObserver(Object register) {
        if (!networkList.isEmpty()) {
            networkList.remove(register);
        }
    }

    public void registerObserver(Object register) {

        //将MainActivity中所有网络注解的监听方法加入集合
        List<MethodManager> methodList = networkList.get(register);

        //为空说明未添加
        if (methodList == null) {
            //开始添加
            methodList = findAnnotationMethod(register);
            networkList.put(register, methodList);
        }
    }

    private List<MethodManager> findAnnotationMethod(Object register) {
        List<MethodManager> methodList = new ArrayList<>();

        //获取类
        Class<?> clazz = register.getClass();
        //获取当前所有方法
        Method[] methods = clazz.getDeclaredMethods();

        //遍历
        for (Method method : methods) {

            //获取方法的注解；
            Network network = method.getAnnotation(Network.class);
            if (network == null) {
                continue;
            }

            //注解方法的校验
            Type returnType = method.getGenericReturnType();
            if (!"void".equalsIgnoreCase(returnType.toString())) {
                throw new RuntimeException(method.getName() + "方法返回不是void");
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "方法有且只有一个参数");
            }

            //过滤了不要要的方法，找到了开始添加到集合
            MethodManager methodManager = new MethodManager(parameterTypes[0], network.netType(), method);
            methodList.add(methodManager);
        }
        return methodList;
    }
}
