package com.tangzy.servicelib;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

public class ArouterConst {
    private static final String AROUTER_GROUP_LITE = "/jumpApp/";/** 设置 */
    public static final String TRTC_SETTING_ACTIVITY = AROUTER_GROUP_LITE + "democlient://mine/trtc";
    public static final String TRTC_DEMO_ACTIVITY = AROUTER_GROUP_LITE + "democlient://mine/demo";


    private static final String AROUTER_GROUP_APP = "/mainApp/";/** 设置 */
    public static final String APP_MAIN_ACTIVITY = AROUTER_GROUP_APP + "democlient://mine/main";
    public static final String APP_LOGIN_ACTIVITY = AROUTER_GROUP_APP + "democlient://mine/login";
    public static final String APP_DEMO_ACTIVITY = AROUTER_GROUP_APP + "democlient://mine/demo";

    public static void goRoomActivity(Context context) {
        ARouter.getInstance().build(ArouterConst.TRTC_SETTING_ACTIVITY)
                .navigation(context);
    }

    public static void goDemoActivity(Context context) {
        ARouter.getInstance().build(ArouterConst.TRTC_DEMO_ACTIVITY)
                .navigation(context);
    }

    public static void goMainActivity(Context context) {
        ARouter.getInstance().build(ArouterConst.APP_MAIN_ACTIVITY)
                .navigation(context);
    }
    public static void goLoginActivity(Context context) {
        ARouter.getInstance().build(ArouterConst.APP_LOGIN_ACTIVITY)
                .navigation(context);
    }
}
