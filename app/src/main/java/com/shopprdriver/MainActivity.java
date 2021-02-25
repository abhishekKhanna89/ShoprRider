package com.shopprdriver;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shopprdriver.Adapter.UserChatListAdapter;
import com.shopprdriver.Model.AvailableChat.AvailableChatModel;
import com.shopprdriver.Model.AvailableChat.Userchat;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {

    SessonManager sessonManager;
    RecyclerView userChatListRecyclerView;
    List<Userchat>chatsListModelList;
    private LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    UserChatListAdapter userChatListAdapter;
    @SuppressLint("WrongConstant")
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





        viewUserChatList();









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
    protected void onRestart() {
        super.onRestart();
        viewUserChatList();

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





}
