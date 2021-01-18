package com.shopprdriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Activity.ChatActivity;
import com.shopprdriver.Model.UserChatList.Userchat;
import com.shopprdriver.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.Holder> {
    List<Userchat>userchatList;
    Context context;
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
        Picasso.get().load(userchat.getImage()).into(holder.image_view_customer_head_shot);
        holder.text_view_customer_name.setText(userchat.getName());
        holder.text_view_customer_chat.setText(userchat.getChat());
        holder.text_view_date.setText(userchat.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("id", userchat.getId())
                        .putExtra("image",userchat.getImage())
                        .putExtra("name",userchat.getName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userchatList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CircleImageView image_view_customer_head_shot;
        TextView text_view_customer_name,text_view_customer_chat,
                text_view_date;
        public Holder(@NonNull View itemView) {
            super(itemView);
            image_view_customer_head_shot=itemView.findViewById(R.id.image_view_customer_head_shot);
            text_view_customer_name=itemView.findViewById(R.id.text_view_customer_name);
            text_view_customer_chat=itemView.findViewById(R.id.text_view_customer_chat);
            text_view_date=itemView.findViewById(R.id.text_view_date);
        }
    }
}
