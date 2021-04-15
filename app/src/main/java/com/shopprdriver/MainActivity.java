package com.shopprdriver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shopprdriver.Activity.LoginActivity;
import com.shopprdriver.Adapter.UserChatListAdapter;
import com.shopprdriver.Model.AvailableChat.AvailableChatModel;
import com.shopprdriver.Model.AvailableChat.Userchat;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.SendBird.utils.ToastUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

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



    SessonManager sessonManager;
    RecyclerView userChatListRecyclerView;
    List<Userchat> chatsListModelList;
    private LinearLayoutManager linearLayoutManager;
   // SwipeRefreshLayout swipeRefreshLayout;
    UserChatListAdapter userChatListAdapter;
    MenuItem register;
    Menu menu_change_language;
    //TextView TvNoOpenOrder;

    /*Todo:- Address*/
    Geocoder geocoder;
    List<Address> addresses;
    String address;

    ImageView emptyPageGif;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessonManager = new SessonManager(this);
        Log.d("token", sessonManager.getToken());
        /*Todo:- Get Address*/
        geocoder = new Geocoder(this, Locale.getDefault());

        userChatListRecyclerView = findViewById(R.id.userChatListRecyclerView);
        //swipeRefreshLayout = findViewById(R.id.SwipeRefresh);
        emptyPageGif=findViewById(R.id.emptyPageGif);
        Glide.with(this).load(R.drawable.no_result).into(emptyPageGif);
        //swipeRefreshLayout.setOnRefreshListener(this);
       // TvNoOpenOrder = findViewById(R.id.TvNoOpenOrder);

        linearLayoutManager = new LinearLayoutManager(this);
        userChatListRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userChatListRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        userChatListRecyclerView.addItemDecoration(dividerItemDecoration);
        userChatListRecyclerView.setNestedScrollingEnabled(true);
       /* swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

            }
        });*/
        viewUserChatList();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        checkPermissions();

    }

    private void viewUserChatList() {
        if (CommonUtils.isOnline(this)) {
            //sessonManager.showProgress(this);
            //Log.d("token",sessonManager.getToken());
            Call<AvailableChatModel> call = ApiExecutor.getApiService(this)
                    .apiUserAvailableChatList("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<AvailableChatModel>() {
                @Override
                public void onResponse(Call<AvailableChatModel> call, Response<AvailableChatModel> response) {

                    //sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            AvailableChatModel chatsListModel = response.body();
                            Gson gson=new Gson();
                            String Strd=gson.toJson(chatsListModel);
                            //Log.d("dhjhgdjh",Strd);
                            if (chatsListModel.getData().getUserchats().size()==0){
                                emptyPageGif.setVisibility(View.VISIBLE);
                                //swipeRefreshLayout.setVisibility(View.GONE);
                            }else {
                                emptyPageGif.setVisibility(View.GONE);
                                //swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }
                            chatsListModelList = chatsListModel.getData().getUserchats();
                            userChatListAdapter = new UserChatListAdapter(MainActivity.this, chatsListModelList);
                            userChatListRecyclerView.setAdapter(userChatListAdapter);
                            userChatListAdapter.notifyDataSetChanged();
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(MainActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(MainActivity.this,"");
                                            Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });
                                }
                            }

                        }
                    }
                    //swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<AvailableChatModel> call, Throwable t) {
                    //sessonManager.hideProgress();
                    //swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            CommonUtils.showToastInCenter(MainActivity.this, getString(R.string.please_check_network));
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        chatsListModelList.clear();
        viewUserChatList();
    }



    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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


   /* @Override
    public void onRefresh() {
        viewUserChatList();
    }*/
}
