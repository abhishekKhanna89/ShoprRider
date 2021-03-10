package com.shopprdriver.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shopprdriver.Model.UploadDocument.DocumentModel;
import com.shopprdriver.Model.UploadDocument.GetDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDocumentActivity extends AppCompatActivity {
    ImageView frontAadhar, backAadhar,
            frontDlno, backDlno,frontBke,backBike, panCard;

    SessonManager sessonManager;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    byte[] byteArrayAdharFront,byteArrayAdharBack,
            byteArrayDlNoFront,byteArrayDlNoBack,byteArrayFrontBike,byteArrayBakeBike,byteArrayPan;
    File destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        frontAadhar = findViewById(R.id.frontAadhar);
        backAadhar = findViewById(R.id.backAadhar);
        frontDlno = findViewById(R.id.frontDlno);
        backDlno = findViewById(R.id.backDlno);
        panCard = findViewById(R.id.panCard);
        frontBke=findViewById(R.id.frontBke);
        backBike=findViewById(R.id.backBike);

        viewDocument();

    }

    private void viewDocument() {
        if (CommonUtils.isOnline(ViewDocumentActivity.this)) {
            Call<GetDocumentModel>call= ApiExecutor.getApiService(this)
                    .apiViewDocument("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<GetDocumentModel>() {
                @Override
                public void onResponse(Call<GetDocumentModel> call, Response<GetDocumentModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            GetDocumentModel getDocumentModel=response.body();
                            if (getDocumentModel.getData().getUser()!=null){
                                Picasso.get().load(getDocumentModel.getData().getUser().getFrontAadhaarCard()).into(frontAadhar);

                                Picasso.get().load(getDocumentModel.getData().getUser().getBackAadhaarCard()).into(backAadhar);

                                Picasso.get().load(getDocumentModel.getData().getUser().getFrontDlNo()).into(frontDlno);

                                Picasso.get().load(getDocumentModel.getData().getUser().getBackDlNo()).into(backDlno);

                                Picasso.get().load(getDocumentModel.getData().getUser().getPanCard()).into(panCard);

                                Picasso.get().load(getDocumentModel.getData().getUser().getBikeFront()).into(frontBke);

                                Picasso.get().load(getDocumentModel.getData().getUser().getBikeBack()).into(backBike);

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetDocumentModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(ViewDocumentActivity.this, getString(R.string.please_check_network));
        }
    }

    public void frontAadhar(View view) {
        selectImage();
    }



    public void backAadhar(View view) {

    }

    public void frontDlno(View view) {

    }

    public void backDlno(View view) {

    }

    public void frontBake(View view) {

    }

    public void backBike(View view) {

    }

    public void panCard(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDocumentActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(ViewDocumentActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayAdharFront=bytes.toByteArray();
         destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adhaarUpload();

    }



    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byteArrayAdharFront=stream.toByteArray();
        frontAadhar.setImageBitmap(bm);
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(stream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adhaarUpload();
    }
    private void adhaarUpload() {
        RequestBody front_aadhaar_card = RequestBody
                .create(MediaType.parse("image/*"), byteArrayAdharFront);
        MultipartBody.Part filePartAdharFront = MultipartBody.Part.createFormData("front_aadhaar_card", destination.getName(), front_aadhaar_card);

/*
        RequestBody back_aadhaar_card = RequestBody
                .create(MediaType.parse("image/*"), byteArrayAdharBack);
        MultipartBody.Part filePartAdharBack = MultipartBody.Part.createFormData("back_aadhaar_card", destination.getName(), back_aadhaar_card);*/
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartAdharFront);
        call.enqueue(new Callback<DocumentModel>() {
            @Override
            public void onResponse(Call<DocumentModel> call, Response<DocumentModel> response) {
                sessonManager.hideProgress();
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        Toast.makeText(ViewDocumentActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        viewDocument();
                    }else {
                        Toast.makeText(ViewDocumentActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DocumentModel> call, Throwable t) {
                sessonManager.hideProgress();
            }
        });
    }



    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context)
        {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}