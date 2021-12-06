package com.tangzy.gradleplugin;

import android.util.Log;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyApplication", "onCreate");
    }
}
