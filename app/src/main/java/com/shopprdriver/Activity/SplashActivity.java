package com.shopprdriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shopprdriver.MainActivity;
import com.shopprdriver.R;
import com.shopprdriver.Session.SessonManager;

public class SplashActivity extends AppCompatActivity {
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(SplashActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            //Log.d("token",newToken);
            sessonManager.setNotificationToken(newToken);
            //Log.e("newToken", newToken);
            //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessonManager.getToken().isEmpty()){

                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();

                }
            }
        }, 5000);
    }
}