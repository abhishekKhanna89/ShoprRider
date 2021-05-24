 package com.shopprdriver.Adapter;

 import android.app.Activity;
 import android.app.Dialog;
 import android.content.Context;
 import android.content.Intent;
 import android.net.Uri;
 import android.os.Build;
 import android.util.Log;
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

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.load.engine.DiskCacheStrategy;
 import com.bumptech.glide.request.RequestOptions;
 import com.shopprdriver.Activity.OrderDetailsActivity;
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
     private int SELF_DIRECTION=0;
     private int SELF_TEXT_IN = 1;
     private int SELF_IMAGE_IN = 3;
     private int SELF_TEXT_OUT = 2;
     private int SELF_IMAGE_OUT = 4;
    private int  SELF_PRODUCT_IN=5;
     private int  SELF_PRODUCT_OUT=6;
     private int  SELF_RATING_IN=7;
     private int  SELF_RATING_OUT=8;

     private int  SELF_AUDIO_IN=9;
     private int  SELF_AUDIO_OUT=10;

     private int  SELF_ADDMONEY_IN=11;
     private int  SELF_ADDMONEY_OUT=12;

     private int  SELF_RECHARGE_IN=13;
     private int  SELF_RECHARGE_OUT=14;

     private int  SELF_PAID_IN=15;
     private int  SELF_PAID_OUT=16;

     private int  SELF_ADDRESS_IN=17;
     private int  SELF_ADDRESS_OUT=18;

     private int  SELF_STORE_IN=19;
     private int  SELF_STORE_OUT=20;

     private int  SELF_ORDERCONFIRMED_IN=21;
     private int  SELF_ORDERCONFIRMED_OUT= 22;


      /*       rating
     audio
     add-money
             recharge
     paid
             address
     store
             order_confirmed*/
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



            Log.d("newcheck====", String.valueOf(viewType));

            if (viewType == SELF_TEXT_IN) {
                //Inflating the layout self
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_text_layout, parent, false);
            }else if(viewType == SELF_TEXT_OUT){

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_text_layout, parent, false);
            }
            // lk changes here
            else if(viewType == SELF_PRODUCT_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_product_layout, parent, false);
            }
            else if(viewType == SELF_PRODUCT_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_product_layout, parent, false);
            }
            else if(viewType == SELF_RATING_IN){

                Log.d("outrating==", String.valueOf(SELF_RATING_IN));
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_rating_layout, parent, false);
            }
            else if(viewType == SELF_RATING_OUT){
              Log.d("outrating==", String.valueOf(SELF_RATING_OUT));

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_rating_layout, parent, false);
            }
            else if(viewType == SELF_AUDIO_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_audio_layout, parent, false);
            }
            else if(viewType == SELF_AUDIO_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_audio_layout, parent, false);
            }
            else if(viewType == SELF_ADDMONEY_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_payment_layout, parent, false);
            }
            else if(viewType == SELF_ADDMONEY_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_payment_layout, parent, false);
            }
            else if(viewType == SELF_RECHARGE_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_recharge_layout, parent, false);
            }
            else if(viewType == SELF_RECHARGE_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_recharge_layout, parent, false);
            }

            else if(viewType == SELF_PAID_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_payment_layout, parent, false);
            }
            else if(viewType == SELF_PAID_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_payment_layout, parent, false);
            }

            else if(viewType == SELF_ADDRESS_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_location_layout, parent, false);
            }
            else if(viewType == SELF_ADDRESS_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_location_layout, parent, false);
            }
            else if(viewType == SELF_STORE_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_store_layout, parent, false);
            }
            else if(viewType == SELF_STORE_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_store_layout, parent, false);
            }
            else if(viewType == SELF_ORDERCONFIRMED_IN){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_orderconfirmed_layout, parent, false);
            }
            else if(viewType == SELF_ORDERCONFIRMED_OUT){


                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_orderconfirmed_layout, parent, false);
            }

            else  if (viewType == SELF_IMAGE_IN) {

                itemView = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.in_msg_image_layout, parent, false);
           }
            else  if (viewType == SELF_IMAGE_OUT)
            {

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.out_msg_image_layout, parent, false);
            }
            else
            {

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.in_msg_blank_layout, parent, false);
            }
