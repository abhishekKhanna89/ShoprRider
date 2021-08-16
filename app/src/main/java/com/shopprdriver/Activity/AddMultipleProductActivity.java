package com.shopprdriver.Activity;

import static com.shopprdriver.camerautils.CameraUtils.getOutputMediaFileUri;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Adapter.MultipleProductAdapter;
import com.shopprdriver.Model.MultipleProduct;
import com.shopprdriver.Model.Send.SendModel;
import com.shopprdriver.MyCallBack.OnSelectImageClick;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Server.ApiFactory;
import com.shopprdriver.Server.ApiService;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.app.Progressbar;
import com.shopprdriver.camerautils.ImageCompression;
import com.shopprdriver.camerautils.ImagePath_MarshMallow;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMultipleProductActivity extends AppCompatActivity implements OnSelectImageClick {

    private final int RESULT_CAPTURE_IMAGE = 0;
    private final int TAKE_PHOTO_CODE = 1;
    private final int MY_CAMERA_PERMISSION_CODE = 103;
    private final int MY_GALLERY_PERMISSION_CODE = 104;
    RecyclerView rvMultipleProducts;
    List<MultipleProduct> multipleProductList;
    MultipleProductAdapter multipleProductAdapter;
    int mainPos = 0;
    String filePath = "";
    private Uri fileUri;
    Button submitBtn;
    private static String baseUrl = ApiExecutor.baseUrl;
    SessonManager sessonManager;
    int chat_id;
    Progressbar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_multiple_product);

        rvMultipleProducts = (RecyclerView) findViewById(R.id.rvMultipleProducts);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        multipleProductList = new ArrayList<>();
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();

        MultipleProduct multipleProduct = new MultipleProduct("", "", "", "", false);
        MultipleProduct multipleProduct1 = new MultipleProduct("Select", "", "", "", false);
        multipleProductList.add(multipleProduct);
        multipleProductList.add(multipleProduct1);

        chat_id = getIntent().getIntExtra("chat_id", 0);

        multipleProductAdapter = new MultipleProductAdapter(this, multipleProductList, this);
        LinearLayoutManager linearLayoutManagerMyFavorite = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMultipleProducts.setLayoutManager(linearLayoutManagerMyFavorite);
        rvMultipleProducts.setAdapter(multipleProductAdapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multipleProductList.size() > 1) {
                    if (isValid()) {
                        Toast.makeText(AddMultipleProductActivity.this, "All OK", Toast.LENGTH_SHORT).show();
                        SendAllProducts();
                    } else {
                        Toast.makeText(AddMultipleProductActivity.this, "Please save all data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddMultipleProductActivity.this, "Please add atleast 1 product.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendAllProducts() {
        MultipartBody.Part[] imageArray1 = new MultipartBody.Part[multipleProductList.size() - 1];
        MultipartBody.Part[] producName = new MultipartBody.Part[multipleProductList.size() - 1];
        MultipartBody.Part[] producPrice = new MultipartBody.Part[multipleProductList.size() - 1];
        MultipartBody.Part[] producQuantity = new MultipartBody.Part[multipleProductList.size() - 1];
        for (int i = 0; i < multipleProductList.size() - 1; i++) {
            //RequestBody producNameRequest = RequestBody.create(MediaType.parse("text/plain"), multipleProductList.get(i).getProductName());
            producName[i] = MultipartBody.Part.createFormData("message[]", multipleProductList.get(i).getProductName());
            producPrice[i] = MultipartBody.Part.createFormData("price[]", multipleProductList.get(i).getProductPrice());
            producQuantity[i] = MultipartBody.Part.createFormData("quantity[]", multipleProductList.get(i).getProductQty());

            File file = new File(multipleProductList.get(i).getProductImage());
            File compressedfile = null;
            try {
                compressedfile = new Compressor(AddMultipleProductActivity.this).compressToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
            imageArray1[i] = MultipartBody.Part.createFormData("file[]", compressedfile.getName(), requestBodyArray);
        }

        RequestBody typeReq = RequestBody.create(MediaType.parse("text/plain"), "products");
        RequestBody direction = RequestBody.create(MediaType.parse("text/plain"), "0");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessonManager.getToken());
        ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);

        iApiServices.apiMultipleProductSend(headers, chat_id, producName, typeReq, producPrice, producQuantity, imageArray1, direction)
                .enqueue(new Callback<SendModel>() {
                    @Override
                    public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                        progressbar.hideProgress();
                        //sessonManager.hideProgress();
                        if (response.body() != null) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                setResult(RESULT_OK);
                                finish();
                            }else {
                                Toast.makeText(AddMultipleProductActivity.this,"" +response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SendModel> call, Throwable t) {
                        progressbar.hideProgress();
                        //sessonManager.hideProgress();
                    }
                });

    }

    private boolean isValid() {
        for (MultipleProduct item : multipleProductList.subList(0, multipleProductList.size() - 1)) {
            if (!item.isSaved())
                return false;
        }
        return true;
    }

    @Override
    public void onSelectImageClick(int position) {
        mainPos = position;
        openImageSelectDialog();
    }

    @Override
    public void onAddMoreClicked() {
        MultipleProduct multipleProduct = new MultipleProduct("", "", "", "", false);
        int newItemPos = multipleProductList.size() - 1;
        multipleProductList.add(newItemPos, multipleProduct);
        multipleProductAdapter.notifyItemInserted(newItemPos);
        rvMultipleProducts.scrollToPosition(newItemPos);
    }

    private final void openImageSelectDialog() {
        final Dialog dialog = new Dialog((Context) this);
        dialog.setContentView(R.layout.customediologe1);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window var10001 = (Window) Objects.requireNonNull(dialog.getWindow());
        lp.copyFrom(var10001 != null ? var10001.getAttributes() : null);
        lp.width = -1;
        lp.height = -2;
        lp.gravity = 17;
        Window var10000 = dialog.getWindow();
        Intrinsics.checkNotNull(var10000);
        Intrinsics.checkNotNullExpressionValue(var10000, "dialog.window!!");
        var10000.setAttributes(lp);
        AppCompatButton rl_image_capture = (AppCompatButton) dialog.findViewById(R.id.button_ok);
        AppCompatButton rl_cancel = (AppCompatButton) dialog.findViewById(R.id.button_cancel);
        rl_image_capture.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                dialog.dismiss();
                openCamera();
            }
        }));
        AppCompatButton rl_image_browse = (AppCompatButton) dialog.findViewById(R.id.button_ok1);
        rl_image_browse.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                dialog.dismiss();
                openGallery();
            }
        }));
        rl_cancel.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                dialog.dismiss();
            }
        }));
        dialog.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), "android.permission.CAMERA") == 0 && ActivityCompat.checkSelfPermission(this.getApplicationContext(), "android.permission.READ_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(this.getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            captureImage();
        } else {
            ActivityCompat.requestPermissions((Activity) this, new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, MY_CAMERA_PERMISSION_CODE);
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = getOutputMediaFileUri(this);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        } else {
            fileUri = getOutputMediaFileUri(this);
            File file = new File(fileUri.getPath());
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission((Context) this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            browseImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, MY_GALLERY_PERMISSION_CODE);
        }
    }

    private final void browseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), TAKE_PHOTO_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0) {
                captureImage();
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == this.MY_GALLERY_PERMISSION_CODE) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "gallery permission granted", Toast.LENGTH_SHORT).show();
                browseImage();
            } else {
                Toast.makeText(this, "gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String imagePath = "";
        switch (requestCode) {
            case TAKE_PHOTO_CODE: {
                try {

                    Uri selectedImage = data != null ? data.getData() : null;
                    if (Build.VERSION.SDK_INT > 22) {
                        imagePath = ImagePath_MarshMallow.getPath(this, selectedImage);
                    } else {
                        filePath = selectedImage.getPath();
                    }

                    ImageCompression imageCompression = new ImageCompression(this);
                    filePath = imageCompression.compressImage(imagePath);
                    multipleProductList.get(mainPos).setProductImage(filePath);
                    multipleProductList.get(mainPos).setSaved(false);
                    multipleProductAdapter.notifyItemChanged(mainPos);
                } catch (Exception var12) {
                    var12.printStackTrace();
                }
            }
            break;
            case RESULT_CAPTURE_IMAGE: {
                try {
                    if (Build.VERSION.SDK_INT > 22) {
                        imagePath = ImagePath_MarshMallow.getPath((Context) this, this.fileUri);
                    } else {
                        imagePath = fileUri.getPath();
                    }

                    ImageCompression imageCompression = new ImageCompression((Context) this);
                    filePath = imageCompression.compressImage(imagePath);
                    multipleProductList.get(mainPos).setProductImage(filePath);
                    multipleProductList.get(mainPos).setSaved(false);
                    multipleProductAdapter.notifyItemChanged(mainPos);
                } catch (Exception var11) {
                    var11.printStackTrace();
                }
            }
            break;
        }
    }
}