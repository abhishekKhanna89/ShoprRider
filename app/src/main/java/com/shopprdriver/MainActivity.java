package com.shopprdriver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shopprdriver.Activity.ChatActivity;
import com.shopprdriver.Activity.LoginActivity;
import com.shopprdriver.Adapter.UserChatListAdapter;
import com.shopprdriver.Model.Send.SendModel;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
import com.shopprdriver.Model.UserChatList.Userchat;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.http2.Header;
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
    private static String baseUrl="http://shoppr.avaskmcompany.xyz/api/shoppr/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessonManager=new SessonManager(this);

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


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            //Log.e("newToken", newToken);
            //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });
        viewUserChatList();






    }
    private void viewUserChatList() {
        if (CommonUtils.isOnline(this)) {
            sessonManager.showProgress(this);
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
    }
}