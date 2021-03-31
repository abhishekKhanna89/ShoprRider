package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.Profile.ProfileModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInfoActivity extends AppCompatActivity {
    TextView username,usermobile;
    ImageView userImage;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        sessonManager=new SessonManager(this);
        username=findViewById(R.id.username);
        usermobile=findViewById(R.id.usermobile);
        userImage=findViewById(R.id.userImage);

        viewProfile();


    }

    private void viewProfile() {
        if (CommonUtils.isOnline(this)) {
            Call<ProfileModel> call = ApiExecutor.getApiService(this)
                    .apiProfileView("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ProfileModel>() {
                @Override
                public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                           ProfileModel profileModel=response.body();
                           if (profileModel.getData().getUser()!=null){
                               String userName=profileModel.getData().getUser().getName();
                               username.setText("Welcome to "+userName);
                               String userMobile=profileModel.getData().getUser().getMobile();
                               String image=profileModel.getData().getUser().getImage();
                               Picasso.get().load(image).into(userImage);
                               usermobile.setText(userMobile);

                           }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfileModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(this, getString(R.string.please_check_network));
        }

    }

    public void document(View view) {
        startActivity(new Intent(AccountInfoActivity.this,ViewDocumentActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void bankDetails(View view) {
        startActivity(new Intent(AccountInfoActivity.this,BankDetailsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void workDetails(View view) {
        startActivity(new Intent(AccountInfoActivity.this,WorkDetailsViewActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void personalDetails(View view) {
        startActivity(new Intent(AccountInfoActivity.this,PersonalInfoActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}