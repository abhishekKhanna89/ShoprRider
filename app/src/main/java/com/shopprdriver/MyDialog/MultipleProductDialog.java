package com.shopprdriver.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Adapter.MultipleProductAdapter;
import com.shopprdriver.Adapter.MultipleProductViewAdapter;
import com.shopprdriver.Model.ChatMessage.ProductItem;
import com.shopprdriver.R;

import java.util.List;

public class MultipleProductDialog {

    public void showDialog(Context context, List<ProductItem> productItemList) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.multiple_product_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgclosee = (ImageView) dialog.findViewById(R.id.imgclosee);
        RecyclerView rvMultipleProductsItem = (RecyclerView) dialog.findViewById(R.id.rvMultipleProductsItem);
        MultipleProductViewAdapter  multipleProductAdapter = new MultipleProductViewAdapter(context, productItemList );
        LinearLayoutManager linearLayoutManagerMyFavorite = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvMultipleProductsItem.setLayoutManager(linearLayoutManagerMyFavorite);
        rvMultipleProductsItem.setAdapter(multipleProductAdapter);

        imgclosee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
