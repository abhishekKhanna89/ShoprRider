package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.R;

public class AccountInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
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