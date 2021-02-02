package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.shopprdriver.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.AgoraRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoChatActivity extends AppCompatActivity {
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private RtcEngine mRtcEngine;// Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    SessonManager sessonManager;
    int chatId, uId;
    String token, channel_name, userId;
    BroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        sessonManager = new SessonManager(this);
        chatId = getIntent().getIntExtra("chatId", 0);
        //

        //token = sessonManager.getAgoraToken();
        channel_name = sessonManager.getAgoraChanelName();
        //userId = sessonManager.getAgoraUserid();
        //Log.d("all",token+":"+channel_name);
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    private void viewInitiateVideoCall(int chatId) {
        if (CommonUtils.isOnline(VideoChatActivity.this)) {
            Call<InitiateVideoCallModel> call = ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer " + sessonManager.getToken(), chatId,channel_name);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            InitiateVideoCallModel initiateVideoCallModel = response.body();
                            if (initiateVideoCallModel.getData() != null) {
                                String token1 = initiateVideoCallModel.getData().getToken();
                                String channel_name1 = initiateVideoCallModel.getData().getChannelName();
                                String uId = initiateVideoCallModel.getData().getUser_id();
                                int bb = Integer.parseInt(uId);
                                mRtcEngine.joinChannel(token1, channel_name1, "Extra Optional Data", bb);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        } else {
            CommonUtils.showToastInCenter(VideoChatActivity.this, getString(R.string.please_check_network));
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        setupLocalVideo();
        // Tutorial Step 3
        //joinChannel();
        viewInitiateVideoCall(chatId);             // Tutorial Step 4
    }

    private void joinChannel() {
        mRtcEngine.joinChannel("006b39ff59abf8e48728d42ac518e72c844IACL+GrgQMd4LD6cpRKCNC8vwcbS7H5I1Lat2Ln+hQk5gOXmXe2379yDIgCMvgAAxbwaYAQAAQAAAAAAAwAAAAAAAgAAAAAABAAAAAAA", "customerchannel3", "Extra Optional Data", 1);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        //Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    // Tutorial Step 10
    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);

    }

    // Tutorial Step 9
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 8
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    // Tutorial Step 6
    public void onEncCallClicked(View view) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                finish();
                // Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            //Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_STREAM_HIGH, false);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    // Tutorial Step 4
    /*private void joinChannel() {
        if (token!=null||channel_name!=null||userId!=null){
            int uId=Integer.parseInt(userId);
            mRtcEngine.joinChannel("006b39ff59abf8e48728d42ac518e72c844IADEgcNfCvYNcB93mmFZToK34EmCbPPVbO5qYWk28RivneXmXe2bjtJtIgBk/QAAQKoaYAQAAQAAAAAAAwAAAAAAAgAAAAAABAAAAAAA", channel_name, "Extra Optional Data", uId);
        }
        // if you do not specify the uid, we will generate the uid for you
    }*/

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

        surfaceView.setTag(uid); // for mark purpose
        //View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        //tipMsg.setVisibility(View.GONE);
    }

    // Tutorial Step 6
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();

        //View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        //tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 10
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }
}