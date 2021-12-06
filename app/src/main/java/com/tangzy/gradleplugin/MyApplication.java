package com.tangzy.gradleplugin;

import android.util.Log;

import com.luojilab.component.componentlib.router.ui.UIRouter;

public class MyApplication extends android.app.Application {
    UIRouter uiRouter = UIRouter.getInstance();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyApplication", "onCreate");
        uiRouter.registerUI("share");
    }
//    o
}
