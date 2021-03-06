package com.shopprdriver.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.CheckinCheckouSucess.CheckinCheckouSucessModel;
import com.shopprdriver.Model.CheckoutStatus.CheckoutStatusModel;
import com.shopprdriver.Model.Logout.LogoutModel;
import com.shopprdriver.Model.Menu_Model;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.CheckInCheckOutRequest;
import com.shopprdriver.SendBird.utils.AuthenticationUtils;
import com.shopprdriver.SendBird.utils.PrefUtils;
import com.shopprdriver.SendBird.utils.ToastUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.app.Progressbar;
import com.shopprdriver.background_service.UpdateLocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final int REQUEST_LOC_PERMISSIONS_REQUEST_CODE = 122;
    RecyclerView menuRecyclerView;
    SessonManager sessonManager;
    public static String checkout;
    Intent serviceIntent;
    LocationListener loclis;

    // lists for permissions
    /*Todo:- Check out type*/
    String check_out_type;
    List<Menu_Model> menuModelList = new ArrayList<>();

    /*Todo:- Current Location*/
    boolean gpsCheck = false;
    Location mLastLocation;
    String location_address;
    LocationManager locman;

    public MenuActivity() {
    }

    Progressbar progressbar;

    /*Todo:- Version Check*/
    String VERSION_URL = ApiExecutor.baseUrl + "app-version";
    String sCurrentVersion;
    int hoursmilllisecond = 86400000;
    int value = 0, savedMillistime;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("Shopper Rider");
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        Log.d("Token", sessonManager.getToken());
        //check_out_type=getIntent().getStringExtra("check_out_type");
        checkPermissions();

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        Log.d("notifiallowed=", String.valueOf(NotificationManagerCompat.from(MenuActivity.this).areNotificationsEnabled()));

        NotificationManager manager = (NotificationManager) MenuActivity.this.getSystemService(MenuActivity.this.NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = manager.getImportance();
        }
        boolean soundAllowed = importance < 0 || importance >= NotificationManager.IMPORTANCE_DEFAULT;

        Log.d("soundAllowed=", String.valueOf(soundAllowed));


        if (String.valueOf(NotificationManagerCompat.from(MenuActivity.this).areNotificationsEnabled()).equals("false")) {

            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MenuActivity.this).create();
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please update sound,notification  lockscreen,floating notification setting to be better use");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, MenuActivity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", MenuActivity.this.getPackageName());
                                intent.putExtra("app_uid", MenuActivity.this.getApplicationInfo().uid);
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + MenuActivity.this.getPackageName()));
                            }
                            MenuActivity.this.startActivity(intent);


                            dialog.dismiss();
                        }
                    });

            alertDialog.show();

        }

        serviceIntent = new Intent(this, UpdateLocationService.class);

        viewCheckoutStatus();
        /*Todo:- Version Check*/
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            //String version = pInfo.versionName;
            sCurrentVersion = pInfo.versionName;
            Log.d("versionName", sCurrentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(sessonManager.getCurrenttime()) > 0) {
            value = Integer.parseInt(sessonManager.getCurrenttime());
        } else {
            value = 0;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        if (sessonManager.getCurrenttime().length() > 0) {
            // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
            value = Integer.parseInt(sessonManager.getCurrenttime());
            Log.d("hellovalueshared===", String.valueOf(value));
            String currentDateandTime = sdf.format(new Date());
            int savedMillis = (int) System.currentTimeMillis();
            int valuemus = (savedMillis - value);
            Log.d("valueminus", String.valueOf(valuemus));
            //Log.d("savemilsaecttime===", String.valueOf(savedMillis) + "," + value + "," + hoursmilllisecond + "," + valuemus);
            if (valuemus >= hoursmilllisecond) {
                appCheckVersionApi();
            }
        }

        locman = (LocationManager) getSystemService(LOCATION_SERVICE);
        loclis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    mLastLocation = location;
                    Geocoder geocoder = new Geocoder(MenuActivity.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = list.get(0);
                    //String localitys = address.getLocality();
                    location_address = address.getAddressLine(0);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }


            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 111);
            return;
        }

        new CountDownTimer(0, 0) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onFinish() {
                if (ActivityCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOC_PERMISSIONS_REQUEST_CODE);
                    }

                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOC_PERMISSIONS_REQUEST_CODE);
                    return;
                }
                locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, loclis);
            }
        }.start();

    }

    private void viewCheckoutStatus() {
        progressbar.showProgress(this);
        Call<CheckoutStatusModel> call = ApiExecutor.getApiService(MenuActivity.this)
                .apiCheckoutStatus("Bearer " + sessonManager.getToken());
        call.enqueue(new Callback<CheckoutStatusModel>() {
            @Override
            public void onResponse(Call<CheckoutStatusModel> call, Response<CheckoutStatusModel> response) {
                progressbar.hideProgress();
                // pDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        CheckoutStatusModel checkoutStatusModel = response.body();
                        if (checkoutStatusModel != null) {
                            check_out_type = checkoutStatusModel.getType();
                            viewMenu();
                        } else {
                            Toast.makeText(MenuActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.body().getStatus().equalsIgnoreCase("failed")) {
                            if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                AuthenticationUtils.deauthenticate(MenuActivity.this, isSuccess -> {
                                    if (getApplication() != null) {
                                        stopService(serviceIntent);
                                        sessonManager.setToken("");
                                        PrefUtils.setAppId(MenuActivity.this, "");
                                        Toast.makeText(MenuActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                                        finishAffinity();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckoutStatusModel> call, Throwable t) {
                progressbar.hideProgress();
                // pDialog.dismiss();
            }
        });
    }

    private void viewMenu() {
        if (check_out_type != null && check_out_type.equalsIgnoreCase("checkin")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            checkout = "Check out";
        } else if (check_out_type != null && check_out_type.equalsIgnoreCase("checkout")) {
            checkout = "Check in";
            stopService(serviceIntent);
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
        menuModelList.add(new Menu_Model("Traveling Details"));

        Menu_Adapter menu_adapter = new Menu_Adapter(MenuActivity.this, menuModelList);
        menuRecyclerView.setAdapter(menu_adapter);
        menu_adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            gpsCheck = locman.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gpsCheck) {
            EnableGPSAutoMatically();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 152);
        } else {

        }
    }

    /*Todo:- Current Location*/
    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(MenuActivity.this)
                    .addOnConnectionFailedListener(MenuActivity.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); // this is the key ingredient


            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MenuActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                gpsCheck = true;
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                gpsCheck = false;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.d("MA", "Window has been closed");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("GPS", gpsCheck);
        super.onSaveInstanceState(savedInstanceState);
    }


    public class Menu_Adapter extends RecyclerView.Adapter<Menu_Adapter.Holder> {
        List<Menu_Model> menuModelList;
        Context context;

        public Menu_Adapter(Context context, List<Menu_Model> menuModelLists) {
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
            viewCount(position, holder);


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
                        startActivity(new Intent(context, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 6) {
                        if (CommonUtils.isOnline(MenuActivity.this)) {
                            KAlertDialog pDialog = new KAlertDialog(MenuActivity.this, KAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Loading");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            CheckInCheckOutRequest checkInCheckOutRequest = new CheckInCheckOutRequest();
                            if (mLastLocation != null) {
                                checkInCheckOutRequest.setLat(String.valueOf(mLastLocation.getLatitude()));
                                checkInCheckOutRequest.setLang(String.valueOf(mLastLocation.getLongitude()));
                                checkInCheckOutRequest.setAddress(location_address);
                            } else {
                                Toast.makeText(MenuActivity.this, "We are fetching your location.Please wait....", Toast.LENGTH_SHORT).show();
                            }

                            if (menu_model.getMenuName().equalsIgnoreCase("Check in")) {
                                Call<CheckinCheckouSucessModel> call = ApiExecutor.getApiService(MenuActivity.this)
                                        .apiCheckIn("Bearer " + sessonManager.getToken(), checkInCheckOutRequest);
                                call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                                    @Override
                                    public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                                        pDialog.dismiss();
                                        if (response.body() != null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                Toast.makeText(MenuActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                viewCheckoutStatus();
                                            } else {
                                                Toast.makeText(MenuActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                                        pDialog.dismiss();
                                    }
                                });
                            } else if (menu_model.getMenuName().equalsIgnoreCase("Check out")) {
                                Call<CheckinCheckouSucessModel> call = ApiExecutor.getApiService(MenuActivity.this)
                                        .apiCheckOut("Bearer " + sessonManager.getToken(), checkInCheckOutRequest);
                                call.enqueue(new Callback<CheckinCheckouSucessModel>() {
                                    @Override
                                    public void onResponse(Call<CheckinCheckouSucessModel> call, Response<CheckinCheckouSucessModel> response) {
                                        pDialog.dismiss();
                                        if (response.body() != null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                Toast.makeText(MenuActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                viewCheckoutStatus();
                                            } else {
                                                Toast.makeText(MenuActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckinCheckouSucessModel> call, Throwable t) {
                                        pDialog.dismiss();
                                    }
                                });
                            }


                        } else {
                            CommonUtils.showToastInCenter(MenuActivity.this, getString(R.string.please_check_network));
                        }
                    } else if (position == 7) {
                        startActivity(new Intent(context, AttendenceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 8) {
                        startActivity(new Intent(context, AccountInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 9) {
                        startActivity(new Intent(context, ReviewsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else if (position == 10) {
                        startActivity(new Intent(context, TravelingDetailsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }


            });

        }

        @Override
        public int getItemCount() {
            return menuModelList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView menu_title, countText;
            LinearLayout llMainView;

            public Holder(@NonNull View itemView) {
                super(itemView);
                menu_title = itemView.findViewById(R.id.menu_title);
                llMainView = itemView.findViewById(R.id.llMainView);
                countText = itemView.findViewById(R.id.countText);
            }
        }

    }

    private void viewCount(int position, Menu_Adapter.Holder holder) {
        Call<CheckoutStatusModel> call = ApiExecutor.getApiService(MenuActivity.this)
                .apiCheckoutStatus("Bearer " + sessonManager.getToken());
        call.enqueue(new Callback<CheckoutStatusModel>() {
            @Override
            public void onResponse(Call<CheckoutStatusModel> call, Response<CheckoutStatusModel> response) {
                // pDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        CheckoutStatusModel checkoutStatusModel = response.body();
                        if (checkoutStatusModel != null) {
                            if (position == 0) {
                                if (checkoutStatusModel.getNotifications().equalsIgnoreCase("0")) {
                                    holder.countText.setVisibility(View.GONE);
                                } else {
                                    holder.countText.setVisibility(View.VISIBLE);
                                    holder.countText.setText(checkoutStatusModel.getNotifications());
                                }
                            } else if (position == 5) {
                                if (checkoutStatusModel.getOrders().equalsIgnoreCase("0")) {
                                    holder.countText.setVisibility(View.GONE);
                                } else {
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
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<LogoutModel> call = ApiExecutor.getApiService(MenuActivity.this)
                                .apiLogoutStatus("Bearer " + sessonManager.getToken());
                        call.enqueue(new Callback<LogoutModel>() {
                            @Override
                            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                if (response.body() != null) {
                                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                        AuthenticationUtils.deauthenticate(MenuActivity.this, isSuccess -> {
                                            if (getApplication() != null) {
                                                stopService(serviceIntent);
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(MenuActivity.this, "");
                                                Toast.makeText(MenuActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                                                finishAffinity();
                                            }
                                        });
                                        //Toast.makeText(MenuActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        AuthenticationUtils.deauthenticate(MenuActivity.this, isSuccess -> {
                                            if (getApplication() != null) {
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(MenuActivity.this, "");
                                                Toast.makeText(MenuActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                                                finishAffinity();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<LogoutModel> call, Throwable t) {

                            }
                        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(this, "Permission denied.");
            }
        } else if (requestCode == REQUEST_LOC_PERMISSIONS_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, loclis);
            }
            return;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("lakshmi", String.valueOf(NotificationManagerCompat.from(MenuActivity.this).areNotificationsEnabled()));

        if (String.valueOf(NotificationManagerCompat.from(MenuActivity.this).areNotificationsEnabled()).equals("false")) {
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MenuActivity.this).create();
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please update sound,notification  lockscreen,floating notification setting to be better use");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (String.valueOf(NotificationManagerCompat.from(MenuActivity.this).areNotificationsEnabled()).equals("false")) {

                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, MenuActivity.this.getPackageName());
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("app_package", MenuActivity.this.getPackageName());
                                    intent.putExtra("app_uid", MenuActivity.this.getApplicationInfo().uid);
                                } else {
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + MenuActivity.this.getPackageName()));
                                }
                                MenuActivity.this.startActivity(intent);
                            }

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


        }


        viewCheckoutStatus();
    }

    private void appCheckVersionApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, VERSION_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response====", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("responce===", jsonObject + "");
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String androidversion = jsonObject1.getString("rider_version");
                        //Log.d("androidversion", androidversion);
                        // sCurrentVersion = defaultConfig.VERSION_NAME;
                        //  Toast.makeText(getActivity(), "" + sCurrentVersion, Toast.LENGTH_SHORT).show();
                        //Log.d("scureentVersion==", sCurrentVersion);
                        //Log.d("scureentserverVersion==", androidversion);
                        if (androidversion.equalsIgnoreCase(sCurrentVersion)) {

                        } else {
                            showDialouge();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", "Bearer " + sessonManager.getToken());
                return headerMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void showDialouge() {
        new AlertDialog.Builder(this)
                .setTitle("Upgrade App")
                .setMessage(getResources().getString(R.string.force_update_app_message))
                .setPositiveButton("Update App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPlayStore();
                        dialog.dismiss();
                        savedMillistime = (int) System.currentTimeMillis();
                        sessonManager.setCurrenttime(String.valueOf(savedMillistime));
                        Log.d("helloTimemills", String.valueOf(savedMillistime));
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savedMillistime = (int) System.currentTimeMillis();
                        sessonManager.setCurrenttime(String.valueOf(savedMillistime));
                        Log.d("helloTimemills", String.valueOf(savedMillistime));
                        dialog.dismiss();
                    }
                })
                .show();

    }


    private void openPlayStore() {
        if (getApplication() == null) {
            return;
        }
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}