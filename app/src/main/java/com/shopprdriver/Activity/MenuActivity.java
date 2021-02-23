package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.shopprdriver.MainActivity;
import com.shopprdriver.Model.CheckinCheckouSucess.CheckinCheckouSucessModel;
import com.shopprdriver.Model.Menu_Model;
import com.shopprdriver.R;
import com.shopprdriver.RequestService.CheckInCheckOutRequest;
import com.shopprdriver.SendBird.utils.ToastUtils;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.shopprdriver.background_service.MyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    /*Todo:- Background Service*/
    private JobScheduler jobScheduler;
    private ComponentName componentName;
    private JobInfo jobInfo;

    RecyclerView menuRecyclerView;
    SessonManager sessonManager;
    String checkout;
    /*Todo:- Address*/
    private Location location;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    /*Todo:- Check out type*/
    String check_out_type;
    String latitude,longitude,location_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sessonManager=new SessonManager(this);
        check_out_type=getIntent().getStringExtra("check_out_type");

        /*Todo:- Get Address*/
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        viewMenu();

    }

    private void viewMenu() {
        if (check_out_type!=null&&check_out_type.equalsIgnoreCase("checkout")||sessonManager.getCheckout_Status().equalsIgnoreCase("checkout")){
            checkout="Check in";
            //new Menu_Model("Check in");
        }else if (check_out_type!=null&&check_out_type.equalsIgnoreCase("checkin")||sessonManager.getCheckout_Status().equalsIgnoreCase("checkin")){
            checkout="Check out";
            //new Menu_Model("Check out");
        }
        Menu_Model[] menuModelList = new Menu_Model[]{
                new Menu_Model("Notification"),
                new Menu_Model("Past Order"),
                new Menu_Model("Wallet Transaction"),
                new Menu_Model("Commission Transaction"),
                new Menu_Model("Chat History"),
                new Menu_Model("Open Order"),
                new Menu_Model(checkout),
                new Menu_Model("Attendance"),
                new Menu_Model("Profile"),
                new Menu_Model("Reviews"),

        };

        Menu_Adapter menu_adapter = new Menu_Adapter(MenuActivity.this, menuModelList);
        menuRecyclerView.setAdapter(menu_adapter);
        menu_adapter.notifyDataSetChanged();
    }

    public class Menu_Adapter extends RecyclerView.Adapter<Menu_Adapter.Holder> {
        Menu_Model[] menu_models;
        Context context;

        public Menu_Adapter(Context context, Menu_Model[] menu_models) {
            this.context = context;
            this.menu_models = menu_models;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_menu, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Menu_Model menu_model = menu_models[position];
            holder.menu_title.setText(menu_model.getMenuName());
           /* if (menu_model.getMenuName().equals("Check in")){
                holder.menu_title.setText(menu_model.getMenuName());
            }else if (menu_model.getMenuName().equals("Check out")){
                holder.menu_title.setText(menu_model.getMenuName());
            }*/
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
                            checkInCheckOutRequest.setLat(latitude);
                            checkInCheckOutRequest.setLang(longitude);
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
                                                //holder.menu_title.setText("Check out");
                                               // holder.menu_title.setText("Check out");

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
                                                //holder.menu_title.setText("Check in");
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
            return menu_models.length;
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView menu_title;
            LinearLayout llMainView;

            public Holder(@NonNull View itemView) {
                super(itemView);
                menu_title = itemView.findViewById(R.id.menu_title);
                llMainView = itemView.findViewById(R.id.llMainView);
            }
        }
    }

    /*Todo: - Location*/
    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }*/
    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            //addressText.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }else {
            showGPSDisabledAlertToUser();
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude= String.valueOf(location.getLatitude());
            longitude= String.valueOf(location.getLongitude());
            Geocoder geocoder = new Geocoder(MenuActivity.this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = list.get(0);
            String localitys = address.getLocality();
            location_address = address.getAddressLine(0);

            // addressText.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
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

}