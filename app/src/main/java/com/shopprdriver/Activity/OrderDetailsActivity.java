package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopprdriver.Model.OrderDetails.Detail;
import com.shopprdriver.R;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {
    SessonManager sessonManager;
    RecyclerView rv_order_details;
    CardView cardOrderSummary;
    TextView orderIdText,totalAmountText,
            serviceChargeText,
            groundTotalText,
            totalPaidText;
    TextView emptyDeatils;
    List<Detail> detailList;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        String orderId=getIntent().getStringExtra("orderId");
        int position=getIntent().getIntExtra("position",0);
        String total=getIntent().getStringExtra("total");
        String service_charge=getIntent().getStringExtra("service_charge");
        /*Todo:- RecyclerView*/
        rv_order_details=findViewById(R.id.rv_order_details);
        rv_order_details.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        /*Todo:- CardView*/
        cardOrderSummary=findViewById(R.id.cardOrderSummary);
        /*Todo:- TextView*/
        emptyDeatils=findViewById(R.id.emptyDeatils);
        orderIdText=findViewById(R.id.orderIdText);
        totalAmountText=findViewById(R.id.totalAmountText);
        serviceChargeText=findViewById(R.id.serviceChargeText);
        groundTotalText=findViewById(R.id.groundTotalText);
        totalPaidText=findViewById(R.id.totalPaidText);


        if (MyOrderActivity.datumList.get(position).getDetails().size()==0){
            cardOrderSummary.setVisibility(View.GONE);
            emptyDeatils.setVisibility(View.VISIBLE);
        }else {
            cardOrderSummary.setVisibility(View.VISIBLE);
            emptyDeatils.setVisibility(View.GONE);

        }
        orderIdText.setText(orderId);
        totalAmountText.setText("₹ " +total);
        serviceChargeText.setText("₹ " +service_charge);
        double num1 = Double.parseDouble(total);
        double num2 = Double.parseDouble(service_charge);
        // add both number and store it to sum
        double sum = num1 + num2;
        groundTotalText.setText("₹ " +sum);
        totalPaidText.setText("₹ " +sum);
        OrderDetailsAdapter orderDetailsAdapter=new OrderDetailsAdapter(OrderDetailsActivity.this,MyOrderActivity.datumList.get(position).getDetails());
        rv_order_details.setAdapter(orderDetailsAdapter);
        orderDetailsAdapter.notifyDataSetChanged();
    }
    public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.Holder>{
        List<Detail> detailList;
        Context context;
        public OrderDetailsAdapter(Context context,List<Detail>detailList){
            this.context=context;
            this.detailList=detailList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return  new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_order_details,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Picasso.get().load(detailList.get(position).getFilePath()).into(holder.productImage);
            holder.nameProductText.setText(detailList.get(position).getMessage());
            holder.priceProductText.setText("\u20B9 "+detailList.get(position).getPrice());
            holder.quantityProductText.setText(detailList.get(position).getQuantity());
        }

        @Override
        public int getItemCount() {
            return detailList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView nameProductText,priceProductText,quantityProductText;
            public Holder(@NonNull View itemView) {
                super(itemView);
                productImage =  itemView.findViewById(R.id.productImage);
                nameProductText = (TextView) itemView.findViewById(R.id.nameProductText);
                priceProductText = itemView.findViewById(R.id.priceProductText);
                quantityProductText = itemView.findViewById(R.id.quantityProductText);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}