//
//



        //returing the view
        return new Holder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
       Chat chat=chatList.get(position);
       sessonManager=new SessonManager(context);

       Log.d("chattypeforchecking==",chat.getType());


     if (chat.getType().equalsIgnoreCase("image")){

           Picasso.get().load(chat.getFilePath()).fit().centerCrop()
                   .placeholder(R.drawable.ic_launcher)
                   .into(holder.image);

           holder.imageText.setText(chat.getMessage());
           holder.dateImage.setText(chat.getCreatedAt());
           //holder.imageLayout.setVisibility(View.VISIBLE);

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

       }

       else if (chat.getType().equalsIgnoreCase("text")) {
             holder.message_body.setText(chat.getMessage());
             holder.dateText.setText(chat.getCreatedAt());
             //holder.textLayout.setVisibility(View.VISIBLE);
         }



       else if (chat.getType().equalsIgnoreCase("product")){
           if (chat.getFilePath().length()==0){
           }else {
               Picasso.get().load(chat.getFilePath()).into(holder.productImage);
           }
           holder.productMessage.setText(chat.getMessage());
           holder.dateProduct.setText(chat.getCreatedAt());
           holder.pqText.setText(chat.getQuantity()+" for "+"₹"+chat.getPrice());
           //holder.productLayout.setVisibility(View.VISIBLE);

         if (chat.getStatus().equalsIgnoreCase("accepted")){
             holder.greenLayout.setVisibility(View.VISIBLE);
             holder.closeRedLayout.setVisibility(View.GONE);
             holder.rejectText.setVisibility(View.GONE);
             holder.cancelText.setVisibility(View.GONE);
             holder.acceptText.setVisibility(View.GONE);
         }
         else if (chat.getStatus().equalsIgnoreCase("rejected")){
             holder.closeRedLayout.setVisibility(View.VISIBLE);
             holder.greenLayout.setVisibility(View.GONE);
             holder.acceptText.setVisibility(View.GONE);
             holder.rejectText.setVisibility(View.GONE);
             holder.cancelText.setVisibility(View.GONE);
         }
         else if (chat.getStatus().equalsIgnoreCase("cancelled")){
             holder.closeRedLayout.setVisibility(View.VISIBLE);
             holder.greenLayout.setVisibility(View.GONE);
             holder.acceptText.setVisibility(View.GONE);
             holder.rejectText.setVisibility(View.GONE);
             holder.cancelText.setVisibility(View.GONE);
         }

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

       }

       else if (chat.getType().equalsIgnoreCase("rating")){
           if (chat.getStatus().equalsIgnoreCase("accepted")){
               holder.ratingLayout.setVisibility(View.VISIBLE);
           }else {
               holder.ratingLayout.setVisibility(View.GONE);
           }
           holder.ratingsMessage.setText(chat.getMessage());
           holder.dateRating.setText(chat.getCreatedAt());

           holder.ratingBar.setRating(Float.parseFloat(chat.getQuantity()));
       }

       else if (chat.getType().equalsIgnoreCase("audio")){

           //holder.voicePlayerView.setVisibility(View.VISIBLE);
         //holder.voicePlayerView.setAudio(chat.getFilePath());
        /// holder.voicePlayerView.iscl
         Log.d("lakshmi===", String.valueOf(holder.voicePlayerView.getImgPlayClickListener()));
         holder.voicePlayerView.setAudio(chat.getFilePath());
         holder.dateText.setText(chat.getCreatedAt());
         holder.voicePlayerView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                // holder.voicePlayerView.setAudio(chat.getFilePath());


               //  holder.voicePlayerView.setImgPauseClickListener();
                 //holder.voicePlayerView.performClick();
             }
         });

       }

       else if (chat.getType().equalsIgnoreCase("add-money")){
           holder.addMoneyMessage.setText(chat.getMessage());
           holder.addMoneyDate.setText(chat.getCreatedAt());
           //holder.addMoneyLayout.setVisibility(View.VISIBLE);
       }
       else if (chat.getType().equalsIgnoreCase("recharge")){
           holder.rechargeTypeMessage.setText(chat.getMessage());
           holder.rechargeTypeDate.setText(chat.getCreatedAt());
           //holder.rechargeTypeLayout.setVisibility(View.VISIBLE);
       }

       //Todo:- Type Paid
        else if (chat.getType().equalsIgnoreCase("paid")){
            //holder.paymentReceiveLayout.setVisibility(View.VISIBLE);
            holder.paymentReceiveMsg.setText(chat.getMessage());
            holder.paymentReceiveDateText.setText(chat.getCreatedAt());
        }

       else if (chat.getType().equalsIgnoreCase("address")){
           String lat =chat.getLat();
           String lon =chat.getLang();
           String url ="https://maps.googleapis.com/maps/api/staticmap?";
           url+="&zoom=14";
           url+="&size=200x200";
           url+="&maptype=roadmap";
           url+="&markers=color:red%7Clabel:%7C"+lat+", "+lon;
           url+="&key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
           Picasso.get().load(url).into(holder.locationImage);
           String currentString = chat.getMessage();
           String[] parts = currentString.split("#");
           try {
               String a=parts[0];
               String b=parts[1];
               holder.location2Text.setText(b);
               holder.locationText.setText(a);
           }catch (Exception e){
               e.printStackTrace();
           }

           holder.locationDate.setText(chat.getCreatedAt());
           //holder.mapLayout.setVisibility(View.VISIBLE);

         holder.locationImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Uri gmmIntentUri = Uri.parse("geo:0,0?q="+chat.getLat()+","+chat.getLang());
                 Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                 mapIntent.setPackage("com.google.android.apps.maps");
                 context.startActivity(mapIntent);
             }
         });

       }


       // Todo:- Store Type
        else if (chat.getType().equalsIgnoreCase("store")){
            holder.storeLocationText.setText(chat.getMessage());
            holder.storeLocationTextDate.setText(chat.getCreatedAt());
           //holder.storeLocationLayout.setVisibility(View.VISIBLE);

         holder.storeLocationLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Uri gmmIntentUri = Uri.parse("geo:0,0?q="+chat.getLat()+","+chat.getLang());
                 Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                 mapIntent.setPackage("com.google.android.apps.maps");
                 context.startActivity(mapIntent);
             }
         });
        }


        else if (chat.getType().equalsIgnoreCase("order_confirmed")){
            //holder.orderConfirmLayout.setVisibility(View.VISIBLE);
            holder.orderConfirmMessage.setText(chat.getMessage());
            holder.orderConfirmDate.setText(chat.getCreatedAt());
            holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("orderId",chat.getOrder_id())
                            .putExtra("position",position));
                }
            });
        }

      // Todo:- Visibility Concept



     /*  holder.acceptText.setOnClickListener(new View.OnClickListener() {
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
        //Todo:-   Zoom Image





       // Todo:- Store Type
        */
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
        SELF_DIRECTION=message.getDirection();
        Log.d("messagetype===",message.getType());


        if (message.getDirection()==1) {
            //Returning self

            if(message.getType().equalsIgnoreCase("text"))
            {
                return SELF_TEXT_IN;
            }
           else if(message.getType().equalsIgnoreCase("image"))
          {
              return SELF_IMAGE_IN;
          }
           else if (message.getType().equalsIgnoreCase("product"))
            {
                return SELF_PRODUCT_IN;
            }
            else if (message.getType().equalsIgnoreCase("rating"))
            {
                Log.d("messagetype===",message.getType());

                return SELF_RATING_IN;
            }
            else if (message.getType().equalsIgnoreCase("audio"))
            {
                return SELF_AUDIO_IN;
            }
            else if (message.getType().equalsIgnoreCase("add-money"))
            {
                return SELF_ADDMONEY_IN;
            }
            else if (message.getType().equalsIgnoreCase("recharge"))
            {
                return SELF_RECHARGE_IN;
            }
            else if (message.getType().equalsIgnoreCase("paid"))
            {
                return SELF_PAID_IN;
            }
            else if (message.getType().equalsIgnoreCase("address"))
            {
                return SELF_ADDRESS_IN;
            }
            else if (message. getType().equalsIgnoreCase("store"))
            {
                return SELF_STORE_IN;
            }
            else if (message.getType().equalsIgnoreCase("order_confirmed"))
            {
                return SELF_ORDERCONFIRMED_IN;
            }


            //return SELF_TEXT;
        }
        else
        {
            if(message.getType().equalsIgnoreCase("text"))
            {
                return SELF_TEXT_OUT;
            }
           else if(message.getType().equalsIgnoreCase("image"))
            {
                return SELF_IMAGE_OUT;
            }

            else if (message.getType().equalsIgnoreCase("product"))
            {
                return SELF_PRODUCT_OUT;
            }

            else if (message.getType().equalsIgnoreCase("rating"))
            {
                Log.d("messagetype===",message.getType());
                return SELF_RATING_OUT;
            }
            else if (message.getType().equalsIgnoreCase("audio"))
            {
                return SELF_AUDIO_OUT;
            }
            else if (message.getType().equalsIgnoreCase("add-money"))
            {
                return SELF_ADDMONEY_OUT;
            }
            else if (message.getType().equalsIgnoreCase("recharge"))
            {
                return SELF_RECHARGE_OUT;
            }
            else if (message.getType().equalsIgnoreCase("paid"))
            {
                return SELF_PAID_OUT;
            }
            else if (message.getType().equalsIgnoreCase("address"))
            {

                return SELF_ADDRESS_OUT;
            }
            else if (message. getType().equalsIgnoreCase("store"))
            {
                return SELF_STORE_OUT;
            }
            else if (message.getType().equalsIgnoreCase("order_confirmed"))
            {
                return SELF_ORDERCONFIRMED_OUT;
            }

        }
        //else returning position
        return 0;
    }




     public class Holder extends RecyclerView.ViewHolder {
        /*Todo:- Location*/
        ImageView locationImage;
        TextView locationText,location2Text,locationDate;
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

         /*Todo:- Type Paid*/
         ChatMessageView paymentReceiveLayout;
         TextView paymentReceiveMsg,paymentReceiveDateText;


         /*Todo:- Confirm Details*/
         ChatMessageView orderConfirmLayout;
         TextView orderConfirmMessage,orderConfirmDate;
         Button detailsBtn;
        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
            location2Text=itemView.findViewById(R.id.location2Text);
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
         /*  ratingBar.setFocusableInTouchMode(true);
            ratingBar.setFocusable(true);
            ratingBar.setIsIndicator(true);*/
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

            /*Todo:- Confirm Details*/
            orderConfirmLayout=itemView.findViewById(R.id.orderConfirmLayout);
            orderConfirmMessage=itemView.findViewById(R.id.orderConfirmMessage);
            orderConfirmDate=itemView.findViewById(R.id.orderConfirmDate);
            detailsBtn=itemView.findViewById(R.id.detailsBtn);

        }
     }
}
