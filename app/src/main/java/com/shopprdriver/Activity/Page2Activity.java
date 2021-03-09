package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.AccountDetailsRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Page2Activity extends AppCompatActivity {
    EditText editHolderName, editBankName,
            editAccountNo, editIfsc;
    SessonManager sessonManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
        String form_step = getIntent().getStringExtra("form_step");
        if (form_step != null) {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + form_step + "</font>")));
        } else {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + 2 + "</font>")));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);

        Log.d("Token",sessonManager.getToken());
        /*Todo:- EditText Find Id*/
        editHolderName = findViewById(R.id.editHolderName);
        editBankName = findViewById(R.id.editBankName);
        editAccountNo = findViewById(R.id.editAccountNo);
        editIfsc = findViewById(R.id.editIfsc);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void submit(View view) {
        if (editHolderName.getText().toString().isEmpty()) {
            editHolderName.setError("Account holder name field Can't be blank");
            editHolderName.requestFocus();
        } else if (editBankName.getText().toString().isEmpty()) {
            editBankName.setError("Bank name field Can't be blank");
            editBankName.requestFocus();
        } else if (editAccountNo.getText().toString().isEmpty()) {
            editAccountNo.setError("Account No. field can't be blank");
            editAccountNo.requestFocus();
        } else if (editIfsc.getText().toString().isEmpty()) {
            editIfsc.setError("IFSC Code field can't be blank");
            editIfsc.requestFocus();

        } else {
            apiAccountDetails();
        }
    }

    private void apiAccountDetails() {
        if (CommonUtils.isOnline(Page2Activity.this)) {
            sessonManager.showProgress(Page2Activity.this);
            AccountDetailsRequest accountDetailsRequest=new AccountDetailsRequest();
            accountDetailsRequest.setAccount_holder(editHolderName.getText().toString());
            accountDetailsRequest.setBank_name(editBankName.getText().toString());
            accountDetailsRequest.setAccount_no(editAccountNo.getText().toString());
            accountDetailsRequest.setIfsc_code(editIfsc.getText().toString());
            Call<UploadDocumentModel>call= ApiExecutor.getApiService(this)
                    .apiUpdateDetails("Bearer " + sessonManager.getToken(),accountDetailsRequest);
            call.enqueue(new Callback<UploadDocumentModel>() {
                @Override
                public void onResponse(Call<UploadDocumentModel> call, Response<UploadDocumentModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(Page2Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            UploadDocumentModel uploadDocumentModel=response.body();
                            int step=uploadDocumentModel.getFormStep();
                            String form_step=String.valueOf(step);
                            startActivity(new Intent(Page2Activity.this, MenuActivity.class)
                                    .putExtra("form_step",form_step)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            finishAffinity();
                        }else {
                            Toast.makeText(Page2Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDocumentModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });


        }else {
            CommonUtils.showToastInCenter(Page2Activity.this, getString(R.string.please_check_network));
        }
    }
}