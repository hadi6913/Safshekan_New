package com.safshekan.parkban.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.LoginActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.dialogs.GPSDialog;

import java.sql.Date;
import java.util.List;

public class GPSTracker implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;// 10; // 10 meters
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 15000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;// 1000 * 60 * 1; // 1 minute
    private Context context;
    private String source;

    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    private Location location;
    public static double latitude;
    public static double longitude;
    protected LocationManager locationManager;
    ILocationChanged locationChanged;
    private boolean canGetLocation;

    public GPSTracker(Context context) {
        this.context = context;
        this.locationChanged = locationChanged;
    }

    public Location getLocation(String source) {
        this.source = source;
        try {

            Log.i("============>", "get loc");
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled || !isNetworkEnabled) {
                showGpsDialog();
                return null;
            }

            this.canGetLocation = true;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((BaseActivity) context).checkLocationPermission("Record");
                    //context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    return null;
                }
            }

            location = getLastKnownLocation();

            // if (location == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = getLastKnownLocation();
                if (location != null) {
                    onLocationChanged(location);

                    if (locationChanged != null)
                        locationChanged.LocationChanged(location);
                }
            }
            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location == null)
            if (isGPSEnabled) {
                Log.i("============>", "show toast");
                ShowToast.getInstance().showWarning(context, R.string.pls_wait_for_location);
                return null;
            }

        Log.i("============>", "location " + location);

        ShowToast.getInstance().showErrorStringMsg(context, "Accuracy  " + location.getAccuracy());
        //ShowToast.getInstance().showErrorStringMsg(context, "time  " + getDateAndTime(location).getHours() + ":" + getDateAndTime(location).getMinutes());

        return location;
    }

    public void showGpsDialog() {
        try {
            // Log.i("------------------>>>", "showGpsDialog ");
            GPSDialog dialogFragment = new GPSDialog();
            dialogFragment.show(((Activity) context).getFragmentManager(), "");

        } catch (Exception e) {
            // Log.i("------------------>>>", "exception " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
//        if (locationChanged != null)
//            locationChanged.LocationChanged(location);

        boolean fakeLocation = false;

        if (location != null) {

//            if (isMockLocationEnabled(context)) {
//
//                fakeLocation = true;

            if (Build.VERSION.SDK_INT >= 18)
                fakeLocation = isMockLocation(location);
            else
                fakeLocation = isMockSettingsON(context);
//            } else
//                fakeLocation = false;

            if (fakeLocation) {
                if (source == BaseActivity.MainActivity){
                    ((BaseActivity) context).finish();
                }else {
                    Intent i = new Intent(context, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }
                ShowToast.getInstance().showError(context, R.string.fake_location);
                locationManager.removeUpdates(this);
            } else {
                this.location = location;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            if (locationChanged != null)
                locationChanged.LocationChanged(location);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation("");
    }

    @Override
    public void onProviderDisabled(String provider) {
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
        /// Log.i("------------------>>>", "onProviderDisabled ");
        showGpsDialog();
//        latitude = 0.0;
//        longitude = 0.0;
    }

    public float getDistanceBetween(Location locationA, Location locationB) {
        return locationA.distanceTo(locationB);
    }

    public Date getDateAndTime(Location location) {
        Date date = null;
        if (location != null) {
            long utcTime = location.getTime();

            date = new Date(utcTime);
        }
        return date;
    }

    public interface ILocationChanged {
        void LocationChanged(Location location);
    }

    public Location getLastKnownLocation() {

        Location net_loc = null, gps_loc = null;
        if (isGPSEnabled)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((BaseActivity) context).checkLocationPermission("Record");
                    return null;
                }
            }
        gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkEnabled)
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // if there are both values use the best one
        if (gps_loc != null && net_loc != null) {
            if (isBetterLocation(gps_loc, net_loc))
                return gps_loc;
            else
                return net_loc;

        } else if (gps_loc != null) {
            return gps_loc;

        } else if (net_loc != null) {
            return net_loc;

        } else
            return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public boolean isMockLocation(Location location) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider();
    }

    public static Boolean isMockLocationEnabled(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

}
