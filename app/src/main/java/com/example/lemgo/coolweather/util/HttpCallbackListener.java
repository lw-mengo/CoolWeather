package com.example.lemgo.coolweather.util;

/**
 * Created by Administrator on 2016/12/3 0003.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
