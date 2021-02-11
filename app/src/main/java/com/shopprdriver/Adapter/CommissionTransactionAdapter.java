package com.shopprdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Model.CommissionTransactions.CommissionTransaction;
import com.shopprdriver.Model.CommissionTransactions.Transaction;
import com.shopprdriver.R;

import java.util.List;

public class CommissionTransactionAdapter extends RecyclerView.Adapter<CommissionTransactionAdapter.Holder> {
    List<Transaction>transactionList;
    Context context;
    public CommissionTransactionAdapter(Context context,List<Transaction>transactionList){
        this.context=context;
        this.transactionList=transactionList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
        .inflate(R.layout.commission_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.t.setText("Order Id : "+transactionList.get(position).getRefid());
        holder.price.setText(String.valueOf(transactionList.get(position).getRiderCommission()));
        holder.bal.setText(transactionList.get(position).getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView t,price,bal;
        public Holder(@NonNull View itemView) {
            super(itemView);
            t=itemView.findViewById(R.id.t);
            price=itemView.findViewById(R.id.price);
            bal=itemView.findViewById(R.id.bal);
        }
    }
}
