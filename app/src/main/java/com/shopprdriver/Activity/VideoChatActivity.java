package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.sendbird.calls.DirectCallLog;
import com.sendbird.calls.SendBirdCall;
import com.shopprdriver.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.BaseApplication;
import com.shopprdriver.SendBird.call.CallService;
import com.shopprdriver.SendBird.utils.BroadcastUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.SendBird.utils.ToastUtils;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class VideoChatActivity extends AppCompatActivity {
    private InputMethodManager mInputMethodManager;

    private TextInputEditText mTextInputEditTextUserId;
    private ImageView mImageViewVideoCall;
    private ImageView mImageViewVoiceCall;

    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Context mContext;
    private BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            mInputMethodManager = (InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        setContentView(R.layout.activity_video_chat);


        mContext = this;


        registerReceiver();
        checkPermissions();


        mTextInputEditTextUserId = findViewById(R.id.text_input_edit_text_user_id);
        mImageViewVideoCall = findViewById(R.id.image_view_video_call);
        mImageViewVoiceCall = findViewById(R.id.image_view_voice_call);

        mImageViewVideoCall.setEnabled(false);
        mImageViewVoiceCall.setEnabled(false);

        String savedCalleeId = PrefUtils.getCalleeId(this);
        if (!TextUtils.isEmpty(savedCalleeId)) {
            mTextInputEditTextUserId.setText(savedCalleeId);
            mTextInputEditTextUserId.setSelection(savedCalleeId.length());
            mImageViewVideoCall.setEnabled(true);
            mImageViewVoiceCall.setEnabled(true);
        }

        mTextInputEditTextUserId.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mTextInputEditTextUserId.clearFocus();
                if (mInputMethodManager != null) {
                    mInputMethodManager.hideSoftInputFromWindow(mTextInputEditTextUserId.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        mTextInputEditTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mImageViewVideoCall.setEnabled(editable != null && editable.length() > 0);
                mImageViewVoiceCall.setEnabled(editable != null && editable.length() > 0);
            }
        });

        mImageViewVideoCall.setOnClickListener(view1 -> {
            String calleeId = (mTextInputEditTextUserId.getText() != null ? mTextInputEditTextUserId.getText().toString() : "");
            if (!TextUtils.isEmpty(calleeId)) {
                CallService.dial(this, calleeId, true);
                PrefUtils.setCalleeId(this, calleeId);
            }
        });

        mImageViewVoiceCall.setOnClickListener(view1 -> {
            String calleeId = (mTextInputEditTextUserId.getText() != null ? mTextInputEditTextUserId.getText().toString() : "");
            if (!TextUtils.isEmpty(calleeId)) {
                CallService.dial(this, calleeId, false);
                PrefUtils.setCalleeId(this, calleeId);
            }
        });
    }
    private void registerReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] registerReceiver()");

        if (mReceiver != null) {
            return;
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(BaseApplication.TAG, "[MainActivity] onReceive()");

                DirectCallLog callLog = (DirectCallLog)intent.getSerializableExtra(BroadcastUtils.INTENT_EXTRA_CALL_LOG);
               /* if (callLog != null) {
                    HistoryFragment historyFragment = (HistoryFragment) mMainPagerAdapter.getItem(1);
                    historyFragment.addLatestCallLog(callLog);
                }*/
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
        registerReceiver(mReceiver, intentFilter);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(BaseApplication.TAG, "[MainActivity] onNewIntent()");

        //setUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }
    private void unregisterReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] unregisterReceiver()");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(mContext, "Permission denied.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(mContext, "Permission denied.");
            }
        }
    }
}