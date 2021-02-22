package com.shopprdriver.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Activity.ChatActivity;
import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.AcceptChatModel;
import com.shopprdriver.Model.AvailableChat.AvailableChatModel;
import com.shopprdriver.Model.AvailableChat.Userchat;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.Holder> {
    List<Userchat>userchatList;
    Context context;
    SessonManager sessonManager;
    public UserChatListAdapter(Context context,List<Userchat>userchatList){
        this.context=context;
        this.userchatList=userchatList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_chats_list,null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Userchat userchat=userchatList.get(position);
        sessonManager=new SessonManager(context);
        Picasso.get().load(userchat.getImage()).into(holder.image_view_customer_head_shot);
        holder.text_view_customer_name.setText(userchat.getName());
        holder.text_view_customer_chat.setText(userchat.getDistance()+" km Away");

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(context)) {
                    sessonManager.showProgress(context);
                    //Log.d("token",sessonManager.getToken());
                    Call<AcceptChatModel> call= ApiExecutor.getApiService(context)
                            .apiAcceptChat("Bearer "+sessonManager.getToken(),userchat.getId());
                    call.enqueue(new Callback<AcceptChatModel>() {
                        @Override
                        public void onResponse(Call<AcceptChatModel> call, Response<AcceptChatModel> response) {
                            sessonManager.hideProgress();
                            if (response.body()!=null){
                                if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                                    AcceptChatModel chatsListModel=response.body();
                                    if(chatsListModel!=null) {
                                        Toast.makeText(context, chatsListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        userchatList.remove(position);
                                        notifyDataSetChanged();
                                        context.startActivity(new Intent(context, ChatActivity.class)
                                                .putExtra("id", userchat.getId())
                                                .putExtra("image",userchat.getImage())
                                                .putExtra("name",userchat.getName())
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                    }else {
                                        Toast.makeText(context, chatsListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AcceptChatModel> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
                }else {
                    CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                }
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(context)) {
                    sessonManager.showProgress(context);
                    Call<AcceptChatModel>call=ApiExecutor.getApiService(context)
                            .apiRejectChat("Bearer "+sessonManager.getToken(),userchat.getId());
                    call.enqueue(new Callback<AcceptChatModel>() {
                        @Override
                        public void onResponse(Call<AcceptChatModel> call, Response<AcceptChatModel> response) {
                            sessonManager.hideProgress();
                            if (response.body()!=null){
                                if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                                    AcceptChatModel chatsListModel=response.body();
                                    if(chatsListModel!=null) {
                                        Toast.makeText(context, chatsListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        userchatList.remove(position);
                                        notifyDataSetChanged();
                                    }else {
                                        Toast.makeText(context, chatsListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AcceptChatModel> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
                }else {
                    CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userchatList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CircleImageView image_view_customer_head_shot;
        TextView text_view_customer_name,text_view_customer_chat;
        Button btnAccept,btnReject;
        public Holder(@NonNull View itemView) {
            super(itemView);
            image_view_customer_head_shot=itemView.findViewById(R.id.image_view_customer_head_shot);
            text_view_customer_name=itemView.findViewById(R.id.text_view_customer_name);
            text_view_customer_chat=itemView.findViewById(R.id.text_view_customer_chat);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnReject=itemView.findViewById(R.id.btnReject);
        }
    }
}
