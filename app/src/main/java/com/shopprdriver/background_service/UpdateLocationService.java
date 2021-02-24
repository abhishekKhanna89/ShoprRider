package com.shopprdriver.background_service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.shopprdriver.Model.LocationUpdateModel;
import com.shopprdriver.Model.UpdateLocationRequest;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLocationService  extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    /*Todo:- Location Manager*/
    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    /*Todo:- Session*/
    SessonManager sessonManager;
    @Override
    public void onCreate() {
        sessonManager=new SessonManager(this);
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

   /* @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

/*Todo:- Location Service*/

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

        }

        startLocationUpdates();
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
            sendLocation(location);
           /* handler = new Handler();
            runnable = new Runnable() {
                public void run() {

                    handler.postDelayed(runnable, 3000);
                }
            };
            handler.postDelayed(runnable, 5000);*/

        }
    }

    public void sendLocation(Location location) {

        //Toast.makeText(context, ""+location, Toast.LENGTH_SHORT).show();

        UpdateLocationRequest updateLocationRequest=new UpdateLocationRequest();
        updateLocationRequest.setLat(location.getLatitude());
        updateLocationRequest.setLang(location.getLongitude());

        Call<LocationUpdateModel> call= ApiExecutor.getApiService(this)
                .apiLocationUpdate("Bearer "+sessonManager.getToken(),updateLocationRequest);
        call.enqueue(new Callback<LocationUpdateModel>() {
            @Override
            public void onResponse(Call<LocationUpdateModel> call, Response<LocationUpdateModel> response) {
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        //Toast.makeText(UpdateLocationService.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }else {
                        //Toast.makeText(UpdateLocationService.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationUpdateModel> call, Throwable t) {

            }
        });
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


}
