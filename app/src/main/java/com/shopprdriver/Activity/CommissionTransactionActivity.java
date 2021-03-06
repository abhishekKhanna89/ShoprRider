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
import com.shopprdriver.Adapter.CommissionTransactionAdapter;
import com.shopprdriver.Model.CommissionTransactions.CommissionTransaction;
import com.shopprdriver.Model.CommissionTransactions.CommissionTransactionsModel;
import com.shopprdriver.Model.CommissionTransactions.Transaction;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommissionTransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessonManager sessonManager;
    TextView balanceText,deliveryChargeText;
    List<CommissionTransaction> historyList;
    String from_date,to_date;
    BottomSheetDialog bottomSheetDialog;
    int mYear,mMonth,mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_transaction);
        getSupportActionBar().setTitle("Commission");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);


        recyclerView=findViewById(R.id.recyclerView);
        balanceText=findViewById(R.id.balanceText);
        deliveryChargeText=findViewById(R.id.deliveryChargeText);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));




        viewData();
    }

    private void viewData() {
        if (CommonUtils.isOnline(CommissionTransactionActivity.this)) {
            sessonManager.showProgress(CommissionTransactionActivity.this);
            Call<CommissionTransactionsModel>call= ApiExecutor.getApiService(this)
                    .apiCommissionTransaction("Bearer "+sessonManager.getToken(),from_date,to_date);
            call.enqueue(new Callback<CommissionTransactionsModel>() {
                @Override
                public void onResponse(Call<CommissionTransactionsModel> call, Response<CommissionTransactionsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            CommissionTransactionsModel commissionTransactionsModel=response.body();
                            if (commissionTransactionsModel.getData()!=null){
                                balanceText.setText("\u20B9 "+commissionTransactionsModel.getData().getCommission());
                                deliveryChargeText.setText("\u20B9 "+commissionTransactionsModel.getData().getDelivery_charge());
                                historyList= commissionTransactionsModel.getData().getCommissionTransactions();
                                RecyclerAdapter recyclerAdapter=new RecyclerAdapter(CommissionTransactionActivity.this,historyList);
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommissionTransactionsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(CommissionTransactionActivity.this, getString(R.string.please_check_network));
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(CommissionTransactionActivity.this,
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(CommissionTransactionActivity.this,
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
        List<CommissionTransaction>historyList;
        Context context;
        public RecyclerAdapter(Context context,List<CommissionTransaction>historyList){
            this.historyList=historyList;
            this.context=context;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_recycler,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            CommissionTransaction history=historyList.get(position);
            holder.button.setText(history.getDate());
            List<Transaction>transactionList=history.getTransactions();
            holder.transactionListRecycler.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            CommissionTransactionAdapter commissionTransactionAdapter=new CommissionTransactionAdapter(context,transactionList);
            holder.transactionListRecycler.setAdapter(commissionTransactionAdapter);
            /*CreditAdapter creditAdapter=new CreditAdapter(context,transactionList);
            holder.transactionListRecycler.setAdapter(creditAdapter);*/

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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}