package com.shopprdriver.background_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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

/**
 * Created by root on 12/2/18.
 */
@SuppressLint("NewApi")
public class MyService extends JobService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    /**
     * Update interval of location request
     */
    private final int UPDATE_INTERVAL = 1000;

    /**
     * fastest possible interval of location request
     */
    private final int FASTEST_INTERVAL = 900;

    /**
     * The Job scheduler.
     */
    JobScheduler jobScheduler;

    /**
     * The Tag.
     */
    String TAG = "MyService";

    /**
     * LocationRequest instance
     */
    private LocationRequest locationRequest;

    /**
     * GoogleApiClient instance
     */
    private GoogleApiClient googleApiClient;

    /**
     * Location instance
     */
    private Location lastLocation;

    /**
     * Method is called when location is changed
     * @param location - location from fused location provider
     */
    SessonManager sessonManager;






    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    /**
     * extract last location if location is not available
     */

    private void getLastKnownLocation() {
        //Log.d(TAG, "getLastKnownLocation()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            Log.i(TAG, "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
            startLocationUpdates();

        } else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();

            //here we can show Alert to start location
        }
    }

    /**
     * this method writes location to text view or shared preferences
     * @param location - location from fused location provider
     */
    @SuppressLint("SetTextI18n")
    private void writeActualLocation(Location location) {
       /* Log.d(TAG, location.getLatitude() + ", " + location.getLongitude());
        //here in this method you can use web service or any other thing*/
        double lat=location.getLatitude();
        double lang=location.getLongitude();



        sessonManager.setLatitude(String.valueOf(lat));
        sessonManager.setLongitude(String.valueOf(lang));

        UpdateLocationRequest updateLocationRequest=new UpdateLocationRequest();
        updateLocationRequest.setLat(lat);
        updateLocationRequest.setLang(lang);

        Call<LocationUpdateModel>call= ApiExecutor.getApiService(this)
                .apiLocationUpdate("Bearer "+sessonManager.getToken(),updateLocationRequest);
        call.enqueue(new Callback<LocationUpdateModel>() {
            @Override
            public void onResponse(Call<LocationUpdateModel> call, Response<LocationUpdateModel> response) {
                if (response.body()!=null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                        //Toast.makeText(MyService.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }else {
                        //Toast.makeText(MyService.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationUpdateModel> call, Throwable t) {

            }
        });
    }

    /**
     * this method only provokes writeActualLocation().
     */
    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }


    /**
     * this method fetches location from fused location provider and passes to writeLastLocation
     */

    private void startLocationUpdates() {
        //Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Default method of service
     * @param params - JobParameters params
     * @return boolean
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        sessonManager=new SessonManager(this);
        startJobAgain();

        createGoogleApi();

        return false;
    }

    /**
     * Create google api instance
     */
    private void createGoogleApi() {
        //Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //connect google api
        googleApiClient.connect();

    }

    /**
     * disconnect google api
     * @param params - JobParameters params
     * @return result
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        googleApiClient.disconnect();
        return false;
    }

    /**
     * starting job again
     */
    private void startJobAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "Job Started");
            ComponentName componentName = new ComponentName(getApplicationContext(),
                    MyService.class);
            jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setMinimumLatency(10000) //10 sec interval
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
            jobScheduler.schedule(jobInfo);
        }
    }

    /**
     * this method tells whether google api client connected.
     * @param bundle - to get api instance
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    /**
     * this method returns whether connection is suspended
     * @param i - 0/1
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"connection suspended");
    }

    /**
     * this method checks connection status
     * @param connectionResult - connected or failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"connection failed");
    }

    /**
     * this method tells the result of status of google api client
     * @param status - success or failure
     */
    /*@Override
    public void onResult(@NonNull Status status) {
        Log.d(TAG,"result of google api client : " + status);
    }*/





}

