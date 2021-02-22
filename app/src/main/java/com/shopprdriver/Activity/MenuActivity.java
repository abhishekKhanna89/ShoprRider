package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.Menu_Model;
import com.shopprdriver.R;

public class MenuActivity extends AppCompatActivity {
    RecyclerView menuRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuRecyclerView=findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        viewMenu();
    }

    private void viewMenu() {
        Menu_Model [] menuModelList=new Menu_Model[]{
                new Menu_Model("Notification"),
                new Menu_Model("Order"),
                new Menu_Model("Wallet Transaction"),
                new Menu_Model("Commission Transaction"),
                new Menu_Model("Chat History"),
                new Menu_Model("Check out"),
                new Menu_Model("Check in"),
                new Menu_Model("Attendance"),
                new Menu_Model("Profile"),
                new Menu_Model("Reviews"),

        };
        Menu_Adapter menu_adapter=new Menu_Adapter(MenuActivity.this,menuModelList);
        menuRecyclerView.setAdapter(menu_adapter);
        menu_adapter.notifyDataSetChanged();
    }
    public class Menu_Adapter extends RecyclerView.Adapter<Menu_Adapter.Holder>{
        Menu_Model[]menu_models;
        Context context;
        public Menu_Adapter(Context context,Menu_Model[]menu_models){
            this.context=context;
            this.menu_models=menu_models;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_menu,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Menu_Model menu_model=menu_models[position];
            holder.menu_title.setText(menu_model.getMenuName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position==0){
                        startActivity(new Intent(context, NotificationListActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else if (position==1){
                        startActivity(new Intent(context, MyOrderActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else if (position==2){
                        startActivity(new Intent(context, WalletTransactionActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else if (position==3){
                        startActivity(new Intent(context, CommissionTransactionActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else if (position==4){
                        startActivity(new Intent(context, ChatHistoryActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else if (position==7){
                        startActivity(new Intent(context, AttendenceActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return menu_models.length;
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView menu_title;
            public Holder(@NonNull View itemView) {
                super(itemView);
                menu_title=itemView.findViewById(R.id.menu_title);
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