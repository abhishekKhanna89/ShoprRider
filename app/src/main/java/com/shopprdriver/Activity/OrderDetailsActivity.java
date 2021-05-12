package com.shopprdriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.shopprdriver.Model.Login.LoginModel;
import com.shopprdriver.Model.OrderDeatilsList.OrderDeatilsListModel;
import com.shopprdriver.Model.OrderDetails.Detail;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {
    SessonManager sessonManager;
    RecyclerView rv_order_details;
    CardView cardOrderSummary;
    TextView orderIdText, totalAmountText,
            serviceChargeText,
            groundTotalText,
            totalPaidText,walletAmountText,payment_text;
    TextView emptyDeatils;
    List<Detail> detailList;
    Button deliverBtn;
    //int show_deliver_button,orderId;
    //String status;
    int orderId;
    TextView TvRatingTitle, RatingTvMessage;
    RatingBar ratingbar;
    LinearLayout linearReview;
    List<com.shopprdriver.Model.OrderDeatilsList.Detail> orderList;

    String message,quantity;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        orderId = getIntent().getIntExtra("orderId", 0);
        Log.d("Token",sessonManager.getToken());
       /* int position=getIntent().getIntExtra("position",0);
        String total=getIntent().getStringExtra("total");
        String service_charge=getIntent().getStringExtra("service_charge");
        show_deliver_button=getIntent().getIntExtra("show_deliver_button",0);

        status=getIntent().getStringExtra("status");*/
        /*Todo:- RecyclerView*/
        rv_order_details = findViewById(R.id.rv_order_details);
        rv_order_details.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        /*Todo:- CardView*/
        cardOrderSummary = findViewById(R.id.cardOrderSummary);
        /*Todo:- TextView*/
        emptyDeatils = findViewById(R.id.emptyDeatils);
        orderIdText = findViewById(R.id.orderIdText);
        totalAmountText = findViewById(R.id.totalAmountText);
        serviceChargeText = findViewById(R.id.serviceChargeText);
        groundTotalText = findViewById(R.id.groundTotalText);
        totalPaidText = findViewById(R.id.totalPaidText);
        TvRatingTitle = findViewById(R.id.TvRatingTitle);
        walletAmountText=findViewById(R.id.walletAmountText);
        payment_text=findViewById(R.id.payment_text);
        ratingbar = findViewById(R.id.ratingBar);
        ratingbar.setIsIndicator(true);
        RatingTvMessage = findViewById(R.id.RatingTvMessage);
        linearReview = findViewById(R.id.linearReview);


        /*Todo:- Btn */
        deliverBtn = findViewById(R.id.deliverBtn);
        myOrderDetailsList();
        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(OrderDetailsActivity.this)) {
                    sessonManager.showProgress(OrderDetailsActivity.this);
                    Call<LoginModel> call = ApiExecutor.getApiService(OrderDetailsActivity.this)
                            .apiDeliverOrder("Bearer " + sessonManager.getToken(), orderId);
                    call.enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                            sessonManager.hideProgress();
                            if (response.body() != null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    Toast.makeText(OrderDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    myOrderDetailsList();
                                }else {
                                    if (response.body().getStatus().equalsIgnoreCase("failed")){
                                        if (response.body().getMessage().equalsIgnoreCase("logout")){
                                            AuthenticationUtils.deauthenticate(OrderDetailsActivity.this, isSuccess -> {
                                                if (getApplication() != null) {
                                                    sessonManager.setToken("");
                                                    PrefUtils.setAppId(OrderDetailsActivity.this,"");
                                                    Toast.makeText(OrderDetailsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                                                    finishAffinity();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter(OrderDetailsActivity.this, getString(R.string.please_check_network));
                }
                //Toast.makeText(OrderDetailsActivity.this, "Jai Mata Di", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void myOrderDetailsList() {
        if (CommonUtils.isOnline(OrderDetailsActivity.this)) {
            sessonManager.showProgress(OrderDetailsActivity.this);
            Call<OrderDeatilsListModel> call = ApiExecutor.getApiService(this)
                    .apiMyOderDetails("Bearer " + sessonManager.getToken(), orderId);
            call.enqueue(new Callback<OrderDeatilsListModel>() {
                @Override
                public void onResponse(Call<OrderDeatilsListModel> call, Response<OrderDeatilsListModel> response) {
                    sessonManager.hideProgress();
                    String resss = new Gson().toJson(response.body());
                    Log.d("dfjjhjk", resss);
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            OrderDeatilsListModel orderDeatilsListModel = response.body();
                            if (orderDeatilsListModel.getData().getOrder() != null) {
                                for (int i=0;i< orderDeatilsListModel.getData().getOrder().getReviews().size();i++){
                                    message = orderDeatilsListModel.getData().getOrder().getReviews().get(i).getMessage();
                                    quantity = orderDeatilsListModel.getData().getOrder().getReviews().get(i).getQuantity();
                                }


                                if (!String.valueOf(orderDeatilsListModel.getData().getOrder().getReviews()).equalsIgnoreCase("[]")) {
                                    linearReview.setVisibility(View.VISIBLE);
                                    float num = Float.parseFloat(quantity);
                                    TvRatingTitle.setText(quantity);
                                    RatingTvMessage.setText(message);
                                    ratingbar.setRating(num);
                                } else {
                                    linearReview.setVisibility(View.GONE);
                                }


                                if (orderDeatilsListModel.getData().getOrder().getShowDeliverButton() == 0) {
                                    deliverBtn.setVisibility(View.GONE);
                                } else {
                                    deliverBtn.setVisibility(View.VISIBLE);
                                }
                                payment_text.setText(orderDeatilsListModel.getData().getPayment_text());
                                orderIdText.setText(orderDeatilsListModel.getData().getOrder().getRefid());
                                totalAmountText.setText("₹ " + orderDeatilsListModel.getData().getOrder().getTotal());
                                serviceChargeText.setText("₹ " + orderDeatilsListModel.getData().getOrder().getServiceCharge());
                                double num1 = Double.parseDouble(orderDeatilsListModel.getData().getOrder().getTotal());
                                double num2 = Double.parseDouble(orderDeatilsListModel.getData().getOrder().getServiceCharge());
                                // add both number and store it to sum
                                double sum = num1 + num2;
                                groundTotalText.setText("₹ " + sum);
                                totalPaidText.setText("₹ " + sum);
                                walletAmountText.setText("₹ "+orderDeatilsListModel.getData().getOrder().getBalanceUsed());
                                orderList = orderDeatilsListModel.getData().getOrder().getDetails();
                                OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderList);
                                rv_order_details.setAdapter(orderDetailsAdapter);
                            }
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(OrderDetailsActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(OrderDetailsActivity.this,"");
                                            Toast.makeText(OrderDetailsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });

                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrderDeatilsListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
           /* Call<OrderDetailsModel> call = ApiExecutor.getApiService(this)
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
            });*/
        }
    }

    public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.Holder> {
        List<com.shopprdriver.Model.OrderDeatilsList.Detail> detailList;
        Context context;

        public OrderDetailsAdapter(Context context, List<com.shopprdriver.Model.OrderDeatilsList.Detail> detailList) {
            this.context = context;
            this.detailList = detailList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_order_details, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Picasso.get().load(detailList.get(position).getFilePath()).into(holder.productImage);
            holder.nameProductText.setText(detailList.get(position).getMessage());
            holder.priceProductText.setText("\u20B9 " + detailList.get(position).getPrice());
            holder.quantityProductText.setText(detailList.get(position).getQuantity());
        }

        @Override
        public int getItemCount() {
            return detailList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView nameProductText, priceProductText, quantityProductText;

            public Holder(@NonNull View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                nameProductText = (TextView) itemView.findViewById(R.id.nameProductText);
                priceProductText = itemView.findViewById(R.id.priceProductText);
                quantityProductText = itemView.findViewById(R.id.quantityProductText);
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