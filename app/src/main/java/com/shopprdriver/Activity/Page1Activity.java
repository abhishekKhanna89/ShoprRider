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
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

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

public class Page1Activity extends AppCompatActivity {

    ImageView frontAadhar, backAadhar,
            frontDlno, backDlno,frontBke,backBike, panCard;
    /*Todo:- FrontAadhar*/
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    String select;
    String select1,select2,select3,select4,select5,select6,select7;
    SessonManager sessonManager;
    private String userChoosenTask;
    byte[]byteArrayAdharFront,byteArrayAdharBack,
            byteArrayDlNoFront,byteArrayDlNoBack,byteArrayFrontBike,byteArrayBakeBike,byteArrayPan;

    File destination;
    private static String baseUrl = ApiExecutor.baseUrl;
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
        frontBke=findViewById(R.id.frontBke);
        backBike=findViewById(R.id.backBike);
        select1="0";
        select2="0";
        select3="0";
        select4="0";
        select5="0";
        select6="0";
        select7="0";



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

        AlertDialog.Builder builder = new AlertDialog.Builder(Page1Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Page1Activity.this);

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
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult(data);
                    select1 = "1";
                }
                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult(data);
                    select1 = "1";
                }
            }
        }else if (select.equalsIgnoreCase("2")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult1(data);
                    select2 = "2";
                }
                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult1(data);
                    select2 = "2";
                }
            }
        }else if (select.equalsIgnoreCase("3")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult3(data);
                    select3 = "3";
                }

                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult3(data);
                    select3 = "3";
                }
            }
        }else if (select.equalsIgnoreCase("4")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE){
                    onSelectFromGalleryResult4(data);
                    select4 = "4";
                }

                else if (requestCode == REQUEST_CAMERA){
                    onCaptureImageResult4(data);
                    select4 = "4";
                }

            }
        }else if (select.equalsIgnoreCase("5")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult5(data);
                    select5 = "5";
                }
                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult5(data);
                    select5 = "5";
                }
            }
        }else if (select.equalsIgnoreCase("6")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult6(data);
                    select6 = "6";
                }
                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult6(data);
                    select6 = "6";
                }
            }
        }else if (select.equalsIgnoreCase("7")){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    onSelectFromGalleryResult7(data);
                    select7 = "7";
                }
                else if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult7(data);
                    select7 = "7";
                }
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

    public void submit(View view) {

        if(select1.equalsIgnoreCase("1")
                &&select2.equalsIgnoreCase("2")
                &&select3.equalsIgnoreCase("3")
                &&select4.equalsIgnoreCase("4")
                &&select5.equalsIgnoreCase("5")
                &&select6.equalsIgnoreCase("6")
                &&select7.equalsIgnoreCase("7"))
        {
            uploadDocument();
        }
        else
        {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();

        }

    }
    private void uploadDocument() {
        if (CommonUtils.isOnline(Page1Activity.this)) {
            sessonManager.showProgress(Page1Activity.this);
            RequestBody frontBike = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayFrontBike);
            MultipartBody.Part filePartFrontBike = MultipartBody.Part.createFormData("bike_front", destination.getName(), frontBike);

            RequestBody backBike = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayBakeBike);
            MultipartBody.Part filePartBackBike = MultipartBody.Part.createFormData("bike_back", destination.getName(), backBike);


            RequestBody pan_card = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayPan);
            MultipartBody.Part filePartPan = MultipartBody.Part.createFormData("pan_card", destination.getName(), pan_card);
            RequestBody front_aadhaar_card = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayAdharFront);
            MultipartBody.Part filePartAdharFront = MultipartBody.Part.createFormData("front_aadhaar_card", destination.getName(), front_aadhaar_card);


            RequestBody back_aadhaar_card = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayAdharBack);
            MultipartBody.Part filePartAdharBack = MultipartBody.Part.createFormData("back_aadhaar_card", destination.getName(), back_aadhaar_card);
            RequestBody front_dl_no = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayDlNoFront);
            MultipartBody.Part filePartDlFront = MultipartBody.Part.createFormData("front_dl_no", destination.getName(), front_dl_no);
            RequestBody back_dl_no = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayDlNoBack);
            MultipartBody.Part filePartback_dl_no = MultipartBody.Part.createFormData("back_dl_no", destination.getName(), back_dl_no);

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiUploadDocument(headers,filePartPan,filePartAdharFront,filePartAdharBack,filePartDlFront,
                    filePartback_dl_no,filePartFrontBike,filePartBackBike)
                    .enqueue(new Callback<UploadDocumentModel>() {
                        @Override
                        public void onResponse(Call<UploadDocumentModel> call, Response<UploadDocumentModel> response) {
                            sessonManager.hideProgress();
                            //Toast.makeText(Page1Activity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body()!=null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    Toast.makeText(Page1Activity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    UploadDocumentModel uploadDocumentModel=response.body();
                                    int step=uploadDocumentModel.getFormStep();
                                    String form_step=String.valueOf(step);
                                    startActivity(new Intent(Page1Activity.this, Page2Activity.class)
                                            .putExtra("form_step",form_step)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    finishAffinity();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadDocumentModel> call, Throwable t) {
                            Toast.makeText(Page1Activity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            sessonManager.hideProgress();
                        }
                    });

        }
    }

}