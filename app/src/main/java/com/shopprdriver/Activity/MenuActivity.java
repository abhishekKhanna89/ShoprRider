package com.shopprdriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.CheckinCheckouSucess.CheckinCheckouSucessModel;
import com.shopprdriver.Model.CheckoutStatus.CheckoutStatusModel;
import com.shopprdriver.Model.Menu_Model;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.CheckInCheckOutRequest;
import com.shopprdriver.SendBird.utils.ToastUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.background_service.UpdateLocationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    RecyclerView menuRecyclerView;
    SessonManager sessonManager;
    public static String checkout;


    // lists for permissions
    /*Todo:- Check out type*/
    String check_out_type;

    //String latitude,longitude,location_address;


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    String location_address;

    List<Menu_Model>menuModelList;




    public MenuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sessonManager=new SessonManager(this);
        //check_out_type=getIntent().getStringExtra("check_out_type");


        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        checkPermissions();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else{
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
        }

       startService(new Intent(this, UpdateLocationService.class));


        menuModelList=new ArrayList<>();

        viewCheckoutStatus();

    }

    private void viewCheckoutStatus() {
        Call<CheckoutStatusModel> call= ApiExecutor.getApiService(MenuActivity.this)
                .apiCheckoutStatus("Bearer "+sessonManager.getToken());
        call.enqueue(new Callback<CheckoutStatusModel>() {
            @Override
            public void onResponse(Call<CheckoutStatusModel> call, Response<CheckoutStatusModel> response) {
               // pDialog.dismiss();
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        CheckoutStatusModel checkoutStatusModel=response.body();
                        if (checkoutStatusModel!=null){
                            check_out_type=checkoutStatusModel.getType();

                            viewMenu();

                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<CheckoutStatusModel> call, Throwable t) {
               // pDialog.dismiss();
            }
        });
    }

    private void viewMenu() {
        if (check_out_type !=null&& check_out_type.equalsIgnoreCase("checkin")){
            checkout="Check out";
        }else if (check_out_type !=null&& check_out_type.equalsIgnoreCase("checkout")){
            checkout="Check in";
        }
        menuModelList.clear();
        menuModelList.add(new Menu_Model("Notification"));
        menuModelList.add(new Menu_Model("Past Order"));
        menuModelList.add(new Menu_Model("Wallet Transaction"));
        menuModelList.add(new Menu_Model("Commission Transaction"));
        menuModelList.add(new Menu_Model("Chat History"));
        menuModelList.add(new Menu_Model("Open Order"));
        menuModelList.add(new Menu_Model(checkout));
        menuModelList.add(new Menu_Model("Attendance"));
        menuModelList.add(new Menu_Model("Profile"));
        menuModelList.add(new Menu_Model("Reviews"));

        Menu_Adapter menu_adapter = new Menu_Adapter(MenuActivity.this, menuModelList);
        menuRecyclerView.setAdapter(menu_adapter);
        menu_adapter.notifyDataSetChanged();
    }

    public class Menu_Adapter extends RecyclerView.Adapter<Menu_Adapter.Holder> {
        List<Menu_Model>menuModelList;
        Context context;

        public Menu_Adapter(Context context, List<Menu_Model>menuModelLists) {
            this.context = context;
            this.menuModelList = menuModelLists;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_menu, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Menu_Model menu_model = menuModelList.get(position);
            holder.menu_title.setText(menu_model.getMenuName());
            viewCount(position,holder);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        startActivity(new Intent(context, NotificationListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 1) {
                        startActivity(new Intent(context, MyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 2) {
                        startActivity(new Intent(context, WalletTransactionActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 3) {
                        startActivity(new Intent(context, CommissionTransactionActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 4) {
                        startActivity(new Intent(context, ChatHistoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 5) {
                        startActivity(new Intent(context,MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 6) {
                        if (CommonUtils.isOnline(MenuActivity.this)) {
                            KAlertDialog pDialog = new KAlertDialog(MenuActivity.this, KAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Loading");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            CheckInCheckOutRequest checkInCheckOutRequest=new CheckInCheckOutRequest();
                            checkInCheckOutRequest.setLat(String.valueOf(mLastLocation.getLatitude()));
                            checkInCheckOutRequest.setLang(String.valueOf(mLastLocation.getLongitude()));
                            checkInCheckOutRequest.setAddress(location_address);

                            if (menu_model.getMenuName().equalsIgnoreCase("Check in")){
                                Call<CheckinCheckouSucessModel> call= ApiExecutor.getApiService(MenuActivity.this)
                                        .apiCheckIn("Bearer "+sessonManager.getToken(),checkInCheckOutRequest);
                                call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                                    @Override
                                    public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                                        pDialog.dismiss();
                                        if (response.body()!=null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                Toast.makeText(MenuActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                viewCheckoutStatus();
                                            }else {
                                                Toast.makeText(MenuActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                                        pDialog.dismiss();
                                    }
                                });
                            }else if (menu_model.getMenuName().equalsIgnoreCase("Check out")){
                                Call<CheckinCheckouSucessModel> call= ApiExecutor.getApiService(MenuActivity.this)
                                        .apiCheckOut("Bearer "+sessonManager.getToken(),checkInCheckOutRequest);
                                call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                                    @Override
                                    public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                                        pDialog.dismiss();
                                        if (response.body()!=null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                Toast.makeText(MenuActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                viewCheckoutStatus();
                                            }else {
                                                Toast.makeText(MenuActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                                        pDialog.dismiss();
                                    }
                                });
                            }


                        }else {
                            CommonUtils.showToastInCenter(MenuActivity.this, getString(R.string.please_check_network));
                        }
                    } else if (position == 7) {
                        startActivity(new Intent(context, AttendenceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    } else if (position == 8) {


                    } else if (position == 9) {
                        startActivity(new Intent(context, ReviewsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }


            });


            if (position == 0) {
                //holder.countText.setVisibility(View.VISIBLE);
                holder.llMainView.setBackgroundColor(Color.parseColor("#cd644c"));
            } else if (position == 1) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#d26088"));
            } else if (position == 2) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#3e77da"));
            } else if (position == 3) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#8fb464"));
            } else if (position == 4) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#d99e4a"));
            } else if (position == 5) {
                //holder.countText.setVisibility(View.VISIBLE);
                holder.llMainView.setBackgroundColor(Color.parseColor("#d94646"));
            } else if (position == 6) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#91be55"));
            } else if (position == 7) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#d94646"));
            } else if (position == 8) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#2fc8a3"));
            } else if (position == 9) {
                holder.llMainView.setBackgroundColor(Color.parseColor("#F8A9A3"));
            }

        }

        @Override
        public int getItemCount() {
            return menuModelList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView menu_title,countText;
            LinearLayout llMainView;

            public Holder(@NonNull View itemView) {
                super(itemView);
                menu_title = itemView.findViewById(R.id.menu_title);
                llMainView = itemView.findViewById(R.id.llMainView);
                countText=itemView.findViewById(R.id.countText);
            }
        }

    }

    private void viewCount(int position, Menu_Adapter.Holder holder) {
        Call<CheckoutStatusModel> call= ApiExecutor.getApiService(MenuActivity.this)
                .apiCheckoutStatus("Bearer "+sessonManager.getToken());
        call.enqueue(new Callback<CheckoutStatusModel>() {
            @Override
            public void onResponse(Call<CheckoutStatusModel> call, Response<CheckoutStatusModel> response) {
                // pDialog.dismiss();
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        CheckoutStatusModel checkoutStatusModel=response.body();
                        if (checkoutStatusModel!=null){
                            if (position==0){
                                if (checkoutStatusModel.getNotifications().equalsIgnoreCase("0")){
                                    holder.countText.setVisibility(View.GONE);
                                }else {
                                    holder.countText.setVisibility(View.VISIBLE);
                                    holder.countText.setText(checkoutStatusModel.getNotifications());
                                }
                            }else if (position==5){
                                if (checkoutStatusModel.getOrders().equalsIgnoreCase("0")){
                                    holder.countText.setVisibility(View.GONE);
                                }else {
                                    holder.countText.setVisibility(View.VISIBLE);
                                    holder.countText.setText(checkoutStatusModel.getOrders());
                                }
                            }


                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<CheckoutStatusModel> call, Throwable t) {
                // pDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu_change_language=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_ot_menu, menu);
        return true;
    }
    public void logout(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessonManager.setToken("");
                        Toast.makeText(MenuActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                        finishAffinity();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MenuActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            /*Getting the location after aquiring location service*/
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {

                    Geocoder geocoder = new Geocoder(MenuActivity.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = list.get(0);
                    String localitys = address.getLocality();
                    location_address = address.getAddressLine(0);
                    //Log.d("AAAAA",""+mLastLocation);
                //_progressBar.setVisibility(View.INVISIBLE);
               // _latitude.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()));
                //_longitude.setText("Longitude: " + String.valueOf(mLastLocation.getLongitude()));
            } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                 * So we will get the current location.
                 * For this we'll implement Location Listener and override onLocationChanged*/
                //Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MenuActivity.this);
            }
        }
    }

    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("BBBBB",""+location);
        mLastLocation = location;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewCheckoutStatus();
    }
}