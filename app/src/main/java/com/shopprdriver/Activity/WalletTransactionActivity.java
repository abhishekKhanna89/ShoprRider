package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shopprdriver.Adapter.CreditAdapter;
import com.shopprdriver.Model.WalletHistory.Transaction;
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

public class WalletTransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessonManager sessonManager;
    TextView balanceText;
    List<WalletTransaction> historyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transaction);
        sessonManager=new SessonManager(this);
        recyclerView=findViewById(R.id.recyclerView);
        balanceText=findViewById(R.id.balanceText);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        getSupportActionBar().setTitle("Wallet Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewData();
    }
    private void viewData() {
        if (CommonUtils.isOnline(WalletTransactionActivity.this)){
            sessonManager.showProgress(WalletTransactionActivity.this);
            Call<WalletHistoryModel> call= ApiExecutor.getApiService(WalletTransactionActivity.this)
                    .apiWalletHistory("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<WalletHistoryModel>() {
                @Override
                public void onResponse(Call<WalletHistoryModel> call, Response<WalletHistoryModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            WalletHistoryModel walletHistoryModel=response.body();
                            if (walletHistoryModel.getData()!=null){
                                balanceText.setText("\u20B9 "+walletHistoryModel.getData().getBalance());
                                historyList= walletHistoryModel.getData().getWalletTransactions();
                                RecyclerAdapter recyclerAdapter=new RecyclerAdapter(WalletTransactionActivity.this,historyList);
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<WalletHistoryModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(WalletTransactionActivity.this, getString(R.string.please_check_network));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent com.example.shoper.activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
        List<WalletTransaction>historyList;
        Context context;
        public RecyclerAdapter(Context context,List<WalletTransaction>historyList){
            this.historyList=historyList;
            this.context=context;
        }
        @NonNull
        @Override
        public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_recycler,null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
            WalletTransaction history=historyList.get(position);
            holder.button.setText(history.getDate());
            List<Transaction>transactionList=history.getTransactions();
            holder.transactionListRecycler.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            CreditAdapter creditAdapter=new CreditAdapter(context,transactionList);
            holder.transactionListRecycler.setAdapter(creditAdapter);

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