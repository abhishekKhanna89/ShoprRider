package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Server.Helper;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.MediaRecorder.VideoSource.CAMERA;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Page1Activity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_IMAGE = 2;
    ImageView frontAadhar, backAadhar,
            frontDlno, backDlno, panCard;


    Bitmap bitmapAadharFront;
    String select;
    ArrayList<String> imagePathListFrontAdhar = new ArrayList<>();
    ArrayList<String> imagePathListBackAdhar = new ArrayList<>();
    ArrayList<String> imagePathListFrontDl = new ArrayList<>();
    ArrayList<String> imagePathListBackDl = new ArrayList<>();
    ArrayList<String> imagePathListPan = new ArrayList<>();
    private static String baseUrl = "http://shoppr.avaskmcompany.xyz/api/shoppr/";
    SessonManager sessonManager;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);
        String form_step = getIntent().getStringExtra("form_step");
        if (form_step != null) {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + form_step + "</font>")));
        } else {
            getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Form Step " + 1 + "</font>")));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        frontAadhar = findViewById(R.id.frontAadhar);
        backAadhar = findViewById(R.id.backAadhar);
        frontDlno = findViewById(R.id.frontDlno);
        backDlno = findViewById(R.id.backDlno);
        panCard = findViewById(R.id.panCard);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void frontAadhar(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Page1Activity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showGallery();
                                break;
                            case 1:
                                showCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    private void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "1";
    }

    private void showGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "1";
    }


    public void backAadhar(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Page1Activity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showGallery2();
                                break;
                            case 1:
                                showCamera2();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "2";
    }

    private void showGallery2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "2";
    }

    public void frontDlno(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Page1Activity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showGallery3();
                                break;
                            case 1:
                                showCamera3();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera3() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "3";
    }

    private void showGallery3() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "3";
    }

    public void backDlno(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Page1Activity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showGallery4();
                                break;
                            case 1:
                                showCamera4();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera4() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "4";
    }

    private void showGallery4() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "4";
    }

    public void panCard(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Page1Activity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showGallery5();
                                break;
                            case 1:
                                showCamera5();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera5() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "5";
    }

    private void showGallery5() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "5";
    }
    /*Todo:- Image Choose*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (select.equalsIgnoreCase("1")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                frontAadhar.setImageBitmap(bitmapAadharFront);
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        frontAadhar.setImageBitmap(bitmapAadharFront);
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (select.equalsIgnoreCase("2")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                backAadhar.setImageBitmap(bitmapAadharFront);
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        backAadhar.setImageBitmap(bitmapAadharFront);
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (select.equalsIgnoreCase("3")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                frontDlno.setImageBitmap(bitmapAadharFront);
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        frontDlno.setImageBitmap(bitmapAadharFront);
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (select.equalsIgnoreCase("4")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                backDlno.setImageBitmap(bitmapAadharFront);
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        backDlno.setImageBitmap(bitmapAadharFront);
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (select.equalsIgnoreCase("5")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                panCard.setImageBitmap(bitmapAadharFront);
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        panCard.setImageBitmap(bitmapAadharFront);
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }



    public void submit(View view) {
        if (imagePathListFrontAdhar.size()==0||imagePathListBackAdhar.size()==0||
                imagePathListFrontDl.size()==0||imagePathListBackDl.size()==0||
                imagePathListPan.size()==0){
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        }else {
            uploadDocument();
            //Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDocument() {
        if (CommonUtils.isOnline(Page1Activity.this)) {
            sessonManager.showProgress(Page1Activity.this);


            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiUploadDocument(headers)
                    .enqueue(new Callback<UploadDocumentModel>() {
                        @Override
                        public void onResponse(Call<UploadDocumentModel> call, Response<UploadDocumentModel> response) {
                            sessonManager.hideProgress();
                            if (response.body()!=null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    Toast.makeText(Page1Activity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    UploadDocumentModel uploadDocumentModel=response.body();
                                    int step=uploadDocumentModel.getFormStep();
                                    String form_step=String.valueOf(step);
                                    sessonManager.setForm_Step(form_step);
                                    startActivity(new Intent(Page1Activity.this, Page2Activity.class)
                                            .putExtra("form_step",form_step)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }
                            }else {
                                Toast.makeText(Page1Activity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadDocumentModel> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });

        }
    }
}