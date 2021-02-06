package com.shopprdriver.SendBird.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shopprdriver.Activity.OtpActivity;
import com.shopprdriver.Activity.VideoChatActivity;
import com.shopprdriver.SendBird.BaseApplication;

public class ActivityUtils {
    public static final int START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE = 1;

    public static void startSignInManuallyActivityForResult(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startSignInManuallyActivityAndFinish()");

        Intent intent = new Intent(activity, OtpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE);
    }

    public static void startMainActivityAndFinish(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startMainActivityAndFinish()");

        Intent intent = new Intent(activity, VideoChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

}
