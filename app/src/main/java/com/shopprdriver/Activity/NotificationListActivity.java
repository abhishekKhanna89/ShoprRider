package com.shopprdriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shopprdriver.Model.NotificationList.Datum;
import com.shopprdriver.Model.NotificationList.NotificationListModel;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class NotificationListActivity extends AppCompatActivity implements
        RecyclerView.OnScrollChangeListener{
    //SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv_notification;
    SessonManager sessonManager;
    List<Datum>notificationList=new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    int currentPage =1;
    int page;
    NotificationListAdapter notificationListAdapter;

    ImageView emptyPageGif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        //Log.d("token",sessonManager.getToken());
        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        rv_notification = (RecyclerView) findViewById(R.id.rv_notification);
        emptyPageGif=findViewById(R.id.emptyPageGif);
        Glide.with(this).load(R.drawable.no_result).into(emptyPageGif);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_notification.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_notification.getContext(),
                linearLayoutManager.getOrientation());
        rv_notification.addItemDecoration(dividerItemDecoration);
        rv_notification.setNestedScrollingEnabled(true);
        //swipeRefreshLayout.setOnRefreshListener(this);
        rv_notification.setOnScrollChangeListener(this);
      /*  swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                    }
                                });*/
        notificationList();

         notificationListAdapter=new NotificationListAdapter(NotificationListActivity.this,notificationList);
        rv_notification.setAdapter(notificationListAdapter);
        notificationListAdapter.notifyDataSetChanged();
    }
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void notificationList() {
        if (CommonUtils.isOnline(NotificationListActivity.this)) {
            sessonManager.showProgress(NotificationListActivity.this);
            Call<NotificationListModel>call= ApiExecutor.getApiService(this)
                    .apiNotificationList("Bearer " + sessonManager.getToken(),currentPage);
            call.enqueue(new Callback<NotificationListModel>() {
                @Override
                public void onResponse(Call<NotificationListModel> call, Response<NotificationListModel> response) {
// stopping swipe refresh
                    //swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            NotificationListModel notificationListModel=response.body();
                            if (notificationListModel.getData().getNotifications().getData()!=null){
                                if (notificationListModel.getData().getNotifications().getData().size()==0){
                                    emptyPageGif.setVisibility(View.VISIBLE);
                                    rv_notification.setVisibility(View.GONE);
                                }else {
                                    emptyPageGif.setVisibility(View.GONE);
                                    rv_notification.setVisibility(View.VISIBLE);
                                }
                                page=notificationListModel.getData().getNotifications().getLastPage();
                                notificationList.addAll(notificationListModel.getData().getNotifications().getData());
                                notificationListAdapter.notifyDataSetChanged();
                                if(notificationList.size()>0){
                                    currentPage= currentPage+1;
                                }
                            }
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(NotificationListActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(NotificationListActivity.this,"");
                                            Toast.makeText(NotificationListActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(NotificationListActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<NotificationListModel> call, Throwable t) {
// stopping swipe refresh
                    //swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(NotificationListActivity.this, getString(R.string.please_check_network));
        }


    }

    /*@Override
    public void onRefresh() {
        notificationList();
    }*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(rv_notification)) {
            //Calling the method getdata again
            if(currentPage<page){
                notificationList();
            }

        }
    }

    public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.Holder>{
        List<Datum>notificationList;
        Context context;
        public NotificationListAdapter(Context context,List<Datum>notificationList){
            this.context=context;
            this.notificationList=notificationList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_notification_list,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Datum datum=notificationList.get(position);
            if (datum.getTitle().isEmpty()){

            }else {
                holder.textTitle.setText(datum.getTitle());
            }
            if (datum.getDescription().isEmpty()){

            }else {
                holder.textDescription.setText(datum.getDescription());
            }
            if (datum.getCreatedAt().isEmpty()){

            }else {
                holder.textDate.setText(datum.getCreatedAt());
            }
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView textTitle,
                    textDate,textDescription;
            public Holder(@NonNull View itemView) {
                super(itemView);
                textTitle=(TextView)itemView.findViewById(R.id.textTitle);
                textDescription=(TextView)itemView.findViewById(R.id.textDescription);
                textDate=(TextView)itemView.findViewById(R.id.textDate);
            }
        }
    }
}