package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.R;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Session.SessonManager;
import com.skyfishjy.library.RippleBackground;

public class InitilizingActivity extends AppCompatActivity {
    RippleBackground rippleBackground;
    ImageView centerImage;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initilizing);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        sessonManager=new SessonManager(this);
        centerImage=findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        Integer form_step=getIntent().getIntExtra("step_from",0);
        String type=getIntent().getStringExtra("type");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackground.stopRippleAnimation();
                        if (type.equalsIgnoreCase("login")){
                            if (form_step==1){
                                startActivity(new Intent(InitilizingActivity.this, Page1Activity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }else if (form_step==2){
                                startActivity(new Intent(InitilizingActivity.this, Page2Activity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }else if (form_step==3){
                                if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step1")){
                                    PrefUtils.getAppId(InitilizingActivity.this);
                                    startActivity(new Intent(InitilizingActivity.this, MenuActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step2")){
                                    startActivity(new Intent(InitilizingActivity.this, PersionalDetailsActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }else {
                                    startActivity(new Intent(InitilizingActivity.this, PersionalDetailsActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }
                            }else if (form_step==4){
                                if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step3")){
                                    PrefUtils.getAppId(InitilizingActivity.this);
                                    startActivity(new Intent(InitilizingActivity.this, MenuActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }else if (sessonManager.getAccountUpdateDetails()!=null&&sessonManager.getAccountUpdateDetails().equalsIgnoreCase("step4")){
                                    startActivity(new Intent(InitilizingActivity.this, WorkLocationActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }else {
                                    startActivity(new Intent(InitilizingActivity.this, WorkLocationActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }

                            }else {
                                sessonManager.getNotificationToken();
                                PrefUtils.getAppId(InitilizingActivity.this);
                                startActivity(new Intent(InitilizingActivity.this, MenuActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }


                        }else {
                            //sessonManager.setForm_Step(form_step);
                            startActivity(new Intent(InitilizingActivity.this, Page1Activity.class)
                                    .putExtra("form_step",form_step)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            //Toast.makeText(OtpActivity.this, "register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }, 1000);

    }
}