package com.tangzy.servicelib;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

public class ArouterConst {
    private static final String AROUTER_GROUP_APP = "/jumpApp/";/** 设置 */
    public static final String MYSELF_SETTING_ACTIVITY = AROUTER_GROUP_APP + "democlient://mine/trtc";

    public static void goRoomActivity(Context context) {
        ARouter.getInstance().build(ArouterConst.MYSELF_SETTING_ACTIVITY)
                .navigation(context);
    }
}
