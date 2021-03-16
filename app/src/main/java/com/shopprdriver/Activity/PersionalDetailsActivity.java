package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.PersonalDetails.PersonalDetailsModel;
import com.shopprdriver.Model.StateList.City;
import com.shopprdriver.Model.StateList.State;
import com.shopprdriver.Model.StateList.StateListModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.PersonalDetailsRequest;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersionalDetailsActivity extends AppCompatActivity {
    SessonManager sessonManager;

    EditText editPAddress,editPPNo,editSMNo,editEMNo;

    /*Todo:- State List*/
    Spinner textCity;
    Spinner spinnerState;
    List<State> stateList;
    List<City> cityList;
    List<String> stateName;
    List<String> cityName;
    int stateId,cityId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persional_details);
        String form_step = getIntent().getStringExtra("form_step");
        if (form_step != null) {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + form_step + "</font>")));
        } else {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + 3 + "</font>")));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        /*Todo:- Find EditText Id*/
        editPAddress=findViewById(R.id.editPAddress);
        editPPNo=findViewById(R.id.editPPNo);
        editSMNo=findViewById(R.id.editSMNo);
        editEMNo=findViewById(R.id.editEMNo);

        /*Todo:- State List*/
        spinnerState = findViewById(R.id.spinnerState);
        textCity = findViewById(R.id.textCity);

        stateName = new ArrayList<>();
        cityName = new ArrayList<>();

        viewStateList();


        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateId=stateList.get(position).getId();
                cityName.clear();
                if (stateList.get(position).getCities().size() != 0) {
                    cityList = stateList.get(position).getCities();
                    for (int i = 0; i < cityList.size(); i++) {
                        cityName.add(cityList.get(i).getName());
                        ArrayAdapter cityAdapter = new ArrayAdapter(PersionalDetailsActivity.this, android.R.layout.simple_list_item_1, cityName);
                        textCity.setAdapter(cityAdapter);
                    }
                } else {
                    //Toast.makeText(RegisterActivity.this, "elsePart", Toast.LENGTH_SHORT).show();
                    textCity.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId=cityList.get(position).getId();
                /*city=textCity.getItemAtPosition(position).toString();
                String id = textCity.get(position).getId();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void viewStateList() {
        if (CommonUtils.isOnline(PersionalDetailsActivity.this)) {
            Call<StateListModel> call = ApiExecutor.getApiService(this)
                    .apiStateList();
            call.enqueue(new Callback<StateListModel>() {
                @Override
                public void onResponse(Call<StateListModel> call, Response<StateListModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StateListModel stateListModel = response.body();
                            if (stateListModel.getData().getStates() != null) {
                                stateList = stateListModel.getData().getStates();
                                for (int i = 0; i < stateListModel.getData().getStates().size(); i++) {
                                    String state = stateList.get(i).getName();
                                    stateName.add(state);
                                }
                                ArrayAdapter stateAdaoter = new ArrayAdapter(PersionalDetailsActivity.this, android.R.layout.simple_list_item_1, stateName);
                                spinnerState.setAdapter(stateAdaoter);

                            }
                        }else {
                            sessonManager.setToken("");
                            PrefUtils.setAppId(PersionalDetailsActivity.this,"");
                            Toast.makeText(PersionalDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PersionalDetailsActivity.this, LoginActivity.class));
                            finishAffinity();
                        }
                    }
                }

                @Override
                public void onFailure(Call<StateListModel> call, Throwable t) {

                }
            });

        } else {
            CommonUtils.showToastInCenter(PersionalDetailsActivity.this, getString(R.string.please_check_network));
        }
    }

    public void submit(View view) {
        if (editPAddress.getText().toString().isEmpty()) {
            editPAddress.setError("Permanent Address Field Can't be blank");
            editPAddress.requestFocus();
        }else if (editPPNo.getText().toString().isEmpty()){
            editPPNo.setError("Permanent Pin Code Field Can't be blank");
            editPPNo.requestFocus();
        }
        else if (editSMNo.getText().toString().isEmpty()) {
            editSMNo.setError("Secondary Mobile No. Field Can't be blank");
            editSMNo.requestFocus();
        }
        else if (editSMNo.getText().toString().length() != 10) {
            editSMNo.setError("Mobile No. should be 10 digit");
            editSMNo.requestFocus();
        } else if (editEMNo.getText().toString().isEmpty()) {
            editEMNo.setError("Secondary Mobile No. Field Can't be blank");
            editEMNo.requestFocus();
        } else if (editEMNo.getText().toString().length() != 10) {
            editEMNo.setError("Mobile No. should be 10 digit");
            editEMNo.requestFocus();
        } else if (cityId==0){
            Toast.makeText(this, "City not available", Toast.LENGTH_SHORT).show();
        }
        else {
            personalDetailsAPI();
        }
    }

    private void personalDetailsAPI() {
        if (CommonUtils.isOnline(PersionalDetailsActivity.this)) {
            sessonManager.showProgress(PersionalDetailsActivity.this);
            PersonalDetailsRequest personalDetailsRequest = new PersonalDetailsRequest();
            personalDetailsRequest.setPermanent_address(editPAddress.getText().toString());
            personalDetailsRequest.setPermanent_pin(editPPNo.getText().toString());
            personalDetailsRequest.setSecondary_mobile(editSMNo.getText().toString());
            personalDetailsRequest.setEmergency_mobile(editEMNo.getText().toString());
            personalDetailsRequest.setPermanent_state(stateId);
            personalDetailsRequest.setPermanent_city(cityId);
            Call<PersonalDetailsModel> call = ApiExecutor.getApiService(this)
                    .apiPersonalDetails("Bearer " + sessonManager.getToken(), personalDetailsRequest);
            call.enqueue(new Callback<PersonalDetailsModel>() {
                @Override
                public void onResponse(Call<PersonalDetailsModel> call, Response<PersonalDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(PersionalDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            PersonalDetailsModel uploadDocumentModel=response.body();
                            int step=uploadDocumentModel.getForm_step();
                            String form_step=String.valueOf(step);
                            sessonManager.setAccountUpdateDetails("step4");
                            startActivity(new Intent(PersionalDetailsActivity.this, WorkLocationActivity.class)
                                    .putExtra("form_step",form_step)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            finishAffinity();
                        }else {
                            Toast.makeText(PersionalDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PersonalDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(PersionalDetailsActivity.this, getString(R.string.please_check_network));
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
}