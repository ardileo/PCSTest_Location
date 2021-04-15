package com.ardileo.pcstest.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;

import androidx.core.app.ActivityCompat;

import static android.app.Activity.RESULT_CANCELED;

public class LocUtils {
    private Context mContext;
    private Location mLocation;
    public String provider;
    public double userlatitude, userlongitude;
    private LocationManager mLocationManager;
    private static int MY_PERMISSION_REQUEST_LOCATION = 1005;
    private static int MY_PERMISSION_REQUEST_GPS = 1004;
//    private GoogleApiClient mGoogleApiClient;

    private AlertDialog alertDialog;


    private Activity mActivity;

    public LocUtils(Context context) {
        this.mContext = context;
        this.provider = getProviderAvailable();

        if (mContext instanceof Activity) {
            mActivity = (Activity) mContext;
        }

//        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API).build();
//        mGoogleApiClient.connect();
        locateCurrentPosition();

    }


    public boolean isPermissionGrant() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public void requestPermissionGrant() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
    }


    public boolean isGPSOn() {
        return getProviderAvailable() != null;
    }

    private String getProviderAvailable() {
        provider = null;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        return provider;
    }

    public void turnOnGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setNumUpdates(1);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(mContext);
        mSettingsClient.checkLocationSettings(builder.build())
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) mContext, MY_PERMISSION_REQUEST_GPS);
                                } catch (IntentSender.SendIntentException sie) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.";
                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public Location getLocation() {
        locateCurrentPosition();
        if (mLocation == null) {
            mLocation = new Location("manual");
            mLocation.setLongitude(userlongitude);
            mLocation.setLatitude(userlatitude);
        }
        return mLocation;
    }

    private boolean checkPermission() {
        return isPermissionGrant();
    }

    public void superLocateCurrentPosition() {
        if (!isGPSOn()) {
            turnOnGPS();
            locateCurrentPosition();
        } else {
            locateCurrentPosition();
        }
    }

    public void locateCurrentPosition() {
        if (checkPermission() && isGPSOn()) {
            mLocation = mLocationManager.getLastKnownLocation(provider);
            if (mLocation != null && mLocation.getLatitude() != 0) {
                userlatitude = mLocation.getLatitude();
                userlongitude = mLocation.getLongitude();
            } else {
                mLocationManager.requestSingleUpdate(provider, defLocationlistener, null);
                provider = LocationManager.NETWORK_PROVIDER;
                mLocation = mLocationManager.getLastKnownLocation(provider);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isPermissionGrant()) {
                    recreate();
                }
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Location Permission");
                builder.setMessage("The app needs location permission. Please grant this permission to continue using the features of the app.");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0])) {
                            recreate();
                        } else {
                            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(uri);
                            mActivity.startActivityForResult(intent, MY_PERMISSION_REQUEST_LOCATION);
                        }
                    }
                });
                builder.setCancelable(false);
                alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    alertDialog.show();
                }
            }
        }
    }

    LocationListener defLocationlistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            locateCurrentPosition();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            locateCurrentPosition();
        }

        @Override
        public void onProviderEnabled(String provider) {
            superLocateCurrentPosition();
        }

        @Override
        public void onProviderDisabled(String provider) {
            superLocateCurrentPosition();
        }
    };

    private void recreate() {
        Intent i = new Intent(mActivity.getIntent()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSION_REQUEST_LOCATION) {
            if (resultCode == RESULT_CANCELED) {
                requestPermissionGrant();
            } else {
                recreate();
            }
        } else if (requestCode == MY_PERMISSION_REQUEST_GPS) {
            if (resultCode == RESULT_CANCELED) {
                turnOnGPS();
            } else {
                recreate();
                locateCurrentPosition();
            }
        }
    }
}
