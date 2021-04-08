package com.shopprdriver.Activity;

import android.os.Bundle;
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
import com.shopprdriver.Model.WorkDetailsView.WorkDetailsViewModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkDetailsViewActivity extends AppCompatActivity {
    RadioButton ptRadio,ftRadio;
    SessonManager sessonManager;
    RadioGroup groupRadio;
    Spinner spinnerWorkList;
    List<WorkDetailsViewModel.Location>locationList;
    List<WorkDetailsViewModel.WorkLocation>workLocationList;
    /*Todo:- Spinner*/
    ArrayList<Integer>spinner_id_list=new ArrayList<>();
    ArrayList<String>spinner_name_list=new ArrayList<>();
    List<Integer>lll=new ArrayList<>();
    int selectedItem;
    int spinner_id;
    String spinner_name;
    ArrayAdapter<String> stateAdaoter;
    public static  String value,radioV;
    int selectedId;
    int id;
    String selectedName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detials_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        groupRadio=findViewById(R.id.groupRadio);
        spinnerWorkList=findViewById(R.id.spinnerWorkList);
        ptRadio=findViewById(R.id.ptRadio);
        ftRadio=findViewById(R.id.ftRadio);
        groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedId=groupRadio.getCheckedRadioButtonId();
                radioV=String.valueOf(selectedId);
                switch (selectedId){
                    case R.id.ptRadio:
                        radioV="0";
                        break;
                    case R.id.ftRadio:
                        radioV="1";
                        break;
                }
            }
        });
        viewWorkDetails();
    }

    private void viewWorkDetails() {
        if (CommonUtils.isOnline(WorkDetailsViewActivity.this)) {
            //sessonManager.showProgress(WorkDetailsViewActivity.this);
            Call<WorkDetailsViewModel>call= ApiExecutor.getApiService(this)
                    .apiWorkDetailsView("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<WorkDetailsViewModel>() {
                @Override
                public void onResponse(Call<WorkDetailsViewModel> call, Response<WorkDetailsViewModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            /*Todo:- Radio*/
                            WorkDetailsViewModel workDetailsViewModel=response.body();
                            if (workDetailsViewModel.getData()!=null){
                                String radio=workDetailsViewModel.getData().getUser().getWorkType();
                                if (radio!=null&&radio.equalsIgnoreCase("0")){
                                    ptRadio.setChecked(true);
                                }else if (radio!=null&&radio.equalsIgnoreCase("1")){
                                    ftRadio.setChecked(true);
                                }
                            }
                            /*Todo:- Spinner selected value*/
                            locationList=workDetailsViewModel.getData().getLocations();
                            for (int i=0;i<locationList.size();i++){
                                id=locationList.get(i).getId();
                                value=locationList.get(i).getName();
                                Log.d("spinnerValue",value);
                            }
                            /*Todo:- Spinner*/
                            workLocationList=workDetailsViewModel.getData().getWorkLocations();

                            for (int i=0;i<workLocationList.size();i++){
                                spinner_id=workLocationList.get(i).getId();
                                spinner_name=workLocationList.get(i).getName();
                                spinner_id_list.add(spinner_id);
                                spinner_name_list.add(spinner_name);
                                if(id==spinner_id){
                                    selectedName= workLocationList.get(i).getName();
                                }


                            }
                        }
                        stateAdaoter = new ArrayAdapter<String>(WorkDetailsViewActivity.this, android.R.layout.simple_list_item_1, spinner_name_list);
                        spinnerWorkList.setAdapter(stateAdaoter);
                        spinnerWorkList.setOnItemSelectedListener(new ItemSelectedListener());

                        int spinnerPosition = stateAdaoter.getPosition(selectedName);
                        spinnerWorkList.setSelection(spinnerPosition);
                        /*if (value!=null){
                            spinnerWorkList.setSelection(((ArrayAdapter<String>)spinnerWorkList.getAdapter()).getPosition(value));
                        }*/

                    }
                }

                @Override
                public void onFailure(Call<WorkDetailsViewModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(WorkDetailsViewActivity.this, getString(R.string.please_check_network));
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
        if (radioV.length()==0){
            Toast.makeText(this, "Please select any option( Part Time / Full Time )", Toast.LENGTH_SHORT).show();
        }else {
            service();
        }
    }

    private void service() {
        if (CommonUtils.isOnline(WorkDetailsViewActivity.this)) {
            sessonManager.showProgress(WorkDetailsViewActivity.this);
            Call<WorkDetailsModel>call=ApiExecutor.getApiService(this)
                    .apiWorkDetails("Bearer " + sessonManager.getToken(),lll,radioV);
            call.enqueue(new Callback<WorkDetailsModel>() {
                @Override
                public void onResponse(Call<WorkDetailsModel> call, Response<WorkDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(WorkDetailsViewActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            spinner_name_list.clear();
                            viewWorkDetails();

                        }else {
                            Toast.makeText(WorkDetailsViewActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<WorkDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(WorkDetailsViewActivity.this, getString(R.string.please_check_network));
        }

    }
    private class ItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            lll.clear();
            selectedItem = workLocationList.get(position).getId();

            Log.d("resSpinner",""+selectedItem);
            lll.add(selectedItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}