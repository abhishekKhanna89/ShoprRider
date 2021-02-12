package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopprdriver.Model.OrderDetails.Datum;
import com.shopprdriver.Model.OrderDetails.OrderDetailsModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyOrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerView.OnScrollChangeListener {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv_myOrder;
    SessonManager sessonManager;
    public static List<Datum> datumList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    int currentPage = 1;
    int page;
    MyOrderListAdapter myOrderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        Log.d("token", sessonManager.getToken());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        rv_myOrder = (RecyclerView) findViewById(R.id.rv_myOrder);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_myOrder.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_myOrder.getContext(),
                linearLayoutManager.getOrientation());
        rv_myOrder.addItemDecoration(dividerItemDecoration);
        rv_myOrder.setNestedScrollingEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        rv_myOrder.setOnScrollChangeListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                orderList();
            }
        });
        myOrderListAdapter = new MyOrderListAdapter(this, datumList);
        rv_myOrder.setAdapter(myOrderListAdapter);
        myOrderListAdapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void orderList() {
        swipeRefreshLayout.setRefreshing(true);
        if (CommonUtils.isOnline(MyOrderActivity.this)) {
            sessonManager.showProgress(MyOrderActivity.this);
            Call<OrderDetailsModel> call = ApiExecutor.getApiService(this)
                    .apiMyOrderDetails("Bearer " + sessonManager.getToken(), currentPage);
            call.enqueue(new Callback<OrderDetailsModel>() {
                @Override
                public void onResponse(Call<OrderDetailsModel> call, Response<OrderDetailsModel> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            OrderDetailsModel orderDetailsModel = response.body();
                            if (orderDetailsModel.getData().getOrders().getData() != null) {
                                page = orderDetailsModel.getData().getOrders().getLastPage();
                                datumList.addAll(orderDetailsModel.getData().getOrders().getData());
                                myOrderListAdapter.notifyDataSetChanged();
                                if (datumList.size() > 0) {
                                    currentPage = currentPage + 1;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrderDetailsModel> call, Throwable t) {

                }
            });
        }


    }

    @Override
    public void onRefresh() {
        orderList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(rv_myOrder)) {
            //Calling the method getdata again
            if (currentPage < page) {
                orderList();
            }

        }
    }

    public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.Holder> {
        List<Datum> datumList;
        Context context;

        public MyOrderListAdapter(Context context, List<Datum> datumList) {
            this.context = context;
            this.datumList = datumList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_my_order, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Datum datum = datumList.get(position);

            Gson gson=new Gson();
            String res=gson.toJson(datum);
            //Log.d("ress",res);
            if (datum.getDetails().size() == 0) {
                Picasso.get().load(R.drawable.pin_logo).into(holder.itemImage);
            } else {
                for (int i=0;i<datumList.get(position).getDetails().size();i++){
                    Picasso.get().load(datumList.get(position).getDetails().get(i).getFilePath()).into(holder.itemImage);
                }

            }

            holder.rfIdText.setText("Order Id :" + datum.getRefid());
            holder.itemDate.setText(datum.getCreatedAt());
            holder.totalText.setText("Total :" + datum.getTotal());
            holder.serviceChargeText.setText("Service Charge :" + datum.getServiceCharge());

            if (datum.getStatus().equalsIgnoreCase("Confirmed")) {
                holder.statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                holder.statusText.setText("Status :" + datum.getStatus());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("orderId", datum.getId())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });

        }

        @Override
        public int getItemCount() {
            return datumList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            CircleImageView itemImage;
            TextView rfIdText, itemDate,
                    totalText, serviceChargeText,
                    statusText;

            public Holder(@NonNull View itemView) {
                super(itemView);
                itemImage = itemView.findViewById(R.id.itemImage);
                rfIdText = itemView.findViewById(R.id.rfIdText);
                itemDate = itemView.findViewById(R.id.itemDate);
                totalText = itemView.findViewById(R.id.totalText);
                serviceChargeText = itemView.findViewById(R.id.serviceChargeText);
                statusText = itemView.findViewById(R.id.statusText);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        orderList();
    }
}