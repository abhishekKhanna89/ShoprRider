package com.shopprdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shopprdriver.Activity.CommissionTransactionActivity;
import com.shopprdriver.Activity.LoginActivity;
import com.shopprdriver.Activity.MyOrderActivity;
import com.shopprdriver.Activity.NotificationListActivity;
import com.shopprdriver.Activity.WalletTransactionActivity;
import com.shopprdriver.Adapter.UserChatListAdapter;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
import com.shopprdriver.Model.UserChatList.Userchat;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.background_service.MyService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {
    /*Todo:- Background Service*/
    private JobScheduler jobScheduler;
    private ComponentName componentName;
    private JobInfo jobInfo;

    SessonManager sessonManager;
    RecyclerView userChatListRecyclerView;
    List<Userchat>chatsListModelList;
    private LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    UserChatListAdapter userChatListAdapter;

    //private static String baseUrl="http://shoppr.avaskmcompany.xyz/api/shoppr/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessonManager=new SessonManager(this);
        Log.d("token",sessonManager.getToken());


        userChatListRecyclerView=findViewById(R.id.userChatListRecyclerView);
        swipeRefreshLayout = findViewById(R.id.SwipeRefresh);

        linearLayoutManager = new LinearLayoutManager(this);
        userChatListRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userChatListRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        userChatListRecyclerView.addItemDecoration(dividerItemDecoration);
        userChatListRecyclerView.setNestedScrollingEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewUserChatList();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }



        viewUserChatList();






    }
    private void viewUserChatList() {
        if (CommonUtils.isOnline(this)) {
            sessonManager.showProgress(this);
            //Log.d("token",sessonManager.getToken());
            Call<UserChatListModel> call= ApiExecutor.getApiService(this)
                    .apiUserChatList("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<UserChatListModel>() {
                @Override
                public void onResponse(Call<UserChatListModel> call, Response<UserChatListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            UserChatListModel chatsListModel=response.body();
                            if(chatsListModel.getData().getUserchats()!=null) {
                                chatsListModelList = chatsListModel.getData().getUserchats();
                                userChatListAdapter=new UserChatListAdapter(MainActivity.this,chatsListModelList);
                                userChatListRecyclerView.setAdapter(userChatListAdapter);
                                userChatListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserChatListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(MainActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_ot_menu, menu);
        return true;
    }

    public void logout(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessonManager.setToken("");
                        Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finishAffinity();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewUserChatList();
        StartBackgroundTask();
    }
    @SuppressLint("NewApi")
    public void StartBackgroundTask() {
        jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        componentName = new ComponentName(getApplicationContext(), MyService.class);
        jobInfo = new JobInfo.Builder(1, componentName)
                .setMinimumLatency(10000) //10 sec interval
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
        jobScheduler.schedule(jobInfo);
    }
    private void showGPSDisabledAlertToUser(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StartBackgroundTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StartBackgroundTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StartBackgroundTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartBackgroundTask();
    }

    public void notification(MenuItem item) {
        startActivity(new Intent(MainActivity.this, NotificationListActivity.class));
    }

    public void order(MenuItem item) {
        startActivity(new Intent(MainActivity.this, MyOrderActivity.class));
    }

    public void walletTransaction(MenuItem item) {
        startActivity(new Intent(MainActivity.this, WalletTransactionActivity.class));
    }

    public void commissionTransaction(MenuItem item) {
        startActivity(new Intent(MainActivity.this, CommissionTransactionActivity.class));
    }
}
