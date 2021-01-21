package com.shopprdriver.Server;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.shopprdriver.Model.AcceptModel;
import com.shopprdriver.Model.CancelModel;
import com.shopprdriver.Model.ChatMessage.ChatMessageModel;
import com.shopprdriver.Model.Login.LoginModel;
import com.shopprdriver.Model.OtpVerification.OtpVerifyModel;
import com.shopprdriver.Model.RatingsModel;
import com.shopprdriver.Model.RejectedModel;
import com.shopprdriver.Model.Send.SendModel;
import com.shopprdriver.Model.UserChatList.UserChatListModel;
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
    @POST("verify-otp")
    Call<OtpVerifyModel>otpService(@Body OtpVerifyRequest verifyRequest);
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


}