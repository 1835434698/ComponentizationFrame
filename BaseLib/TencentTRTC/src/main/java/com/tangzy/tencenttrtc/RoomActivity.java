package com.tangzy.tencenttrtc;


import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.liteav.TXLiteAVCode;
import com.tencent.liteav.device.TXDeviceManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author tzy
 * @date 2021/10/11 13:13
 * @discription
 */
public class RoomActivity  extends AppCompatActivity {

    public static final int BANNER = 1003;
    public static final String BUGLYSDKAPPID = "9cc83c7d3e";
    public static final int MEETSDKAPPID = 1400581541;
    public static final String SECRETKEY = "2ce5fa16ce806b05d630f2e83a2497f55208625b0b98b9c51c4e5704479a57a5";


    protected static String TAG = "BaseActivity";
    private TXCloudVideoView mTXCVVLocalPreviewView;
    private TRTCCloud mTRTCCloud;
    private TXDeviceManager mTXDeviceManager;
    private boolean mIsFrontCamera = true;


    private List<String> mRemoteUidList;
    private List<TXCloudVideoView>          mRemoteViewList;
    private int                             mUserCount = 0;



    private Button mButtonMuteVideo;
    private Button                          mButtonMuteAudio;
    private Button                          mButtonSwitchCamera;
    private Button                          mButtonAudioRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkPermission(new CheckPermListener() {
//                @Override
//                public void superPermission() {
                    initView();
                    enterRoom();
//                }
//            }, R.string.ask_again, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }else {
            initView();
            enterRoom();
        }
    }

    private void initView() {
        mTXCVVLocalPreviewView = findViewById(R.id.txcvv_main);
        mButtonMuteVideo = findViewById(R.id.btn_mute_video);
        mButtonMuteAudio = findViewById(R.id.btn_mute_audio);
        mButtonSwitchCamera = findViewById(R.id.btn_switch_camera);
        mButtonAudioRoute = findViewById(R.id.btn_audio_route);
    }

    private void enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(RoomActivity.this));
        mTXDeviceManager = mTRTCCloud.getDeviceManager();

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = Constant.MEETSDKAPPID;
        trtcParams.userId = "mUserId";
        trtcParams.roomId = Integer.parseInt("12345646");
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(trtcParams.userId);

        mTRTCCloud.startLocalPreview(mIsFrontCamera, mTXCVVLocalPreviewView);
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mFloatingView != null && mFloatingView.isShown()) {
//            mFloatingView.dismiss();
//        }
        exitRoom();
    }

    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.stopLocalPreview();
            mTRTCCloud.exitRoom();
            mTRTCCloud.setListener(null);
        }
        mTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }

    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<RoomActivity> mContext;

        public TRTCCloudImplListener(RoomActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            Log.d(TAG, "onUserVideoAvailable userId " + userId + ", mUserCount " + mUserCount + ",available " + available);
            int index = mRemoteUidList.indexOf(userId);
            if (available) {
                if (index != -1) {
                    return;
                }
                mRemoteUidList.add(userId);
                refreshRemoteVideoViews();
            } else {
                if (index == -1) {
                    return;
                }
                mTRTCCloud.stopRemoteView(userId);
                mRemoteUidList.remove(index);
                refreshRemoteVideoViews();
            }

        }

        private void refreshRemoteVideoViews() {
            for (int i = 0; i < mRemoteViewList.size(); i++) {
                if (i < mRemoteUidList.size()) {
                    String remoteUid = mRemoteUidList.get(i);
                    mRemoteViewList.get(i).setVisibility(View.VISIBLE);
                    mTRTCCloud.startRemoteView(remoteUid, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL,mRemoteViewList.get(i));
                } else {
                    mRemoteViewList.get(i).setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.d(TAG, "sdk callback onError");
            RoomActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode+ "]" , Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }
    }
}
