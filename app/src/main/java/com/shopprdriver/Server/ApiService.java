package com.shopprdriver.Server;

import androidx.annotation.NonNull;

import com.shopprdriver.Model.AcceptChatModel;
import com.shopprdriver.Model.AcceptModel;
import com.shopprdriver.Model.Attendences.AttendencesModel;
import com.shopprdriver.Model.AvailableChat.AvailableChatModel;
import com.shopprdriver.Model.CancelModel;
import com.shopprdriver.Model.ChatMessage.ChatMessageModel;
import com.shopprdriver.Model.CheckinCheckouSucess.CheckinCheckouSucessModel;
import com.shopprdriver.Model.CommissionTransactions.CommissionTransactionsModel;
import com.shopprdriver.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shopprdriver.Model.LocationUpdateModel;
import com.shopprdriver.Model.Login.LoginModel;
import com.shopprdriver.Model.NotificationList.NotificationListModel;
import com.shopprdriver.Model.OrderDeatilsList.OrderDeatilsListModel;
import com.shopprdriver.Model.OrderDetails.OrderDetailsModel;
import com.shopprdriver.Model.OtpVerification.OtpVerifyModel;
import com.shopprdriver.Model.ProfileStatus.ProfileStatusModel;
import com.shopprdriver.Model.RatingsModel;
import com.shopprdriver.Model.RejectedModel;
import com.shopprdriver.Model.Send.SendModel;
import com.shopprdriver.Model.StateList.StateListModel;
import com.shopprdriver.Model.UpdateLocationRequest;
import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
import com.shopprdriver.Model.WalletHistory.WalletHistoryModel;

import com.shopprdriver.RequestService.AccountDetailsRequest;
import com.shopprdriver.RequestService.CheckInCheckOutRequest;
import com.shopprdriver.RequestService.LoginRequest;
import com.shopprdriver.RequestService.OtpVerifyRequest;
import com.shopprdriver.RequestService.RatingsRequest;
import com.shopprdriver.RequestService.TextTypeRequest;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login-with-otp")
    Call<LoginModel> loginUser(@Body LoginRequest requestBody);

    @Multipart
    @POST("register")
    Call<LoginModel> apiRegister(@HeaderMap Map<String, String> token, @Part MultipartBody.Part[] images, @PartMap() Map<String, RequestBody> partMap);


    @POST("verify-otp")
    Call<OtpVerifyModel>otpService(@Body OtpVerifyRequest verifyRequest);
    @NonNull
    @GET("available-chats")
    Call<AvailableChatModel> apiUserAvailableChatList(@Header("Authorization") String token);
    @NonNull
    @GET("chats")
    Call<UserChatListModel> apiUserChatList(@Header("Authorization") String token);

    @NonNull
    @GET("chat-messages/{chat_id}")
    Call<ChatMessageModel>apiChatMessage(@Header("Authorization") String token,
                                         @Path("chat_id")int chat_id);
    @NonNull
    @GET("accept/{message_id}")
    Call<AcceptModel>apiAccept(@Header("Authorization") String token,
                               @Path("message_id")int message_id);
    @NonNull
    @GET("reject/{message_id}")
    Call<RejectedModel>apiRejected(@Header("Authorization") String token,
                                   @Path("message_id")int message_id);
    @NonNull
    @GET("cancel/{message_id}")
    Call<CancelModel>apiCancel(@Header("Authorization") String token,
                               @Path("message_id")int message_id);
    @POST("rate-service/{message_id}")
    Call<RatingsModel>apiRatings(@Header("Authorization") String token,
                                 @Path("message_id")int message_id,@Body RatingsRequest ratingsRequest);

    @POST("send-message/{chat_id}")
    Call<SendModel>apiSend(@Header("Authorization") String token,
                           @Path("chat_id")int chat_id, @Body TextTypeRequest textTypeRequest);

    @Multipart
    @POST("send-message/{chat_id}")
    Call<SendModel>apiImageSend(@HeaderMap Map<String, String> token, @Path("chat_id")int chat_id, @Part MultipartBody.Part[] images, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("send-message/{chat_id}")
    Call<SendModel>apiAudioSend(@HeaderMap Map<String, String> token, @Path("chat_id")int chat_id,@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("send-message/{chat_id}")
    Call<SendModel>apiProductSend(@HeaderMap Map<String, String> token, @Path("chat_id")int chat_id, @Part MultipartBody.Part[] images, @PartMap() Map<String, RequestBody> partMap);


    @POST("update-location")
    Call<LocationUpdateModel>apiLocationUpdate(@Header("Authorization") String token,
                                               @Body UpdateLocationRequest updateLocationRequest);


    @NonNull
    @GET("initiate-video-call/{chat_id}")
    Call<InitiateVideoCallModel>apiInitiateVideoCall(@Header("Authorization") String token,
                                                     @Path("chat_id")int chat_id);
    @NonNull
    @GET("notifications")
    Call<NotificationListModel>apiNotificationList(@Header("Authorization") String token,
                                                   @Query("page")int page);


    @NonNull
    @GET("orders")
    Call<OrderDetailsModel>apiMyOrderDetails(@Header("Authorization") String token,
                                             @Query("page")int page);

    @NonNull
    @GET("state-list")
    Call<StateListModel>apiStateList();
    @Multipart
    @POST("upload-document")
    Call<UploadDocumentModel>apiUploadDocument(@HeaderMap Map<String, String> token,@Part MultipartBody.Part  pan_card,
                                               @Part MultipartBody.Part front_aadhaar_card,
                                               @Part MultipartBody.Part back_aadhaar_card,
                                               @Part MultipartBody.Part front_dl_no,
                                               @Part MultipartBody.Part back_dl_no);
    @NonNull
    @GET("wallet-history")
    Call<WalletHistoryModel>apiWalletHistory(@Header("Authorization") String token);

    @NonNull
    @GET("deliver-order/{order_id}")
    Call<LoginModel>apiDeliverOrder(@Header("Authorization") String token,
                                    @Path("order_id")int order_id);
    @NonNull
    @GET("order-details/{order_id}")
    Call<OrderDeatilsListModel>apiMyOderDetails(@Header("Authorization") String token,
                                                @Path("order_id")int order_id);

    @POST("commission-history")
    Call<CommissionTransactionsModel>apiCommissionTransaction(@Header("Authorization") String token);

    @POST("update-details")
    Call<UploadDocumentModel>apiUpdateDetails(@Header("Authorization") String token,
                                              @Body AccountDetailsRequest accountDetailsRequest);

    @NonNull
    @GET("profile-status")
    Call<ProfileStatusModel>apiProfileStatus(@Header("Authorization") String token);

    @NonNull
    @GET("accept-chat/{chat_id}")
    Call<AcceptChatModel>apiAcceptChat(@Header("Authorization") String token,
                                       @Path("chat_id") int chat_id);
    @NonNull
    @GET("reject-chat/{chat_id}")
    Call<AcceptChatModel>apiRejectChat(@Header("Authorization") String token,
                                       @Path("chat_id") int chat_id);

    @POST("check-in")
    Call<CheckinCheckouSucessModel>apiCheckIn(@Header("Authorization") String token,
                                              @Body CheckInCheckOutRequest checkInCheckOutRequest);

    @POST("check-out")
    Call<CheckinCheckouSucessModel>apiCheckOut(@Header("Authorization") String token,
                                              @Body CheckInCheckOutRequest checkInCheckOutRequest);

    @NonNull
    @GET("attendences")
    Call<AttendencesModel>apiAttendence(@Header("Authorization") String token);

}
