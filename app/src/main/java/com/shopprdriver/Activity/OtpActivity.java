package com.shopprdriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.OtpVerification.OtpVerifyModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.OtpVerifyRequest;
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

        btnVerify=findViewById(R.id.btnVerify);
        editOtp=findViewById(R.id.editOtp);

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
                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editOtp.getText().toString().isEmpty())){
                                sessonManager.setToken(response.body().getToken());
                                startActivity(new Intent(OtpActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        }else {
                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
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