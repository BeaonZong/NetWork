package com.example.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.library.NetworkManager;
import com.example.library.annotation.Network;
import com.example.library.listener.NetChangeObserer;
import com.example.library.type.NetType;
import com.example.library.utils.Constants;

//public class MainActivity extends BaseActivity  implements NetChangeObserer {
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        NetworkManager.getDefault().init(getApplication());
//        NetworkManager.getDefault().setListener(this);

        NetworkManager.getDefault().registerObserver(this);

    }


    @Network(netType = NetType.WIFI)
    public void network(NetType netType) {
        switch (netType) {
            case WIFI:
                Log.e(Constants.LOG_TAG, "network >>>  WIFI" );
                break;

            case CMNET:
            case CMWAP:
                Log.e(Constants.LOG_TAG, "流量: " + netType.name());
                break;

            case NONE:
                Log.e(Constants.LOG_TAG, "onDisConnect: " + "没有网络");
                break;


        }
    }

//    @Override
//    public void onConnect(NetType netType) {
//        Log.e(Constants.LOG_TAG, "onConnect: " + netType.name());
//    }
//
//    @Override
//    public void onDisConnect() {
//        Log.e(Constants.LOG_TAG, "onDisConnect: " + "没有网络");
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getDefault().unRegisterObserver(this);
        NetworkManager.getDefault().unRegisterAllObserver();
    }
}
