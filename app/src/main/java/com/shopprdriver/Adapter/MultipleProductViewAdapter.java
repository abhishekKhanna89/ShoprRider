package com.shopprdriver.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shopprdriver.Model.ChatMessage.ProductItem;
import com.shopprdriver.Model.DeleteProductModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MultipleProductViewAdapter extends RecyclerView.Adapter<MultipleProductViewAdapter.Holder> {
    List<ProductItem> multipleProductList;
    Context context;
    SessonManager sessonManager;

    public MultipleProductViewAdapter(Context context, List<ProductItem> multipleProductList) {
        this.context = context;
        this.multipleProductList = multipleProductList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.multiple_product_dialog_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        sessonManager = new SessonManager(context);
        ProductItem item = multipleProductList.get(position);

        if (item.getFile_path().length() == 0) {
        } else {
            Picasso.get().load(item.getFile_path()).into(holder.productImage);
        }
        holder.productMessage.setText(item.getMessage());
        holder.dateProduct.setText(item.getCreated_at());
        holder.pqText.setText(item.getQuantity() + " for " + "₹" + item.getPrice());
        //holder.productLayout.setVisibility(View.VISIBLE);

        if (item.getStatus().equalsIgnoreCase("accepted")) {
            holder.greenLayout.setVisibility(View.VISIBLE);
            holder.closeRedLayout.setVisibility(View.GONE);
            holder.rejectText.setVisibility(View.GONE);
            holder.cancelText.setVisibility(View.GONE);
            holder.acceptText.setVisibility(View.GONE);
        } else if (item.getStatus().equalsIgnoreCase("rejected")) {
            holder.closeRedLayout.setVisibility(View.VISIBLE);
            holder.greenLayout.setVisibility(View.GONE);
            holder.acceptText.setVisibility(View.GONE);
            holder.rejectText.setVisibility(View.GONE);
            holder.cancelText.setVisibility(View.GONE);
        } else if (item.getStatus().equalsIgnoreCase("cancelled")) {
            holder.closeRedLayout.setVisibility(View.VISIBLE);
            holder.greenLayout.setVisibility(View.GONE);
            holder.acceptText.setVisibility(View.GONE);
            holder.rejectText.setVisibility(View.GONE);
            holder.cancelText.setVisibility(View.GONE);
        }
        holder.tv_btn_cancel_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(context, "Message In Clicked", Toast.LENGTH_SHORT).show();

                Log.d("ksajdf", String.valueOf(item.getId()));
                deleteApiProduct(String.valueOf(item.getId()));
            }
        });


        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst = (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(item.getFile_path()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });

    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView pqText, dateProduct, productMessage;
        Button acceptText, rejectText, cancelText;
        LinearLayout greenLayout, closeRedLayout;
        Button tv_btn_cancel_in;
        ;

        public Holder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            pqText = itemView.findViewById(R.id.pqText);
            dateProduct = itemView.findViewById(R.id.dateProduct);
            productMessage = itemView.findViewById(R.id.productMessage);
            greenLayout = itemView.findViewById(R.id.greenLayout);
            closeRedLayout = itemView.findViewById(R.id.closeRedLayout);
            acceptText = itemView.findViewById(R.id.acceptText);
            rejectText = itemView.findViewById(R.id.rejectText);
            cancelText = itemView.findViewById(R.id.cancelText);
            tv_btn_cancel_in = itemView.findViewById(R.id.tv_btn_cancel_in);
        }
    }

    private void deleteApiProduct(String messageId) {
        Log.d("jzdfhkjdsg", sessonManager.getToken());
        Log.d("jhgvjhdf", messageId);
        if (CommonUtils.isOnline(context)) {
            sessonManager.showProgress(context);
            //Log.d("token",sessonManager.getToken());
            Call<DeleteProductModel> call = ApiExecutor.getApiService(context)
                    .apiDeleteProduct("Bearer " + sessonManager.getToken(), messageId);
            call.enqueue(new Callback<DeleteProductModel>() {
                @Override
                public void onResponse(Call<DeleteProductModel> call, Response<DeleteProductModel> response) {

                    Log.d("azcjhsadfc", new Gson().toJson(response.body()));
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("Success")) {
                            Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<DeleteProductModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        } else {
            //CommonUtils.showToastInCenter(context, getString(R.string.please_check_network));

            Toast.makeText(context, "please_check_network", Toast.LENGTH_SHORT).show();
        }
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
}
