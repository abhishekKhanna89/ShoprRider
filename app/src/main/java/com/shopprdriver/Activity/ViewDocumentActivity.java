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
    /*Todo:- FrontAadhar*/
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String userChoosenTask;
    byte[] byteArrayAdharFront,byteArrayAdharBack,
            byteArrayDlNoFront,byteArrayDlNoBack,byteArrayFrontBike,byteArrayBakeBike,byteArrayPan;
    File destination;
    String select;
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
        select="1";
    }



    public void backAadhar(View view) {
        selectImage();
        select="2";
    }

    public void frontDlno(View view) {
        selectImage();
        select="3";
    }

    public void backDlno(View view) {
        selectImage();
        select="4";
    }

    public void frontBake(View view) {
        selectImage();
        select="5";
    }

    public void backBike(View view) {
        selectImage();
        select="6";
    }

    public void panCard(View view) {
        selectImage();
        select="7";
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
        if (select.equalsIgnoreCase("1")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        }else if (select.equalsIgnoreCase("2")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult1(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult1(data);
            }
        }else if (select.equalsIgnoreCase("3")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult3(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult3(data);
            }
        }else if (select.equalsIgnoreCase("4")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult4(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult4(data);
            }
        }else if (select.equalsIgnoreCase("5")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult5(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult5(data);
            }
        }else if (select.equalsIgnoreCase("6")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult6(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult6(data);
            }
        }else if (select.equalsIgnoreCase("7")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult7(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult7(data);
            }
        }

    }

    /*Todo:- Seven*/
    private void onCaptureImageResult7(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayPan=bytes.toByteArray();
        panCard.setImageBitmap(thumbnail);
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

        adhaarUpload7();
    }


    private void onSelectFromGalleryResult7(Intent data) {
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
        byteArrayPan=stream.toByteArray();
        panCard.setImageBitmap(bm);
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
        adhaarUpload7();
    }

    private void adhaarUpload7() {
        RequestBody pan_card = RequestBody
                .create(MediaType.parse("image/*"),byteArrayPan);
        MultipartBody.Part filePartPan = MultipartBody.Part.createFormData("pan_card", destination.getName(), pan_card);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartPan);
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

    /*Todo:- Six*/
    private void onCaptureImageResult6(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayBakeBike=bytes.toByteArray();
        backBike.setImageBitmap(thumbnail);
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

        adhaarUpload6();
    }

    private void onSelectFromGalleryResult6(Intent data) {
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
        byteArrayBakeBike=stream.toByteArray();
        backBike.setImageBitmap(bm);
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
        adhaarUpload6();
    }

    private void adhaarUpload6() {
        RequestBody backBike = RequestBody
                .create(MediaType.parse("image/*"),byteArrayBakeBike);
        MultipartBody.Part filePartBackBike = MultipartBody.Part.createFormData("bike_back", destination.getName(), backBike);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartBackBike);
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

    /*Todo:- Five*/
    private void onCaptureImageResult5(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayFrontBike=bytes.toByteArray();
        frontBke.setImageBitmap(thumbnail);
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

        adhaarUpload5();
    }

    private void onSelectFromGalleryResult5(Intent data) {
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
        byteArrayFrontBike=stream.toByteArray();
        frontBke.setImageBitmap(bm);
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
        adhaarUpload5();
    }

    private void adhaarUpload5() {
        RequestBody frontBike = RequestBody
                .create(MediaType.parse("image/*"),byteArrayFrontBike);
        MultipartBody.Part filePartFrontBike = MultipartBody.Part.createFormData("bike_front", destination.getName(), frontBike);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartFrontBike);
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

    /*Todo:- Four*/
    private void onCaptureImageResult4(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayDlNoBack=bytes.toByteArray();
        backDlno.setImageBitmap(thumbnail);
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

        adhaarUpload4();
    }

    private void onSelectFromGalleryResult4(Intent data) {
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
        byteArrayDlNoBack=stream.toByteArray();
        backDlno.setImageBitmap(bm);
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
        adhaarUpload4();
    }

    private void adhaarUpload4() {
        RequestBody back_dl_no = RequestBody
                .create(MediaType.parse("image/*"), byteArrayDlNoBack);
        MultipartBody.Part filePartback_dl_no = MultipartBody.Part.createFormData("back_dl_no", destination.getName(), back_dl_no);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartback_dl_no);
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

    /*Todo:- Third*/
    private void onCaptureImageResult3(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayDlNoFront=bytes.toByteArray();
        frontDlno.setImageBitmap(thumbnail);
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

        adhaarUpload3();
    }

    /*Todo:- Third*/
    private void onSelectFromGalleryResult3(Intent data) {
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
        byteArrayDlNoFront=stream.toByteArray();
        frontDlno.setImageBitmap(bm);
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
        adhaarUpload3();
    }

    private void adhaarUpload3() {
        RequestBody front_dl_no = RequestBody
                .create(MediaType.parse("image/*"), byteArrayDlNoFront);
        MultipartBody.Part filePartDlFront = MultipartBody.Part.createFormData("front_dl_no", destination.getName(), front_dl_no);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartDlFront);
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

    /*Todo:- Second*/
    private void onCaptureImageResult1(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayAdharBack=bytes.toByteArray();
        backAadhar.setImageBitmap(thumbnail);
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

        adhaarUpload1();
    }

    private void adhaarUpload1() {
        RequestBody back_aadhaar_card = RequestBody
                .create(MediaType.parse("image/*"), byteArrayAdharBack);
        MultipartBody.Part filePartAdharBack = MultipartBody.Part.createFormData("back_aadhaar_card", destination.getName(), back_aadhaar_card);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        Call<DocumentModel>call=ApiExecutor.getApiService(this)
                .apiDocument(headers,filePartAdharBack);
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

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult1(Intent data) {
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
        byteArrayAdharBack=stream.toByteArray();
        backAadhar.setImageBitmap(bm);
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
        adhaarUpload1();
    }

    /*Todo:- first*/
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArrayAdharFront=bytes.toByteArray();
        frontAadhar.setImageBitmap(thumbnail);
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