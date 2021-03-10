package com.shopprdriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
    RadioButton work_time;
    List<WorkLocationModel.Location>locationList;
    ArrayList<Integer>locationIdList=new ArrayList<>();
    ArrayList<String>locationNameList=new ArrayList<>();
    int locationWorkId,selectedItem;
    String locationWorkName,workPart;
    List<Integer>lll=new ArrayList<>();
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
        spinnerWorkList=findViewById(R.id.spinnerWorkList);
        groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId=groupRadio.getCheckedRadioButtonId();
                work_time=(RadioButton)findViewById(selectedId);
                String work=work_time.getText().toString();
                if (work.equals("Part Time")){
                    workPart="part-time";
                }else if (work.equals("Permanent")){
                    workPart="permanent";
                }
                //Toast.makeText(WorkLocationActivity.this, ""+selectedId, Toast.LENGTH_SHORT).show();
            }
        });
        viewWorkLocationList();
    }

    private void viewWorkLocationList() {
        Call<WorkLocationModel>call= ApiExecutor.getApiService(this)
                .apiWorkLocation();
        call.enqueue(new Callback<WorkLocationModel>() {
            @Override
            public void onResponse(Call<WorkLocationModel> call, Response<WorkLocationModel> response) {
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
        if (TextUtils.isEmpty(workPart)){
            Toast.makeText(this, "Please select any option( Permanent / Part Time )", Toast.LENGTH_SHORT).show();
        }else {
            service();
        }
    }

    private void service() {
        if (CommonUtils.isOnline(WorkLocationActivity.this)) {
            sessonManager.showProgress(WorkLocationActivity.this);
            Call<WorkDetailsModel>call=ApiExecutor.getApiService(this)
                    .apiWorkDetails("Bearer " + sessonManager.getToken(),lll,workPart);
            call.enqueue(new Callback<WorkDetailsModel>() {
                @Override
                public void onResponse(Call<WorkDetailsModel> call, Response<WorkDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
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
            lll.add(selectedItem)
                    /*parent.getItemAtPosition(position).toString()*/;
            //Toast.makeText(WorkLocationActivity.this, ""+selectedItem, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}