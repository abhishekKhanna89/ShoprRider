package com.shopprdriver.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shopprdriver.Model.UploadDocument.UploadDocumentModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_IMAGE = 2;
    ImageView frontAadhar, backAadhar,
            frontDlno, backDlno,frontBke,backBike, panCard;


    Bitmap bitmapAadharFront;
    String select;

    private static String baseUrl = "http://shoppr.avaskmcompany.xyz/api/shoppr/";
    SessonManager sessonManager;
    byte[] byteArrayAdharFront,byteArrayAdharBack,
            byteArrayDlNoFront,byteArrayDlNoBack,byteArrayFrontBike,byteArrayBakeBike,byteArrayPan;
    File finalFile;
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
    public void frontBake(View view) {
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
                                showGallery6();
                                break;
                            case 1:
                                showCamera6();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera6() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "6";
    }

    private void showGallery6() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "6";
    }

    public void backBike(View view) {
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
                                showGallery7();
                                break;
                            case 1:
                                showCamera7();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void showCamera7() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        select = "7";
    }

    private void showGallery7() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        select = "7";
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayAdharFront = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                 finalFile = new File(getRealPathFromURI(tempUri));

                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        frontAadhar.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayAdharFront=stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                         finalFile = new File(getRealPathFromURI(tempUri));
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayAdharBack = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                 finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        backAadhar.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayAdharBack = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                         finalFile = new File(getRealPathFromURI(tempUri));
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayDlNoFront = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                 finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        frontDlno.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayDlNoFront = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                         finalFile = new File(getRealPathFromURI(tempUri));
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayDlNoBack = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                 finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        backDlno.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayDlNoBack = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                         finalFile = new File(getRealPathFromURI(tempUri));
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayPan = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                 finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        panCard.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayPan = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                         finalFile = new File(getRealPathFromURI(tempUri));
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (select.equalsIgnoreCase("6")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                frontBke.setImageBitmap(bitmapAadharFront);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayFrontBike = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        frontBke.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayFrontBike = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        finalFile = new File(getRealPathFromURI(tempUri));
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (select.equalsIgnoreCase("7")) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmapAadharFront = (Bitmap) extras.get("data");
                bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                backBike.setImageBitmap(bitmapAadharFront);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayBakeBike = stream.toByteArray();
                Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                finalFile = new File(getRealPathFromURI(tempUri));
                //savebitmap(bitmapAadharFront);
            }

            if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        bitmapAadharFront = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmapAadharFront = Bitmap.createScaledBitmap(bitmapAadharFront, 800, 800, false);
                        backBike.setImageBitmap(bitmapAadharFront);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapAadharFront.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArrayBakeBike = stream.toByteArray();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmapAadharFront);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        finalFile = new File(getRealPathFromURI(tempUri));
                        //savebitmap(bitmapAadharFront);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void submit(View view) {
        if (byteArrayAdharBack.length == 0) {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        } else if (byteArrayAdharFront.length == 0) {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        } else if (byteArrayDlNoFront.length == 0) {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        } else if (byteArrayDlNoBack.length == 0) {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        } else if (byteArrayPan.length==0) {
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        } else if(byteArrayFrontBike.length==0){
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
        }else if (byteArrayBakeBike.length==0){
            Toast.makeText(this, "Please select all image", Toast.LENGTH_SHORT).show();
        }else {
            uploadDocument();
        }
    }

    private void uploadDocument() {
        if (CommonUtils.isOnline(Page1Activity.this)) {
            sessonManager.showProgress(Page1Activity.this);
            RequestBody frontBike = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayFrontBike);
            MultipartBody.Part filePartFrontBike = MultipartBody.Part.createFormData("bike_front", finalFile.getName(), frontBike);

            RequestBody backBike = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayBakeBike);
            MultipartBody.Part filePartBackBike = MultipartBody.Part.createFormData("bike_back", finalFile.getName(), backBike);


            RequestBody pan_card = RequestBody
                    .create(MediaType.parse("image/*"),byteArrayPan);
            MultipartBody.Part filePartPan = MultipartBody.Part.createFormData("pan_card", finalFile.getName(), pan_card);
            RequestBody front_aadhaar_card = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayAdharFront);
            MultipartBody.Part filePartAdharFront = MultipartBody.Part.createFormData("front_aadhaar_card", finalFile.getName(), front_aadhaar_card);


            RequestBody back_aadhaar_card = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayAdharBack);
            MultipartBody.Part filePartAdharBack = MultipartBody.Part.createFormData("back_aadhaar_card", finalFile.getName(), back_aadhaar_card);
            RequestBody front_dl_no = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayDlNoFront);
            MultipartBody.Part filePartDlFront = MultipartBody.Part.createFormData("front_dl_no", finalFile.getName(), front_dl_no);
            RequestBody back_dl_no = RequestBody
                    .create(MediaType.parse("image/*"), byteArrayDlNoBack);
            MultipartBody.Part filePartback_dl_no = MultipartBody.Part.createFormData("back_dl_no", finalFile.getName(), back_dl_no);

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