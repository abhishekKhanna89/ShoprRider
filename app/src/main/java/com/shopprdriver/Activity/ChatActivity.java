

package com.shopprdriver.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.gson.Gson;
import com.shopprdriver.Adapter.ChatAppMsgAdapter;
import com.shopprdriver.Adapter.ChatMessageAdapter;
import com.shopprdriver.Model.ChatMessage.Chat;
import com.shopprdriver.Model.ChatMessage.ChatMessageModel;
import com.shopprdriver.Model.ChatModel;
import com.shopprdriver.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shopprdriver.Model.Send.SendModel;
import com.shopprdriver.Model.TerminateChat.TerminateChatModel;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.TextTypeRequest;
import com.shopprdriver.RequestService.WalletRequest;
import com.shopprdriver.SendBird.call.CallService;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Server.Helper;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.app.Config;
import com.shopprdriver.app.Progressbar;
import com.shopprdriver.util.NotificationUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class ChatActivity extends AppCompatActivity {
    SessonManager sessonManager;
    int chat_id;
    RecyclerView chatRecyclerView;
    List<Chat> chatList;
    EditText editText;
    ImageButton sendMsgBtn,chooseImage;
    /*Todo:- BroadCast Receiver*/
    BroadcastReceiver mRegistrationBroadcastReceiver;
    String body;

    List<ChatModel> msgDtoList;
    ChatAppMsgAdapter chatAppMsgAdapter;
    /*Todo:- Image Choose*/
    int PICK_IMAGE_MULTIPLE = 1;
    File photoFile;
    Uri photoUri;
    String mCurrentMPath;
    ArrayList<String> imagePathList = new ArrayList<>();
    Bitmap bitmap = null;
    private String photoPath;
    String imageEncoded;
    private static String baseUrl=ApiExecutor.baseUrl;

    /*Todo:- Voice Recorder*/
    boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;
    String pathforaudio;
    /*Todo:- FloatingActionButton*/
    FabSpeedDial fabSpeedDial;
    /*Todo:- Alert Dialog*/
    AlertDialog alertDialog;
    ImageView circleImage;
    BroadcastReceiver mMessageReceiver;

    /*Todo:- UserDP*/
    CircleImageView userDp;
    TextView userName;

    Progressbar progressbar;

    /*Todo:- Recording Library*/
    RecordView recordView;
    RecordButton recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        //chatRecyclerView.setHasFixedSize(true);
        //chatRecyclerView.setItemViewCacheSize(1000);
        //chatRecyclerView.setDrawingCacheEnabled(true);
        //chatRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //chatRecyclerView.setNestedScrollingEnabled(false);
        /*Todo:- UserDP*/
        userDp=findViewById(R.id.userDp);
        userName=findViewById(R.id.userName);

        editText = findViewById(R.id.editText);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        chooseImage=findViewById(R.id.chooseImage);
        /*Todo:- Recording Library*/
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);


        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);


        recordView.setSlideToCancelText("Slide To Cancel");


        recordView.setCustomSounds(0, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                recordView.setVisibility(View.VISIBLE);
                Log.d("RecordView", "onStart");
                startRecording();

            }

            @Override
            public void onCancel() {
                //Toast.makeText(ChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onCancel");

            }



            @Override
            public void onFinish(long recordTime) {
                recordView.setVisibility(View.GONE);
                String time = getHumanTimeText(recordTime);
                stopRecording();
                //Toast.makeText(ChatActivity.this, "onFinishRecord - Recorded Time is: " + time, Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onFinish");

                Log.d("RecordTime", time);
            }
            @Override
            public void onLessThanSecond() {
                // Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                recordView.setVisibility(View.GONE);
                Log.d("RecordView", "Basket Animation Finished");
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            String chat_status = getIntent().getStringExtra("chat_status");
            if (chat_status != null && chat_status.equalsIgnoreCase("0")) {
                chat_id = getIntent().getIntExtra("chat_id", 0);
                Log.d("chatId1",""+chat_id);
                //chatMessageList(chat_id);
            } else if (chat_status != null && chat_status.equalsIgnoreCase("1")) {
                chat_id = getIntent().getIntExtra("chat_id", 0);
                Log.d("chatId2",""+chat_id);
                //chatMessageList(chat_id);
            }else if (chat_status != null && chat_status.equalsIgnoreCase("2")) {
                chat_id = Integer.parseInt(extras.getString("chat_id"));
                //chatMessageList(chat_id);
                Log.d("chatId3",""+chat_id);
            }else {
                String  value = String.valueOf(getIntent().getExtras().get("chat_id"));
                chat_id= Integer.parseInt(value);
                //chatMessageList(chat_id);
                Log.d("chatId4",""+chat_id);
            }

            //
        }
        //Log.d("chatId5",""+chat_id);
        if (chatList==null){
            chatMessageList(chat_id);
        }else  if (chatList.size()>0){
            ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
            chatRecyclerView.setAdapter(chatMessageAdapter);
            chatRecyclerView.scrollToPosition(chatList.size()-1);
            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
            chatMessageAdapter.notifyDataSetChanged();
        }






        /*Todo:- FloatingActionButton*/
        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        /*fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {

                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }
        });*/
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.fab_product:
                        showCustomDialog();
                        break;
                    case R.id.action_wallet:
                        showWalletDialog();
                        break;
                    case R.id.action_ask_payment:
                        Call<SendModel>call=ApiExecutor.getApiService(ChatActivity.this)
                                .apiSendPaymentRequest("Bearer "+sessonManager.getToken(),chat_id,"payment");
                        call.enqueue(new Callback<SendModel>() {
                            @Override
                            public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                if (response.body()!=null) {
                                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                        chatMessageList(chat_id);
                                        Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<SendModel> call, Throwable t) {

                            }
                        });
                        break;
                    case  R.id.action_terminate_chat:
                        new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("Are you sure want to terminate chat?")
                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        terminate();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.splash_transparent)
                                .show();
                        break;
                }
                return false;
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
        msgDtoList=new ArrayList<>();
        chatList=new ArrayList<>();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessageList(Integer.parseInt(String.valueOf(chat_id)));
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(mMessageReceiver,new IntentFilter(i));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    sendMsgBtn.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                } else {
                    sendMsgBtn.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                    recordView.setVisibility(View.GONE);
                    sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.send));
                    sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String msgContent = editText.getText().toString();
                            if(!TextUtils.isEmpty(msgContent))
                            {
                                if (CommonUtils.isOnline(ChatActivity.this)) {
                                    //sessonManager.showProgress(ChatActivity.this);
                                    TextTypeRequest textTypeRequest=new TextTypeRequest();
                                    textTypeRequest.setType("text");
                                    textTypeRequest.setMessage(msgContent);
                                    Call<SendModel>call=ApiExecutor.getApiService(ChatActivity.this)
                                            .apiSend("Bearer "+sessonManager.getToken(), chat_id,textTypeRequest);
                                    call.enqueue(new Callback<SendModel>() {
                                        @Override
                                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                           // sessonManager.hideProgress();
                                            if (response.body()!=null) {
                                                SendModel sendModel=response.body();
                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                    editText.getText().clear();
                                                    chatMessageList(chat_id);
                                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(ChatActivity.this, ""+sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }


                                        @Override
                                        public void onFailure(Call<SendModel> call, Throwable t) {
                                           // sessonManager.hideProgress();
                                        }
                                    });
                                }else {
                                    CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
                                }

                            }
                        }
                    });

                }
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*if (chatList.size()==0){
            chatMessageList(chat_id);
        }else if (chatList.size()>0){
            ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
            chatRecyclerView.setAdapter(chatMessageAdapter);
            chatRecyclerView.scrollToPosition(chatList.size()-1);
            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
            chatMessageAdapter.notifyDataSetChanged();
        }*/
    }

    private void terminate() {
        Call<TerminateChatModel>call=ApiExecutor.getApiService(this)
                .apiChatTerminate("Bearer "+sessonManager.getToken(),chat_id);
        call.enqueue(new Callback<TerminateChatModel>() {
            @Override
            public void onResponse(Call<TerminateChatModel> call, Response<TerminateChatModel> response) {
                if (response.body()!=null){
                    if (response.body().getStatus()!=null&&response.body().getStatus().equalsIgnoreCase("success")){
                        Toast.makeText(ChatActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ChatActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TerminateChatModel> call, Throwable t) {

            }
        });
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void showWalletDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_wallet, viewGroup, false);

        EditText editName=dialogView.findViewById(R.id.editName);
        Button submitBtn=dialogView.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(ChatActivity.this)) {
                    //sessonManager.showProgress(ChatActivity.this);
                    WalletRequest walletRequest=new WalletRequest();
                    walletRequest.setType("add-money");
                    walletRequest.setMessage(editName.getText().toString());
                    ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
                    iApiServices.apiWalletRequest("Bearer "+sessonManager.getToken(), chat_id,walletRequest)
                            .enqueue(new Callback<SendModel>() {
                                @Override
                                public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                    //sessonManager.hideProgress();
                                    if (response.body()!=null) {
                                        if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            chatMessageList(chat_id);

                                            alertDialog.dismiss();
                                            // Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                        }else {
                                            //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<SendModel> call, Throwable t) {
                                    //sessonManager.hideProgress();
                                }
                            });
                }else {
                    CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
                }
            }
        });

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.product_dialog_layout, viewGroup, false);
        TextView successText=dialogView.findViewById(R.id.successText);
        circleImage=dialogView.findViewById(R.id.circleImage);
        ImageView insertImage=dialogView.findViewById(R.id.insertImage);
        ImageView closeBtn=dialogView.findViewById(R.id.closeBtn);
        EditText editName=dialogView.findViewById(R.id.editName);
        EditText editPrice=dialogView.findViewById(R.id.editPrice);
        EditText editQuantity=dialogView.findViewById(R.id.editQuantity);
        Button submitBtn=dialogView.findViewById(R.id.submitBtn);



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.showProgress(ChatActivity.this);
                if (CommonUtils.isOnline(ChatActivity.this)) {
                    //sessonManager.showProgress(ChatActivity.this);
                    HashMap<String, RequestBody> partMap = new HashMap<>();
                    partMap.put("type", ApiFactory.getRequestBodyFromString("product"));
                    partMap.put("name",ApiFactory.getRequestBodyFromString(editName.getText().toString()));
                    partMap.put("price",ApiFactory.getRequestBodyFromString(editPrice.getText().toString()));
                    partMap.put("quantity",ApiFactory.getRequestBodyFromString(editQuantity.getText().toString()));
                    MultipartBody.Part[] imageArray1 = new MultipartBody.Part[imagePathList.size()];
                    //Log.d("arrayLis",""+imageArray1);

                    for (int i = 0; i < imageArray1.length; i++) {
                        File file = new File(imagePathList.get(i));
                        try {
                            File compressedfile = new Compressor(ChatActivity.this).compressToFile(file);
                            RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                            imageArray1[i] = MultipartBody.Part.createFormData("file", compressedfile.getName(), requestBodyArray);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer "+sessonManager.getToken());
                    ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
                    iApiServices.apiProductSend(headers, chat_id,imageArray1,partMap)
                            .enqueue(new Callback<SendModel>() {
                                @Override
                                public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                    progressbar.hideProgress();
                                    //sessonManager.hideProgress();
                                    if (response.body()!=null) {
                                        if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            chatMessageList(chat_id);

                                            alertDialog.dismiss();
                                            // Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<SendModel> call, Throwable t) {
                                    progressbar.hideProgress();
                                    //sessonManager.hideProgress();
                                }
                            });
                }else {
                    CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserImageFile();
            }
        });



        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void inserImageFile() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 787);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeCameraImg1();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }

    private void takeCameraImg1() throws Exception {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createFile1();
                //Log.d("checkexcesdp", String.valueOf(photoFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                //Log.d("checkexcep", ex.getMessage());
            }
            photoFile = createFile1();
            photoUri = FileProvider.getUriForFile(ChatActivity.this, getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, 2);

        }
    }

    private File createFile1()throws Exception {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMPath = image.getAbsolutePath();
        return image;
    }



    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 786);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }

    private void chatMessageList(int chat_id) {
        Log.d("ChhatId++",""+chat_id);
        if (CommonUtils.isOnline(ChatActivity.this)) {
            Log.d("TokenResponse",sessonManager.getToken());
            Call<ChatMessageModel>call=ApiExecutor.getApiService(this).apiChatMessage("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<ChatMessageModel>() {
                @Override
                public void onResponse(Call<ChatMessageModel> call, Response<ChatMessageModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ChatMessageModel chatMessageModel=response.body();
                            Gson gson=new Gson();
                            String msg=gson.toJson(chatMessageModel);
                            Log.d("ressTYTUTYUU",""+msg);
                            if (chatMessageModel.getData()!=null){
                                chatList=chatMessageModel.getData().getChats();
                                Picasso.get().load(chatMessageModel.getData().getCustomer().getImage()).into(userDp);
                                userName.setText(chatMessageModel.getData().getCustomer().getName());
                                ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
                                chatRecyclerView.setAdapter(chatMessageAdapter);
                                chatMessageAdapter.notifyDataSetChanged();
                                chatRecyclerView.scrollToPosition(chatList.size()-1);
                                chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                                //chatMessageAdapter.notifyDataSetChanged();

                            }
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ChatActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatActivity.this,"");
                                            Toast.makeText(ChatActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatMessageModel> call, Throwable t) {
                    //sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == 1)) {

            try {
                rotateImage();

            } catch (Exception e) {
                e.printStackTrace();

            }


        }else if ((resultCode == RESULT_OK && requestCode == 2)){
            rotateImage1();
        }
        else if ((requestCode == 786)) {
            selectFromGallery(data);
        }else if ((requestCode == 787)){
            selectFromGallery1(data);
        }

    }

    private void selectFromGallery1(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPath = Helper.pathFromUri(ChatActivity.this, mImageUri);
                        imagePathList.add(photoPath);


                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor1.getString(columnIndex1);

                        if (Build.VERSION.SDK_INT > 23) {
                            // Log.d("inelswe", "inelse");
                            bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                        } else {
                            // Log.d("inelse", "inelse");
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                        }


                        //   deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        //ProfileUpdateAPI();
                        circleImage.setImageBitmap(bitmap);


                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPath = Helper.pathFromUri(ChatActivity.this, mImageUri);
                    imagePathList.add(photoPath);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor1.getString(columnIndex1);


                    if (Build.VERSION.SDK_INT > 23) {
                        //Log.d("inelswe", "inelse");
                        bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                    } else {
                        //Log.d("inelse", "inelse");
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                    }

                    //  deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);

                    cursor1.close();
                    //ProfileUpdateAPI();
                    circleImage.setImageBitmap(bitmap);


                }


            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void rotateImage1() {
        try {
            String photoPath = photoFile.getAbsolutePath();
            imagePathList.add(photoPath);

            // Log.d("ress",""+imagePathList);
            bitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), photoUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            if (Build.VERSION.SDK_INT > 23) {
                bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), photoUri);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            }
            //ProfileUpdateAPI();
            circleImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void takeCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createFile();
                //Log.d("checkexcesdp", String.valueOf(photoFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                //Log.d("checkexcep", ex.getMessage());
            }
            photoFile = createFile();
            photoUri = FileProvider.getUriForFile(ChatActivity.this, getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, 1);

        }

    }

    private File createFile() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMPath = image.getAbsolutePath();
        return image;
    }

    public void rotateImage() throws IOException {

        try {
            String photoPath = photoFile.getAbsolutePath();
            imagePathList.add(photoPath);

            // Log.d("ress",""+imagePathList);
            bitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), photoUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            if (Build.VERSION.SDK_INT > 23) {
                bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), photoUri);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            }
            ProfileUpdateAPI();
            //circleImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void ProfileUpdateAPI() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("image"));
            MultipartBody.Part[] imageArray1 = new MultipartBody.Part[imagePathList.size()];
            //Log.d("arrayLis",""+imageArray1);

            for (int i = 0; i < imageArray1.length; i++) {
                File file = new File(imagePathList.get(i));
                try {
                    File compressedfile = new Compressor(ChatActivity.this).compressToFile(file);
                    RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                    imageArray1[i] = MultipartBody.Part.createFormData("file", compressedfile.getName(), requestBodyArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer "+sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiImageSend(headers, chat_id,imageArray1,partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body()!=null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    chatMessageList(chat_id);
                                    Toast.makeText(ChatActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ChatActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectFromGallery(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPath = Helper.pathFromUri(ChatActivity.this, mImageUri);
                        imagePathList.add(photoPath);


                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor1.getString(columnIndex1);

                        if (Build.VERSION.SDK_INT > 23) {
                            // Log.d("inelswe", "inelse");
                            bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                        } else {
                            // Log.d("inelse", "inelse");
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                        }


                        //   deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        ProfileUpdateAPI();
                        //circleImage.setImageBitmap(bitmap);


                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPath = Helper.pathFromUri(ChatActivity.this, mImageUri);
                    imagePathList.add(photoPath);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor1.getString(columnIndex1);


                    if (Build.VERSION.SDK_INT > 23) {
                        //Log.d("inelswe", "inelse");
                        bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                    } else {
                        //Log.d("inelse", "inelse");
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                    }

                    //  deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);

                    cursor1.close();
                    ProfileUpdateAPI();
                    //circleImage.setImageBitmap(bitmap);


                }


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }


    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = null;
        if (Build.VERSION.SDK_INT > 23) {
            ei = new ExifInterface(input);
        }


        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }


    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void askForPermissioncamera(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }


    }
    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        uploadFile();
    }

    private void uploadFile() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            File file = new File(pathforaudio);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            //RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);

            //Call call = getResponse.uploadFile(fileToUpload, filename);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("audio"));
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());

            iApiServices.apiAudioSend(headers, chat_id, fileToUpload, partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    chatMessageList(chat_id);
                                   // Toast.makeText(ChatActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }


    private void startRecording() {
        //Get app external directory path
        String recordPath = getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        //filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);



        pathforaudio=recordPath + "/" + recordFile;

        Log.d("recordpath====",recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }
    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    /*Todo:- Option Menu*//*

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calling, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }else if (id==R.id.action_audio){
            initializationVoice(chat_id);
        }
        else if (id==R.id.action_video){
            initializationVideo(chat_id);
           *//* startActivity(new Intent(ChatDetailsActivity.this,VideoChatViewActivity.class)
                    .putExtra("chatId",chat_id)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*//*
        }

        return super.onOptionsItemSelected(item);
    }*/
    private void initializationVideo(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel>call= ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            InitiateVideoCallModel initiateVideoCallModel = response.body();
                            if (initiateVideoCallModel.getData()!=null){
                                String savedUserId=initiateVideoCallModel.getData().getUser_id();
                                CallService.dial(ChatActivity.this, savedUserId, true);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                //String savedCalleeId = PrefUtils.getCalleeId(ChatActivity.this);
                                /*PrefUtils.setCalleeId(ChatActivity.this, savedUserId);


                                CallService.dial(ChatActivity.this, savedCalleeId, true);*/

                            }
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ChatActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatActivity.this,"");
                                            Toast.makeText(ChatActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    private void initializationVoice(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel>call= ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            InitiateVideoCallModel initiateVideoCallModel = response.body();
                            if (initiateVideoCallModel.getData()!=null){
                                String savedUserId=initiateVideoCallModel.getData().getUser_id();
                                CallService.dial(ChatActivity.this, savedUserId, false);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                /*PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                String savedCalleeId = PrefUtils.getCalleeId(ChatActivity.this);
                                CallService.dial(ChatActivity.this, savedCalleeId, false);*/
                            }
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ChatActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatActivity.this,"");
                                            Toast.makeText(ChatActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void initializationVoice(View view) {
        initializationVoice(chat_id);
    }

    public void initializationVideo(View view) {
        initializationVideo(chat_id);
    }

    public void finish(View view) {
       onBackPressed();
    }
}