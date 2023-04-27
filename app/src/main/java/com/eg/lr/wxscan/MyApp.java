package com.eg.lr.wxscan;

import android.app.Application;

import com.eg.lr.wxscan.qr.WxQrUtil;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WxQrUtil.INSTANCE.install(this);
    }
}
