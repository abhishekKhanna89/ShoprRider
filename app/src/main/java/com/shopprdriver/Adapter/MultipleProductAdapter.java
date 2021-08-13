package com.shopprdriver.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shopprdriver.Model.MultipleProduct;
import com.shopprdriver.MyCallBack.OnSelectImageClick;
import com.shopprdriver.R;
import com.shopprdriver.util.NotificationUtils;

import java.util.List;

public class MultipleProductAdapter extends RecyclerView.Adapter<MultipleProductAdapter.Holder> {
    List<MultipleProduct> multipleProductList;
    Context context;
    OnSelectImageClick onSelectImageClick;

    public MultipleProductAdapter(Context context, List<MultipleProduct> multipleProductList, OnSelectImageClick onSelectImageClick) {
        this.context = context;
        this.multipleProductList = multipleProductList;
        this.onSelectImageClick = onSelectImageClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.multiple_product_list_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        MultipleProduct item = multipleProductList.get(position);
        Glide.with(context).load(item.getProductImage()).apply(NotificationUtils.getRequestOptions(context)).into(holder.imgProduct);
        holder.editName.setText(item.getProductName());
        holder.editPrice.setText(item.getProductPrice());
        holder.editQuantity.setText(String.valueOf(item.getProductQty()));
        if (item.getProductName().equalsIgnoreCase("Select")) {
            holder.llAddMore.setVisibility(View.VISIBLE);
            holder.llProductDesc.setVisibility(View.GONE);
        } else {
            holder.llAddMore.setVisibility(View.GONE);
            holder.llProductDesc.setVisibility(View.VISIBLE);
        }
        try {
            editDataFields(holder, position);
        } catch (Exception e) {
            Log.e("TAG", "onBindViewHolder: " + e.getMessage());
        }
        if (position == multipleProductList.size() - 2) {
            holder.editName.requestFocus();
        }
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllFieldFilled(position)) {
                    multipleProductList.get(position).setSaved(true);
                    showDeleteBtn(holder);
                }
            }
        });

        holder.llAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick.onAddMoreClicked();
            }
        });

        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleProductList.remove(position);
                notifyItemRangeRemoved(position, multipleProductList.size());
            }
        });

        if (item.isSaved()) {
            showDeleteBtn(holder);
        } else {
            showSaveBtn(holder);
        }
    }

    private boolean isAllFieldFilled(int position) {
        if (multipleProductList.get(position).getProductName().equalsIgnoreCase("")) {
            Toast.makeText(context, "Please enter Product Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (multipleProductList.get(position).getProductImage().equalsIgnoreCase("")) {
            Toast.makeText(context, "Please select Product Image.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (multipleProductList.get(position).getProductPrice().equalsIgnoreCase("")) {
            Toast.makeText(context, "Please enter Product Price.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (multipleProductList.get(position).getProductQty().equalsIgnoreCase("")) {
            Toast.makeText(context, "Please enter Product Quantity.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return multipleProductList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgClose;
        TextView editName, editPrice, editQuantity;
        Button btnSave, btnDelete;
        LinearLayoutCompat llAddMore, llProductDesc;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgClose = itemView.findViewById(R.id.imgClose);
            editName = itemView.findViewById(R.id.editName);
            editPrice = itemView.findViewById(R.id.editPrice);
            editQuantity = itemView.findViewById(R.id.editQuantity);
            btnSave = itemView.findViewById(R.id.submitBtn);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            llAddMore = itemView.findViewById(R.id.llAddMore);
            llProductDesc = itemView.findViewById(R.id.llProductDesc);
        }
    }

    private void editDataFields(Holder holder, int position) {
        holder.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                multipleProductList.get(position).setProductName(editable.toString());
                multipleProductList.get(position).setSaved(false);
                showSaveBtn(holder);
            }
        });
        holder.editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                multipleProductList.get(position).setProductPrice(editable.toString());
                multipleProductList.get(position).setSaved(false);
                showSaveBtn(holder);
            }
        });
        holder.editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                multipleProductList.get(position).setProductQty(editable.toString());
                multipleProductList.get(position).setSaved(false);
                showSaveBtn(holder);
            }
        });

        holder.imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick.onSelectImageClick(position);
            }
        });
    }

    private void showSaveBtn(Holder holder) {
        holder.btnSave.setVisibility(View.VISIBLE);
        holder.btnDelete.setVisibility(View.GONE);
    }

    private void showDeleteBtn(Holder holder) {
        holder.btnSave.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.VISIBLE);
    }

}
