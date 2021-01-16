package com.shopprdriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shopprdriver.R;

public class LoginActivity extends AppCompatActivity {
    EditText editMobile;
    Button btnLogin;
    TextView textRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

    }
}