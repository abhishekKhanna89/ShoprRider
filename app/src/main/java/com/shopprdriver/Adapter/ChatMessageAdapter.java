 package com.shopprdriver.Adapter;

 import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
 import android.widget.Button;
 import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Model.AcceptModel;
import com.shopprdriver.Model.CancelModel;
import com.shopprdriver.Model.ChatMessage.Chat;
import com.shopprdriver.Model.RatingsModel;
import com.shopprdriver.Model.RejectedModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.RatingsRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;


 public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
     List<Chat>chatList;
    Context context;
    private int SELF = 1;
    View itemView;
    SessonManager sessonManager;

    public ChatMessageAdapter(Context context, List<Chat>chatList){
        this.context=context;
        this.chatList=chatList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_layout, parent, false);
        } else{
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_layout, parent, false);
        }



        //returing the view
        return new Holder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
       Chat chat=chatList.get(position);
       sessonManager=new SessonManager(context);


       if (chat.getType().equalsIgnoreCase("image")){
           Picasso.get().load(chat.getFilePath()).into(holder.image);
           //Glide.with(context).load(chat.getFilePath()).into(holder.image);
           holder.imageText.setText(chat.getMessage());
           holder.dateImage.setText(chat.getCreatedAt());

       }else {
           holder.imageLayout.setVisibility(View.GONE);

       }
       if (chat.getType().equalsIgnoreCase("text")){
           holder.message_body.setText(chat.getMessage());
           holder.dateText.setText(chat.getCreatedAt());
       }else {
           holder.textLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("product")){
           if (chat.getFilePath().length()==0){
           }else {
               Picasso.get().load(chat.getFilePath()).into(holder.productImage);
           }
           holder.productMessage.setText(chat.getMessage());
           holder.dateProduct.setText(chat.getCreatedAt());
           holder.pqText.setText(chat.getQuantity()+" / "+"₹"+chat.getPrice());
       }else {
           holder.productLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("rating")){
           holder.ratingsMessage.setText(chat.getMessage());
           holder.dateRating.setText(chat.getCreatedAt());

           holder.ratingBar.setRating(Float.parseFloat(chat.getQuantity()));
       }else {
           holder.ratingLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("audio")){
           holder.voicePlayerView.setAudio(chat.getFilePath());
       }else {
           holder.voicePlayerView.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("add-money")){
           holder.addMoneyMessage.setText(chat.getMessage());
           holder.addMoneyDate.setText(chat.getCreatedAt());
           holder.addMoneyLayout.setVisibility(View.VISIBLE);
       }
       if (chat.getType().equalsIgnoreCase("recharge")){
           holder.rechargeTypeMessage.setText(chat.getMessage());
           holder.rechargeTypeDate.setText(chat.getCreatedAt());
           holder.rechargeTypeLayout.setVisibility(View.VISIBLE);
       }

       /*Todo:- Type Paid*/
        if (chat.getType().equalsIgnoreCase("paid")){
            holder.paymentReceiveLayout.setVisibility(View.VISIBLE);
            holder.paymentReceiveMsg.setText(chat.getMessage());
            holder.paymentReceiveDateText.setText(chat.getCreatedAt());
        }

       if (chat.getType().equalsIgnoreCase("address")){
           String lat =chat.getLat();
           String lon =chat.getLang();
           String url ="https://maps.googleapis.com/maps/api/staticmap?";
           url+="&zoom=14";
           url+="&size=200x200";
           url+="&maptype=roadmap";
           url+="&markers=color:red%7Clabel:%7C"+lat+", "+lon;
           url+="&key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
           Picasso.get().load(url).into(holder.locationImage);
           holder.locationText.setText(chat.getMessage());
           holder.locationDate.setText(chat.getCreatedAt());
       }else {
           holder.mapLayout.setVisibility(View.GONE);
       }

        /*Todo:- Store Type*/
        if (chat.getType().equalsIgnoreCase("store")){
            holder.storeLocationText.setText(chat.getMessage());
            holder.storeLocationTextDate.setText(chat.getCreatedAt());
        }else {
            holder.storeLocationLayout.setVisibility(View.GONE);
        }


       /*Todo:- Visibility Concept*/
       if (chat.getStatus().equalsIgnoreCase("accepted")){
           holder.greenLayout.setVisibility(View.VISIBLE);
           holder.closeRedLayout.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.GONE);
           holder.acceptText.setVisibility(View.GONE);
           holder.ratingBar.setIsIndicator(true);
       }
       if (chat.getStatus().equalsIgnoreCase("rejected")){
           holder.closeRedLayout.setVisibility(View.VISIBLE);
           holder.greenLayout.setVisibility(View.GONE);
           holder.acceptText.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.GONE);
       }
       if (chat.getStatus().equalsIgnoreCase("cancelled")){
           holder.closeRedLayout.setVisibility(View.VISIBLE);
           holder.greenLayout.setVisibility(View.GONE);
           holder.acceptText.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.GONE);
       }


       holder.acceptText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   sessonManager.showProgress(context);
                   Call<AcceptModel>call= ApiExecutor.getApiService(context)
                           .apiAccept("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<AcceptModel>() {
                       @Override
                       public void onResponse(Call<AcceptModel> call, Response<AcceptModel> response) {
                           sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   AcceptModel acceptModel=response.body();
                                   if (acceptModel.getStatus().equalsIgnoreCase("success")){
                                       holder.greenLayout.setVisibility(View.VISIBLE);
                                       holder.closeRedLayout.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.VISIBLE);
                                       holder.acceptText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<AcceptModel> call, Throwable t) {
                           sessonManager.hideProgress();
                       }
                   });
               }else
               {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }

           }
       });
       holder.rejectText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   sessonManager.showProgress(context);
                   Call<RejectedModel>call=ApiExecutor.getApiService(context)
                           .apiRejected("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<RejectedModel>() {
                       @Override
                       public void onResponse(Call<RejectedModel> call, Response<RejectedModel> response) {
                           sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   RejectedModel rejectedModel=response.body();
                                   if (rejectedModel.getStatus().equalsIgnoreCase("success")){
                                       holder.closeRedLayout.setVisibility(View.VISIBLE);
                                       holder.greenLayout.setVisibility(View.GONE);
                                       holder.acceptText.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<RejectedModel> call, Throwable t) {
                           sessonManager.hideProgress();
                       }
                   });
               }else {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }

           }
       });
       holder.cancelText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   sessonManager.showProgress(context);
                   Call<CancelModel>call=ApiExecutor.getApiService(context)
                           .apiCancel("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<CancelModel>() {
                       @Override
                       public void onResponse(Call<CancelModel> call, Response<CancelModel> response) {
                           sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   CancelModel cancelModel=response.body();
                                   if (cancelModel.getStatus().equalsIgnoreCase("success")){
                                       holder.closeRedLayout.setVisibility(View.VISIBLE);
                                       holder.greenLayout.setVisibility(View.GONE);
                                       holder.acceptText.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<CancelModel> call, Throwable t) {
                           sessonManager.hideProgress();
                       }
                   });
               }else {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }
           }
       });

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float a=rating;
                int b;
                b=(int)a;
                String ratingValue=String.valueOf(b);
                if (CommonUtils.isOnline(context)) {
                    sessonManager.showProgress(context);
                    RatingsRequest ratingsRequest=new RatingsRequest();
                    ratingsRequest.setRatings(ratingValue);
                    Call<RatingsModel>call=ApiExecutor.getApiService(context)
                            .apiRatings("Bearer "+sessonManager.getToken(),chat.getId(),ratingsRequest);
                    call.enqueue(new Callback<RatingsModel>() {
                        @Override
                        public void onResponse(Call<RatingsModel> call, Response<RatingsModel> response) {
                            sessonManager.hideProgress();
                            //Log.d("response",response.body().getStatus());
                            if (response.body()!=null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    //ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(context,chatList);
                                    //chatMessageAdapter.refreshEvents(chatList);
                                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RatingsModel> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
                }else {
                    CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
                }
            }
        });
        /*Todo:-   Zoom Image*/
        holder.locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+chat.getLat()+","+chat.getLang());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context,R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst= (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context,R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst= (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });
        /*Todo:- Store Type*/
        holder.storeLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+chat.getLat()+","+chat.getLang());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        holder.setIsRecyclable(false);
    }



     @Override
    public int getItemCount() {
         if (chatList != null) {
             return chatList.size();

         } else return 0;
        //return chatList.size();
    }
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Chat message = chatList.get(position);

        //If its owner  id is  equals to the logged in user id
        if (message.getDirection()==1) {
            //Returning self
            return SELF;
        }
        //else returning position
        return position;
    }




     public class Holder extends RecyclerView.ViewHolder {
        /*Todo:- Location*/
        ImageView locationImage;
        TextView locationText,locationDate;
         ChatMessageView mapLayout;
        /*Todo:- Text*/
        TextView message_body,dateText;
         ChatMessageView textLayout;
        /*Todo:- Product*/
        ImageView productImage;
        TextView pqText,dateProduct,productMessage;
        Button acceptText,rejectText,cancelText;
        LinearLayout greenLayout,closeRedLayout;
         ChatMessageView productLayout;
        /*Todo:- Image*/
        ImageView image;
        TextView imageText,dateImage;
         ChatMessageView imageLayout;
        /*Todo:- Rating*/
        ChatMessageView ratingLayout;
        TextView ratingsMessage,dateRating;
        AppCompatRatingBar ratingBar;
        /*Todo:- Audio*/
        VoicePlayerView voicePlayerView;

        /*Todo:- Store Type*/
         ChatMessageView storeLocationLayout;
         TextView storeLocationText,storeLocationTextDate;
         /*Todo:- AddMoney Type*/
         ChatMessageView addMoneyLayout;
         TextView addMoneyMessage,addMoneyDate;
         /*Todo:- Recharge Type*/
         ChatMessageView rechargeTypeLayout;
         TextView rechargeTypeMessage,rechargeTypeDate;

         /*Tod:- Type Paid*/
         ChatMessageView paymentReceiveLayout;
         TextView paymentReceiveMsg,paymentReceiveDateText;
        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
            locationImage=itemView.findViewById(R.id.locationImage);
            locationText=itemView.findViewById(R.id.locationText);
            locationDate=itemView.findViewById(R.id.locationDate);
            mapLayout=itemView.findViewById(R.id.mapLayout);
            /*Todo:- Text*/
            message_body=itemView.findViewById(R.id.message_body);
            dateText=itemView.findViewById(R.id.dateText);
            textLayout=itemView.findViewById(R.id.textLayout);
            /*Todo:- Product*/
            productImage=itemView.findViewById(R.id.productImage);
            pqText=itemView.findViewById(R.id.pqText);
            dateProduct=itemView.findViewById(R.id.dateProduct);
            productMessage=itemView.findViewById(R.id.productMessage);
            productLayout=itemView.findViewById(R.id.productLayout);
            greenLayout=itemView.findViewById(R.id.greenLayout);
            closeRedLayout=itemView.findViewById(R.id.closeRedLayout);
            acceptText=itemView.findViewById(R.id.acceptText);
            rejectText=itemView.findViewById(R.id.rejectText);
            cancelText=itemView.findViewById(R.id.cancelText);
            /*Todo:- Image*/
            image=itemView.findViewById(R.id.Image);
            imageText=itemView.findViewById(R.id.Text);
            dateImage=itemView.findViewById(R.id.dateImage);
            imageLayout=itemView.findViewById(R.id.imageLayout);
            /*Todo:- Rating*/
            ratingLayout=itemView.findViewById(R.id.ratingLayout);
            ratingsMessage=itemView.findViewById(R.id.ratingsMessage);
            dateRating=itemView.findViewById(R.id.dateRating);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            /*Todo:- Audio*/
            voicePlayerView=itemView.findViewById(R.id.voicePlayerView);

            /*Todo:- Store Type*/
            storeLocationLayout=itemView.findViewById(R.id.storeLocationLayout);
            storeLocationText=itemView.findViewById(R.id.storeLocationText);
            storeLocationTextDate=itemView.findViewById(R.id.storeLocationTextDate);
            /*Todo:- AddMoney Type*/
            addMoneyLayout=(ChatMessageView) itemView.findViewById(R.id.addMoneyTypeLayout);
            addMoneyMessage=itemView.findViewById(R.id.addMoneyMessage);
            addMoneyDate=itemView.findViewById(R.id.addMoneyDate);

            /*Todo:- Recharge Type*/
            rechargeTypeLayout=(ChatMessageView)itemView.findViewById(R.id.rechargeTypeLayout);
            rechargeTypeMessage=itemView.findViewById(R.id.rechargeTypeMessage);
            rechargeTypeDate=itemView.findViewById(R.id.rechargeTypeDate);

            /*Todo:- Paid*/
            paymentReceiveLayout=itemView.findViewById(R.id.paymentReceiveLayout);
            paymentReceiveMsg=itemView.findViewById(R.id.paymentReceiveMsg);
            paymentReceiveDateText=itemView.findViewById(R.id.paymentReceiveDateText);

        }
     }
}
