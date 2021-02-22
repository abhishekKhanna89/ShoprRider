package com.shopprdriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.Login.LoginModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.LoginRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editMobile;
    Button btnLogin;
    TextView textRegister;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessonManager=new SessonManager(this);

        editMobile=findViewById(R.id.editMobile);
        btnLogin=findViewById(R.id.btnLogin);
        textRegister=findViewById(R.id.textRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMobile.getText().toString().isEmpty()){
                    editMobile.setError("Mobile Field Can't be blank");
                    editMobile.requestFocus();
                }
                else if(editMobile.getText().toString().length()!=10){
                    editMobile.setError("Mobile No. should be 10 digit");
                    editMobile.requestFocus();
                }
                else {
                    MobileEmailAPI();
                }
            }
        });
    }

    private void MobileEmailAPI() {
        if (CommonUtils.isOnline(LoginActivity.this)) {
            sessonManager.showProgress(LoginActivity.this);
            LoginRequest loginRequest=new LoginRequest();
            loginRequest.setMobile(editMobile.getText().toString());
            Call<LoginModel> call= ApiExecutor.getApiService(LoginActivity.this)
                    .loginUser(loginRequest);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editMobile.getText().toString().isEmpty())){
                                startActivity(new Intent(LoginActivity.this,OtpActivity.class)
                                        .putExtra("type","login")
                                        .putExtra("mobile",editMobile.getText().toString())
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }else {
                                sessonManager.getToken();
                                startActivity(new Intent(LoginActivity.this, MenuActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        }else {
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(LoginActivity.this, getString(R.string.please_check_network));
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}