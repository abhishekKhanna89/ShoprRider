package com.shopprdriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
import com.shopprdriver.Model.UserChatList.Userchat;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHistoryActivity extends AppCompatActivity {
    SessonManager sessonManager;
    RecyclerView userChatListRecyclerView;
    List<Userchat> chatsListModelList;
    private LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    UserChatHistoryAdapter userChatListAdapter;
    ImageView emptyPageGif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        userChatListRecyclerView=findViewById(R.id.userChatListRecyclerView);
        emptyPageGif=findViewById(R.id.emptyPageGif);
        Glide.with(this).load(R.drawable.no_result).into(emptyPageGif);
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
            Call<UserChatListModel> call= ApiExecutor.getApiService(this)
                    .apiUserChatList("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<UserChatListModel>() {
                @Override
                public void onResponse(Call<UserChatListModel> call, Response<UserChatListModel> response) {
                    sessonManager.hideProgress();
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            UserChatListModel chatsListModel=response.body();
                            if(chatsListModel.getData().getUserchats()!=null) {
                                if (chatsListModel.getData().getUserchats().size()==0){
                                    emptyPageGif.setVisibility(View.VISIBLE);
                                    userChatListRecyclerView.setVisibility(View.GONE);
                                    swipeRefreshLayout.setVisibility(View.GONE);
                                }else {
                                    emptyPageGif.setVisibility(View.GONE);
                                    userChatListRecyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                }
                                chatsListModelList = chatsListModel.getData().getUserchats();
                                userChatListAdapter=new UserChatHistoryAdapter(ChatHistoryActivity.this,chatsListModelList);
                                userChatListRecyclerView.setAdapter(userChatListAdapter);
                                userChatListAdapter.notifyDataSetChanged();

                            }
                        }else {
                            sessonManager.setToken("");
                            PrefUtils.setAppId(ChatHistoryActivity.this,"");
                            Toast.makeText(ChatHistoryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ChatHistoryActivity.this, LoginActivity.class));
                            finishAffinity();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserChatListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatHistoryActivity.this, getString(R.string.please_check_network));
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        viewUserChatList();
    }
    public class UserChatHistoryAdapter extends RecyclerView.Adapter<UserChatHistoryAdapter.Holder>{
        List<Userchat> chatsListModelList;
        Context context;
        public UserChatHistoryAdapter(Context context,List<Userchat> chatsListModelList){
            this.context=context;
            this.chatsListModelList=chatsListModelList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_chat_history,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Userchat userchat=chatsListModelList.get(position);
            Picasso.get().load(userchat.getImage()).into(holder.image_view_customer_head_shot);
            holder.text_view_customer_name.setText(userchat.getName());
            holder.text_view_customer_chat.setText(userchat.getChat());
            holder.dateText.setText(userchat.getDate());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra("chat_id", userchat.getId())
                            .putExtra("chat_status","0")
                            .putExtra("image",userchat.getImage())
                            .putExtra("name",userchat.getName())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return chatsListModelList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            CircleImageView image_view_customer_head_shot;
            TextView text_view_customer_name;
            TextView text_view_customer_chat,dateText;
            public Holder(@NonNull View itemView) {
                super(itemView);
                image_view_customer_head_shot=itemView.findViewById(R.id.image_view_customer_head_shot);
                text_view_customer_chat=itemView.findViewById(R.id.text_view_customer_chat);
                text_view_customer_name=itemView.findViewById(R.id.text_view_customer_name);
                dateText=itemView.findViewById(R.id.dateText);

            }
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