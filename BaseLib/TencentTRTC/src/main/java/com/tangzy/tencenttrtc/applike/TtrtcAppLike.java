package com.tangzy.tencenttrtc.applike;

import android.util.Log;

import com.tangzy.plugjar.applicationlike.IApplicationLike;

public class TtrtcAppLike implements IApplicationLike {
    @Override
    public void onCreate() {
        Log.d("TtrtcAppLike", "onCreate");

    }

    @Override
    public void onStop() {
        Log.d("TtrtcAppLike", "onStop");

    }
}
