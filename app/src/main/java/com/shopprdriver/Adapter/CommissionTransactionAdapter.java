package com.shopprdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        int x = transactionList.get(position).getRiderCommission();
        int y = transactionList.get(position).getRider_delivery_charge();
        int sum = x + y;
        holder.price.setText(String.valueOf("\u20B9 "+sum));
        holder.bal.setText("("+"\u20B9 "+x+" + "+"\u20B9 "+y+")");
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
