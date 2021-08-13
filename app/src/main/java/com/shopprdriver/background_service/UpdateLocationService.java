package com.shopprdriver.background_service;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shopprdriver.Activity.MenuActivity;
import com.shopprdriver.Model.LocationUpdateModel;
import com.shopprdriver.Model.UpdateLocationRequest;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.SessonManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public Context context = this;
    /*Todo:- Location Manager*/
    private Location location;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 10000, FASTEST_INTERVAL = 10000;
    private static final float DISTANCE_INTERVAL = 10f;
    Handler handler;
    Runnable runnable;
    /*Todo:- Session*/
    SessonManager sessonManager;
    ArrayList<LatLng> dvrLoc = new ArrayList();
    int i = 0;

    @Override
    public void onCreate() {
        sessonManager = new SessonManager(this);
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        dvrLoc.add(new LatLng(28.565532, 77.244447));
        dvrLoc.add(new LatLng(28.566052, 77.244417));
        dvrLoc.add(new LatLng(28.566918, 77.244311));
        dvrLoc.add(new LatLng(28.567478, 77.243068));
        dvrLoc.add(new LatLng(28.568139, 77.241460));
        dvrLoc.add(new LatLng(28.568677, 77.240057));
        dvrLoc.add(new LatLng(28.569002, 77.239112));
        dvrLoc.add(new LatLng(28.568094, 77.239342));
        dvrLoc.add(new LatLng(28.567310, 77.239687));
        dvrLoc.add(new LatLng(28.566627, 77.239980));
        dvrLoc.add(new LatLng(28.565988, 77.240184));
        dvrLoc.add(new LatLng(28.565797, 77.239763));
        dvrLoc.add(new LatLng(28.565898, 77.238908));
        dvrLoc.add(new LatLng(28.566268, 77.238653));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        prepareForegroundNotification();
        return START_STICKY;
    }

    private void prepareForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "LocationChannel",
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Intent notificationIntent = new Intent(this, MenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "LocationChannel")
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle("Getting Location")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.splash)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        startForeground(52, notification);
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
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
               ) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            //addressText.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        } else {

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

            /*handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    sendLocation(location);
                    handler.postDelayed(runnable, 10000);
                }
            };
            handler.postDelayed(runnable, 10000);*/

        }
    }

    public void sendLocation(Location location) {
    /*    Log.d("locationUpdate",""+location);
        Log.d("locationUpdate","position: "+i);
*/
        UpdateLocationRequest updateLocationRequest = new UpdateLocationRequest();
        updateLocationRequest.setLat(location.getLatitude());
        updateLocationRequest.setLang(location.getLongitude());

        /*if (i<14) {
            updateLocationRequest.setLat(dvrLoc.get(i).latitude);
            updateLocationRequest.setLang(dvrLoc.get(i).longitude);
            i++;
        }else
            i=0;*/

        Call<LocationUpdateModel> call = ApiExecutor.getApiService(this)
                .apiLocationUpdate("Bearer " + sessonManager.getToken(), updateLocationRequest);
        call.enqueue(new Callback<LocationUpdateModel>() {
            @Override
            public void onResponse(Call<LocationUpdateModel> call, Response<LocationUpdateModel> response) {
                if (response.body() != null) {
                    LocationUpdateModel locationUpdateModel = response.body();
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        //Toast.makeText(UpdateLocationService.this, ""+locationUpdateModel.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        //   Toast.makeText(UpdateLocationService.this, ""+locationUpdateModel.getMessage(), Toast.LENGTH_SHORT).show();
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
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
