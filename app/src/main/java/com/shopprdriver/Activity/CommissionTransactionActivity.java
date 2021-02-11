package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shopprdriver.Adapter.CommissionTransactionAdapter;
import com.shopprdriver.Adapter.CreditAdapter;
import com.shopprdriver.Model.CommissionTransactions.CommissionTransaction;
import com.shopprdriver.Model.CommissionTransactions.CommissionTransactionsModel;
import com.shopprdriver.Model.CommissionTransactions.Transaction;
import com.shopprdriver.Model.WalletHistory.WalletHistoryModel;
import com.shopprdriver.Model.WalletHistory.WalletTransaction;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommissionTransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessonManager sessonManager;
    TextView balanceText;
    List<CommissionTransaction> historyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_transaction);

        sessonManager=new SessonManager(this);
        recyclerView=findViewById(R.id.recyclerView);
        balanceText=findViewById(R.id.balanceText);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        getSupportActionBar().setTitle("Commission");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewData();
    }

    private void viewData() {
        if (CommonUtils.isOnline(CommissionTransactionActivity.this)) {
            sessonManager.showProgress(CommissionTransactionActivity.this);
            Call<CommissionTransactionsModel>call= ApiExecutor.getApiService(this)
                    .apiCommissionTransaction("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<CommissionTransactionsModel>() {
                @Override
                public void onResponse(Call<CommissionTransactionsModel> call, Response<CommissionTransactionsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            CommissionTransactionsModel commissionTransactionsModel=response.body();
                            if (commissionTransactionsModel.getData()!=null){
                                balanceText.setText("\u20B9 "+commissionTransactionsModel.getData().getCommission());
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
}