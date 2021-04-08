package com.shopprdriver.Activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shopprdriver.Model.ProfileStatus.ProfileStatusModel;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.app.Config;
import com.shopprdriver.util.NotificationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG ="" ;
    SessonManager sessonManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Boolean mAutoAuthenticateResult;
    private String mEncodedAuthInfo;
    int step_form;
    String newToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(SplashActivity.this);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            // Log.d("responseNotification",newToken);
            sessonManager.setNotificationToken(newToken);
            //Log.e("newToken", newToken);
            //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });

        AuthenticationUtils.autoAuthenticate(this, userId -> {

        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessonManager.getToken().isEmpty()){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                else {
                    viewStatus();
                }

            }
        }, 3000);
    }
    private void viewStatus() {
        if (CommonUtils.isOnline(SplashActivity.this)) {
            Call<ProfileStatusModel>call= ApiExecutor.getApiService(this)
                    .apiProfileStatus("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ProfileStatusModel>() {
                @Override
                public void onResponse(Call<ProfileStatusModel> call, Response<ProfileStatusModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ProfileStatusModel profileStatusModel=response.body();
                            if (profileStatusModel!=null){
                                 step_form=profileStatusModel.getFormStep();
                                 String type=profileStatusModel.getType();
                                 sessonManager.setCheckout_Status(type);
                                if (step_form==1){
                                    startActivity(new Intent(SplashActivity.this, Page1Activity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }else if (step_form==2){
                                    startActivity(new Intent(SplashActivity.this, Page2Activity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }else if (step_form==3){
                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step1")){
                                        PrefUtils.getAppId(SplashActivity.this);
                                        startActivity(new Intent(SplashActivity.this, MenuActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step2")){
                                        startActivity(new Intent(SplashActivity.this, PersionalDetailsActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }else {
                                        startActivity(new Intent(SplashActivity.this, PersionalDetailsActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }

                                }else if (step_form==4){
                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step3")){
                                        PrefUtils.getAppId(SplashActivity.this);
                                        startActivity(new Intent(SplashActivity.this, MenuActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step4")){
                                        startActivity(new Intent(SplashActivity.this, WorkLocationActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }else {
                                        startActivity(new Intent(SplashActivity.this, WorkLocationActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }

                                }else {
                                    PrefUtils.getAppId(SplashActivity.this);
                                    sessonManager.getNotificationToken();
                                    startActivity(new Intent(SplashActivity.this, MenuActivity.class)
                                            .putExtra("check_out_type",type));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();

                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfileStatusModel> call, Throwable t) {

                }
            });

        }else {
            CommonUtils.showToastInCenter(SplashActivity.this, getString(R.string.please_check_network));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}