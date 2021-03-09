package com.shopprdriver.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shopprdriver.Model.TravelingDetails.TravelingDetailsModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelingDetailsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessonManager sessonManager;
    TextView balanceText,deliveryChargeText;
    List<TravelingDetailsModel.CommissionTransaction> travelingDetailsModelList;
    String from_date,to_date;
    BottomSheetDialog bottomSheetDialog;
    int mYear,mMonth,mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessonManager=new SessonManager(this);


        recyclerView=findViewById(R.id.recyclerView);
        balanceText=findViewById(R.id.balanceText);
        deliveryChargeText=findViewById(R.id.deliveryChargeText);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));




        viewData();

    }
    private void viewData() {
        if (CommonUtils.isOnline(TravelingDetailsActivity.this)) {
            sessonManager.showProgress(TravelingDetailsActivity.this);
            Call<TravelingDetailsModel>call= ApiExecutor.getApiService(this)
                    .apiTravelingDetails("Bearer "+sessonManager.getToken(),from_date,to_date);
            call.enqueue(new Callback<TravelingDetailsModel>() {
                @Override
                public void onResponse(Call<TravelingDetailsModel> call, Response<TravelingDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            TravelingDetailsModel commissionTransactionsModel=response.body();
                            if (commissionTransactionsModel.getData()!=null){
                                balanceText.setText("\u20B9 "+commissionTransactionsModel.getData().getCommission());
                                deliveryChargeText.setText(commissionTransactionsModel.getData().getTotalKm()+" Km");
                                travelingDetailsModelList= commissionTransactionsModel.getData().getCommissionTransactions();
                                RecyclerAdapter recyclerAdapter=new RecyclerAdapter(TravelingDetailsActivity.this,travelingDetailsModelList);
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<TravelingDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(TravelingDetailsActivity.this, getString(R.string.please_check_network));
        }

    }
    public void filterCommission(View view) {
        bottomSheetDialog=new BottomSheetDialog(this,R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.layout_filter_commission,null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        TextView fromDateText=bottomSheetDialog.findViewById(R.id.fromDateText);
        TextView toDateText=bottomSheetDialog.findViewById(R.id.toDateText);
        TextView resetDateText=bottomSheetDialog.findViewById(R.id.resetDateText);
        Button applyBtn=bottomSheetDialog.findViewById(R.id.applyBtn);
        fromDateText.setText(sessonManager.getFromDate());
        toDateText.setText(sessonManager.getToDate());
        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TravelingDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                fromDateText.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TravelingDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                toDateText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        resetDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessonManager.setFromDate("");
                sessonManager.setToDate("");
                viewData();
                bottomSheetDialog.dismiss();
            }
        });
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from_date=fromDateText.getText().toString();
                to_date=toDateText.getText().toString();
                sessonManager.setFromDate(from_date);
                sessonManager.setToDate(to_date);
                viewData();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();

    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
        List<TravelingDetailsModel.CommissionTransaction>historyList;
        Context context;
        public RecyclerAdapter(Context context,List<TravelingDetailsModel.CommissionTransaction>historyList){
            this.historyList=historyList;
            this.context=context;
        }
        @NonNull
        @Override
        public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_recycler,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            TravelingDetailsModel.CommissionTransaction history=historyList.get(position);
            holder.button.setText(history.getDate());
            List<TravelingDetailsModel.Transaction>transactionList=history.getTransactions();
            holder.transactionListRecycler.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            CommissionTransactionAdapter commissionTransactionAdapter=new CommissionTransactionAdapter(context,transactionList);
            holder.transactionListRecycler.setAdapter(commissionTransactionAdapter);


        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            Button button;
            RecyclerView transactionListRecycler;
            public Holder(@NonNull View itemView) {
                super(itemView);
                button=itemView.findViewById(R.id.button);
                transactionListRecycler=itemView.findViewById(R.id.transactionListRecycler);
            }
        }
    }
    public  class CommissionTransactionAdapter extends RecyclerView.Adapter<CommissionTransactionAdapter.Holder>{
        List<TravelingDetailsModel.Transaction>transactionList;
        Context context;
        public CommissionTransactionAdapter(Context context,List<TravelingDetailsModel.Transaction>transactionList){
            this.context=context;
            this.transactionList=transactionList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_traveling_details,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.t.setText("Km Travel "+transactionList.get(position).getKm());
            holder.price.setText(String.valueOf("\u20B9 "+transactionList.get(position).getRiderCommission()));
            holder.timeT.setText(transactionList.get(position).getTime());
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView t,price,bal,timeT;
            public Holder(@NonNull View itemView) {
                super(itemView);
                t=itemView.findViewById(R.id.t);
                price=itemView.findViewById(R.id.price);
                bal=itemView.findViewById(R.id.bal);
                timeT=itemView.findViewById(R.id.timeT);
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