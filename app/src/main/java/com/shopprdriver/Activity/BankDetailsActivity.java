package com.shopprdriver.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.BankDetailsGet.BankDetailsGetModel;
import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.AccountDetailsRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {
    SessonManager sessonManager;
    EditText editHolderName, editBankName,
            editAccountNo, editIfsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        /*Todo:- EditText Find Id*/
        editHolderName = findViewById(R.id.editHolderName);
        editBankName = findViewById(R.id.editBankName);
        editAccountNo = findViewById(R.id.editAccountNo);
        editIfsc = findViewById(R.id.editIfsc);
        viewBankDetails();
    }
    private void viewBankDetails() {
        if (CommonUtils.isOnline(BankDetailsActivity.this)) {
            //sessonManager.showProgress(BankDetailsActivity.this);
            Call<BankDetailsGetModel> call= ApiExecutor.getApiService(this)
                    .apiViewBankDetails("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<BankDetailsGetModel>() {
                @Override
                public void onResponse(Call<BankDetailsGetModel> call, Response<BankDetailsGetModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            //Toast.makeText(BankDetailsActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                            BankDetailsGetModel bankDetailsGetModel=response.body();
                            if (bankDetailsGetModel.getData().getUser()!=null){
                                editHolderName.setText(bankDetailsGetModel.getData().getUser().getAccountHolder());
                                editBankName.setText(bankDetailsGetModel.getData().getUser().getBankName());
                                editAccountNo.setText(bankDetailsGetModel.getData().getUser().getAccountNo());
                                editIfsc.setText(bankDetailsGetModel.getData().getUser().getIfscCode());
                            }
                        }else {
                            //Toast.makeText(BankDetailsActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BankDetailsGetModel> call, Throwable t) {
                    //sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(BankDetailsActivity.this, getString(R.string.please_check_network));
        }
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
        if (CommonUtils.isOnline(BankDetailsActivity.this)) {
            sessonManager.showProgress(BankDetailsActivity.this);
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
                            sessonManager.setAccountUpdateDetails("step1");
                            Toast.makeText(BankDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            viewBankDetails();
                        }else {
                            Toast.makeText(BankDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadDocumentModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });


        }else {
            CommonUtils.showToastInCenter(BankDetailsActivity.this, getString(R.string.please_check_network));
        }
    }
}