package com.github.jeterlee.alipayhome;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("YwanhzyLog")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                //.hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);                // default 0
        //.logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
    }
}
