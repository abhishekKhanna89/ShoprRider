package com.shopprdriver.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.OtpVerification.OtpVerifyModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.OtpVerifyRequest;
import com.shopprdriver.SendBird.BaseApplication;
import com.shopprdriver.SendBird.call.CallService;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    Button btnVerify;
    EditText editOtp;
    SessonManager sessonManager;
    String type,mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sessonManager=new SessonManager(this);

        type=getIntent().getStringExtra("type");
        mobile=getIntent().getStringExtra("mobile");

        btnVerify=findViewById(R.id.btnsubmit);
        editOtp=findViewById(R.id.editusername);



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editOtp.getText().toString().isEmpty()){
                    editOtp.setError("OTP Field Can't be blank");
                    editOtp.requestFocus();
                }
                else if(editOtp.getText().toString().length()!=6){
                    editOtp.setError("OTP should be 6 digit");
                    editOtp.requestFocus();
                }
                else {
                    OtpVerifyAPI();
                }
            }
        });
    }

    private void OtpVerifyAPI() {
        if (CommonUtils.isOnline(OtpActivity.this)) {
            sessonManager.showProgress(OtpActivity.this);
            OtpVerifyRequest otpVerifyRequest=new OtpVerifyRequest();
            otpVerifyRequest.setOtp(editOtp.getText().toString());
            otpVerifyRequest.setMobile(mobile);
            otpVerifyRequest.setType(type);
            otpVerifyRequest.setNotification_token(sessonManager.getNotificationToken());

            Call<OtpVerifyModel> call= ApiExecutor.getApiService(OtpActivity.this)
                    .otpService(otpVerifyRequest);
            call.enqueue(new Callback<OtpVerifyModel>() {
                @Override
                public void onResponse(Call<OtpVerifyModel> call, Response<OtpVerifyModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            OtpVerifyModel otpVerifyModel=response.body();
                            String userId=otpVerifyModel.getUser_id();
                            String sendbird_token=otpVerifyModel.getSendbird_token();
                            //Log.d("res",sendbird_token);
                            int form_step=otpVerifyModel.getForm_step();
                            //String savedAppId = PrefUtils.getAppId(OtpActivity.this);
                            String savedAppId= BaseApplication.APP_ID;
                            Log.d("savedid+++",savedAppId);
                            PrefUtils.setAppId(OtpActivity.this,savedAppId);
                            if((!editOtp.getText().toString().isEmpty())){
                                sessonManager.setToken(response.body().getToken());
                                if (!TextUtils.isEmpty(savedAppId) && !TextUtils.isEmpty(userId) &&((BaseApplication)getApplication()).initSendBirdCall(savedAppId)) {
                                    AuthenticationUtils.authenticate(OtpActivity.this, userId, sendbird_token, isSuccess -> {
                                        if (isSuccess) {
                                            setResult(RESULT_OK, null);
                                            sessonManager.getNotificationToken();
                                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            CallService.dial(OtpActivity.this, "lakshmikant", String.valueOf(form_step),type, false);
                                            /*sessonManager.getNotificationToken();
                                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                            if (type.equalsIgnoreCase("login")){
                                                if (form_step==1){
                                                    startActivity(new Intent(OtpActivity.this, Page1Activity.class));
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    finish();
                                                }else if (form_step==2){
                                                    startActivity(new Intent(OtpActivity.this, Page2Activity.class));
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    finish();
                                                }else if (form_step==3){
                                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step1")){
                                                        PrefUtils.getAppId(OtpActivity.this);
                                                        startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step2")){
                                                        startActivity(new Intent(OtpActivity.this, PersionalDetailsActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }else {
                                                        startActivity(new Intent(OtpActivity.this, PersionalDetailsActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }
                                                }else if (form_step==4){
                                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step3")){
                                                        PrefUtils.getAppId(OtpActivity.this);
                                                        startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step4")){
                                                        startActivity(new Intent(OtpActivity.this, WorkLocationActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }else {
                                                        startActivity(new Intent(OtpActivity.this, WorkLocationActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }

                                                }else {
                                                    sessonManager.getNotificationToken();
                                                    PrefUtils.getAppId(OtpActivity.this);
                                                    startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    finish();
                                                }


                                            }else {

                                                //sessonManager.setForm_Step(form_step);
                                                startActivity(new Intent(OtpActivity.this, Page1Activity.class)
                                                        .putExtra("form_step",form_step)
                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                                //Toast.makeText(OtpActivity.this, "register", Toast.LENGTH_SHORT).show();
                                            }*/

                                        }
                                    });

                                }
                                /*if (((BaseApplication)getApplication()).initSendBirdCall(savedAppId)) {
                                    AuthenticationUtils.authenticate(OtpActivity.this, userId, sendbird_token, isSuccess -> {
                                        if (isSuccess) {
                                            setResult(RESULT_OK, null);
                                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            if (type.equalsIgnoreCase("login")){
                                                if (form_step==1){
                                                    startActivity(new Intent(OtpActivity.this, Page1Activity.class));
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    finish();
                                                }else if (form_step==2){
                                                    startActivity(new Intent(OtpActivity.this, Page2Activity.class));
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    finish();
                                                }else if (form_step==3){
                                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step1")){
                                                        PrefUtils.getAppId(OtpActivity.this);
                                                        startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step2")){
                                                        startActivity(new Intent(OtpActivity.this, PersionalDetailsActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }else {
                                                        startActivity(new Intent(OtpActivity.this, PersionalDetailsActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }
                                                }else if (form_step==4){
                                                    if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step3")){
                                                        PrefUtils.getAppId(OtpActivity.this);
                                                        startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step4")){
                                                        startActivity(new Intent(OtpActivity.this, WorkLocationActivity.class));
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        finish();
                                                    }else {
                                                    startActivity(new Intent(OtpActivity.this, WorkLocationActivity.class));
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    finish();
                                                    }

                                                }else {
                                                    sessonManager.getNotificationToken();
                                                    PrefUtils.getAppId(OtpActivity.this);
                                                    startActivity(new Intent(OtpActivity.this, MenuActivity.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    finish();
                                                }


                                            }else {

                                                //sessonManager.setForm_Step(form_step);
                                                startActivity(new Intent(OtpActivity.this, Page1Activity.class)
                                                        .putExtra("form_step",form_step)
                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                                //Toast.makeText(OtpActivity.this, "register", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }*/
                            }
                        }else {
                            Toast.makeText(OtpActivity.this, "OTP is not correct", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpVerifyModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(OtpActivity.this, getString(R.string.please_check_network));
        }
    }

    public void ResendOTP(View view) {

    }
}