package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.WorkDetails.WorkDetailsModel;
import com.shopprdriver.Model.WorkLocation.WorkLocationModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkLocationActivity extends AppCompatActivity {
    SessonManager sessonManager;
    RadioGroup groupRadio;
    Spinner spinnerWorkList;
    List<WorkLocationModel.Location>locationList;
    ArrayList<Integer>locationIdList=new ArrayList<>();
    ArrayList<String>locationNameList=new ArrayList<>();
    int locationWorkId,selectedItem;
    String locationWorkName;
    List<Integer>lll=new ArrayList<>();
    String radioV;
    int selectedId;
    RadioButton partTime,fullTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_location);
        String form_step = getIntent().getStringExtra("form_step");
        if (form_step != null) {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + form_step + "</font>")));
        } else {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + 4 + "</font>")));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        Log.d("Token",sessonManager.getToken());
        groupRadio=findViewById(R.id.groupRadio);
        partTime=findViewById(R.id.partTime);
        fullTime=findViewById(R.id.fullTime);
        spinnerWorkList=findViewById(R.id.spinnerWorkList);
        radioV="3";
        groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedId = groupRadio.getCheckedRadioButtonId();
                // radioV=String.valueOf(selectedId);

                if (checkedId == R.id.partTime) {
                    radioV="0";
                   // Log.d(TAG, "A");
                } else if (checkedId == R.id.fullTime) {
                  //  Log.d(TAG, "B");
                    radioV="1";
                }


            }

             /*   Log.d("checkedId=", String.valueOf(checkedId));

                if(checkedId==1)
                {
                    radioV="0";
                }else if(checkedId==2)
                {
                    radioV="1";
                }

            }*/
        });
        viewWorkLocationList();
    }

    private void viewWorkLocationList() {
        Call<WorkLocationModel>call= ApiExecutor.getApiService(this)
                .apiWorkLocation();
        call.enqueue(new Callback<WorkLocationModel>() {
            @Override
            public void onResponse(Call<WorkLocationModel> call, Response<WorkLocationModel> response) {
                //Log.d("nameLocation",response.body().getStatus());
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        WorkLocationModel workLocationModel=response.body();
                        if (workLocationModel.getData()!=null){
                            locationList=workLocationModel.getData().getLocations();

                            for (int i=0;i<workLocationModel.getData().getLocations().size();i++){
                                locationWorkId=workLocationModel.getData().getLocations().get(i).getId();
                                locationWorkName=workLocationModel.getData().getLocations().get(i).getName();

                                locationIdList.add(locationWorkId);
                                locationNameList.add(locationWorkName);
                            }
                        }
                        //locationIdList.add(0,0);
                        ArrayAdapter<String> stateAdaoter = new ArrayAdapter<String>(WorkLocationActivity.this, android.R.layout.simple_list_item_1, locationNameList);
                        spinnerWorkList.setAdapter(stateAdaoter);
                        spinnerWorkList.setOnItemSelectedListener(new MyOnItemSelectedListener());
                    }
                }
            }

            @Override
            public void onFailure(Call<WorkLocationModel> call, Throwable t) {

            }
        });
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
        /*if (radioV.length()==0||lll.size()==0){
            Toast.makeText(this, "Please select any option( Part Time / Full Time )", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, ""+radioV+lll, Toast.LENGTH_SHORT).show();
            service(radioV,lll);
        }*/
        service();

    }

    private void service() {

        Log.d("TokenResponse",sessonManager.getToken()+":"+lll+":"+radioV);

        if (CommonUtils.isOnline(WorkLocationActivity.this)) {
            sessonManager.showProgress(WorkLocationActivity.this);
            Call<WorkDetailsModel>call=ApiExecutor.getApiService(this)
                    .apiWorkDetails("Bearer " + sessonManager.getToken(),lll,radioV);
            call.enqueue(new Callback<WorkDetailsModel>() {
                @Override
                public void onResponse(Call<WorkDetailsModel> call, Response<WorkDetailsModel> response) {
                    //Log.d("nameLocation",response.body().getStatus());
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        Log.d("res",response.body().getStatus());
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(WorkLocationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            WorkDetailsModel workDetailsModel=response.body();
                            int step=workDetailsModel.getFormStep();
                            String form_step=String.valueOf(step);
                            startActivity(new Intent(WorkLocationActivity.this, MenuActivity.class)
                                    .putExtra("form_step",form_step)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            finishAffinity();
                        }else {
                            Toast.makeText(WorkLocationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<WorkDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(WorkLocationActivity.this, getString(R.string.please_check_network));
        }

    }

    private class MyOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lll.clear();
            selectedItem = locationList.get(position).getId();
            lll.add(selectedItem);
                    /*parent.getItemAtPosition(position).toString()*/
            //Toast.makeText(WorkLocationActivity.this, ""+selectedItem, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}