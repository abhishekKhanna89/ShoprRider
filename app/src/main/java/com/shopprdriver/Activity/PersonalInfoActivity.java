package com.shopprdriver.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.shopprdriver.Model.PersonalDetails.PersonalDetailsModel;
import com.shopprdriver.Model.PersonalInfoView.PersonalInfoViewModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.PersonalDetailsRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {
    SessonManager sessonManager;

    EditText editPAddress,editPPNo,editSMNo,editEMNo;

    /*Todo:- State List*/
    Spinner textCity;
    Spinner spinnerState;
    /*Todo:- Get Value Spinner*/
    List<PersonalInfoViewModel.State>stateList;
    List<PersonalInfoViewModel.City>cityList;
    /*Todo:- State*/
    ArrayList<Integer>stateIdList=new ArrayList<>();
    ArrayList<String>stateNameList=new ArrayList<>();
    /*Todo:- City*/
    ArrayList<String>cityIdList=new ArrayList<>();
    ArrayList<String>cityNameList=new ArrayList<>();

    List<Integer>lll=new ArrayList<>();

    String stateIdU;
    int stateId,cityId;
    ArrayAdapter<String> stateAdaoter;
    ArrayAdapter<String> cityAdapter;

    String cityN,value;
    int cityI,cityIdU;
    int aaa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
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
        viewPersonalView();


       // spinnerState.setSelection(Integer.parseInt(stateIdU));

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateId=stateList.get(position).getId();
                //Log.d("StateSpinnerId",""+stateId);
                cityNameList.clear();
                if (stateList.get(position).getCities().size() != 0) {
                    cityList = stateList.get(position).getCities();
                    Log.d("citySize",""+stateList.get(position).getCities().size());
                    for (int i = 0; i < cityList.size(); i++) {
                        cityI=cityList.get(i).getId();
                        cityN=cityList.get(i).getName();
                        if(cityI==cityIdU){
                            value=cityN;
                        }
                        cityNameList.add(cityN);
                    }
                    cityAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this, android.R.layout.simple_list_item_1, cityNameList);
                    textCity.setAdapter(cityAdapter);
                    if (value!=null){
                        textCity.setSelection(((ArrayAdapter<String>)textCity.getAdapter()).getPosition(value));
                    }
                    aaa=stateList.get(position).getCities().size();
                } else {
                    aaa=stateList.get(position).getCities().size();
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
                Log.d("resCityId",""+cityI);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void viewPersonalView() {
        if (CommonUtils.isOnline(PersonalInfoActivity.this)) {
            Call<PersonalInfoViewModel>call= ApiExecutor.getApiService(this)
                    .apiPersonalDetailsView("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<PersonalInfoViewModel>() {
                @Override
                public void onResponse(Call<PersonalInfoViewModel> call, Response<PersonalInfoViewModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            PersonalInfoViewModel personalInfoViewModel=response.body();
                            String respons=new Gson().toJson(personalInfoViewModel);
                            Log.d("resAAA",respons);
                            if (personalInfoViewModel.getData()!=null){
                                /*Todo:- User */
                                editPAddress.setText(personalInfoViewModel.getData().getUser().getPermanentAddress());
                                editPPNo.setText(personalInfoViewModel.getData().getUser().getPermanentPin());
                                editSMNo.setText(personalInfoViewModel.getData().getUser().getSecondaryMobile());
                                editEMNo.setText(personalInfoViewModel.getData().getUser().getEmergencyMobile());
                                /*Todo:- State Spinner*/
                                stateIdU = personalInfoViewModel.getData().getUser().getPermanentState();
                                Log.d("resStateI",stateIdU);

                                /*Todo:- City Spinner*/
                                cityIdU = Integer.parseInt(personalInfoViewModel.getData().getUser().getPermanentCity());
                                stateList=personalInfoViewModel.getData().getStates();
                                for (int i=0;i<stateList.size();i++){
                                    int stateI=personalInfoViewModel.getData().getStates().get(i).getId();
                                    String stateN=personalInfoViewModel.getData().getStates().get(i).getName();
                                    stateIdList.add(stateI);
                                    stateNameList.add(stateN);

                                }
                                stateAdaoter = new ArrayAdapter<String>(PersonalInfoActivity.this, android.R.layout.simple_list_item_1, stateNameList);
                                spinnerState.setAdapter(stateAdaoter);
                               /* if (stateIdU!=null){
                                    spinnerState.setSelection(Integer.parseInt(stateIdU));
                                    //spinnerState.setSelection(((ArrayAdapter<String>)spinnerState.getAdapter()).getPosition(stateIdU));
                                }*/
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<PersonalInfoViewModel> call, Throwable t) {

                }
            });


        }else {
            CommonUtils.showToastInCenter(PersonalInfoActivity.this, getString(R.string.please_check_network));
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
        } else if (aaa==0){
            Toast.makeText(this, "City not available", Toast.LENGTH_SHORT).show();
        }
        else {
            personalDetailsAPI();
        }
    }
    private void personalDetailsAPI() {
        if (CommonUtils.isOnline(PersonalInfoActivity.this)) {
            sessonManager.showProgress(PersonalInfoActivity.this);
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
                            sessonManager.setAccountUpdateDetails("step3");
                             stateNameList.clear();
                             Toast.makeText(PersonalInfoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                             viewPersonalView();
                            /*PersonalDetailsModel uploadDocumentModel=response.body();
                            int step=uploadDocumentModel.getForm_step();
                            String form_step=String.valueOf(step);
                            startActivity(new Intent(PersonalInfoActivity.this, WorkLocationActivity.class)
                                    .putExtra("form_step",form_step)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            finishAffinity();*/
                        }else {
                            Toast.makeText(PersonalInfoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PersonalDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(PersonalInfoActivity.this, getString(R.string.please_check_network));
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