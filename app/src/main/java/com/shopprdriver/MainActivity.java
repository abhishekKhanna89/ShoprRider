package com.shopprdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.shopprdriver.Activity.AttendenceActivity;
import com.shopprdriver.Activity.ChatHistoryActivity;
import com.shopprdriver.Activity.CommissionTransactionActivity;
import com.shopprdriver.Activity.LoginActivity;
import com.shopprdriver.Activity.MyOrderActivity;
import com.shopprdriver.Activity.NotificationListActivity;
import com.shopprdriver.Activity.WalletTransactionActivity;
import com.shopprdriver.Adapter.UserChatListAdapter;
import com.shopprdriver.Model.AvailableChat.AvailableChatModel;
import com.shopprdriver.Model.AvailableChat.Userchat;
import com.shopprdriver.Model.CheckinCheckouSucess.CheckinCheckouSucessModel;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
import com.shopprdriver.RequestService.CheckInCheckOutRequest;
import com.shopprdriver.SendBird.utils.ToastUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.background_service.MyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

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
    MenuItem register;
    Menu menu_change_language;

    /*Todo:- Address*/
    Geocoder geocoder;
    List<Address> addresses;
    String address;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessonManager=new SessonManager(this);
        Log.d("token",sessonManager.getToken());
        /*Todo:- Get Address*/
        geocoder = new Geocoder(this, Locale.getDefault());

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


        checkPermissions();







    }
    private void viewUserChatList() {
        if (CommonUtils.isOnline(this)) {
            sessonManager.showProgress(this);
            //Log.d("token",sessonManager.getToken());
            Call<AvailableChatModel> call= ApiExecutor.getApiService(this)
                    .apiUserAvailableChatList("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<AvailableChatModel>() {
                @Override
                public void onResponse(Call<AvailableChatModel> call, Response<AvailableChatModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            AvailableChatModel chatsListModel=response.body();
                            if(chatsListModel.getData().getUserchats()!=null) {
                                chatsListModelList = chatsListModel.getData().getUserchats();
                                if (chatsListModel.getData().getType().equalsIgnoreCase("checkout")){
                                    //register=menu_change_language.findItem(R.id.actionCheckOut).setVisible(false);
                                    //register=menu_change_language.findItem(R.id.actionCheckIn).setVisible(true);
                                }else if (chatsListModel.getData().getType().equalsIgnoreCase("checkin")){
                                    //register=menu_change_language.findItem(R.id.actionCheckOut).setVisible(true);
                                    //register=menu_change_language.findItem(R.id.actionCheckIn).setVisible(false);
                                }

                                userChatListAdapter=new UserChatListAdapter(MainActivity.this,chatsListModelList);
                                userChatListRecyclerView.setAdapter(userChatListAdapter);
                                userChatListAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<AvailableChatModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(MainActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu_change_language=menu;
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

    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }

    public void chatHistory(MenuItem item) {
        startActivity(new Intent(this, ChatHistoryActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void checkout(MenuItem item) {
        if (CommonUtils.isOnline(this)) {
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            double latitude=Double.parseDouble(sessonManager.getLatitude());
            double longitude=Double.parseDouble(sessonManager.getLongitude());
            try {
                addresses = geocoder.getFromLocation(latitude,longitude, 1);
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if (address != null) {
                    //Toast.makeText(this, ""+address, Toast.LENGTH_SHORT).show();
                   // addressText.setText(address);
                }

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            } catch (IOException e) {
                e.printStackTrace();
            }

            CheckInCheckOutRequest checkInCheckOutRequest=new CheckInCheckOutRequest();
            checkInCheckOutRequest.setLat(sessonManager.getLatitude());
            checkInCheckOutRequest.setLang(sessonManager.getLongitude());
            checkInCheckOutRequest.setAddress(address);
            Call<CheckinCheckouSucessModel>call=ApiExecutor.getApiService(this)
                    .apiCheckIn("Bearer "+sessonManager.getToken(),checkInCheckOutRequest);
            call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                @Override
                public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                    pDialog.dismiss();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(MainActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                    pDialog.dismiss();
                }
            });

        }else {
            CommonUtils.showToastInCenter(MainActivity.this, getString(R.string.please_check_network));
        }


    }

    public void checkin(MenuItem item) {
        if (CommonUtils.isOnline(this)) {
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            double latitude=Double.parseDouble(sessonManager.getLatitude());
            double longitude=Double.parseDouble(sessonManager.getLongitude());
            try {
                addresses = geocoder.getFromLocation(latitude,longitude, 1);
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if (address != null) {
                    //Toast.makeText(this, ""+address, Toast.LENGTH_SHORT).show();
                    // addressText.setText(address);
                }

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            } catch (IOException e) {
                e.printStackTrace();
            }

            CheckInCheckOutRequest checkInCheckOutRequest=new CheckInCheckOutRequest();
            checkInCheckOutRequest.setLat(sessonManager.getLatitude());
            checkInCheckOutRequest.setLang(sessonManager.getLongitude());
            checkInCheckOutRequest.setAddress(address);
            Call<CheckinCheckouSucessModel>call=ApiExecutor.getApiService(this)
                    .apiCheckOut("Bearer "+sessonManager.getToken(),checkInCheckOutRequest);
            call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                @Override
                public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                    pDialog.dismiss();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(MainActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                    pDialog.dismiss();
                }
            });

        }else {
            CommonUtils.showToastInCenter(MainActivity.this, getString(R.string.please_check_network));
        }
    }

    public void attendance(MenuItem item) {
        startActivity(new Intent(MainActivity.this, AttendenceActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
