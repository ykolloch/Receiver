package com.example.yannic.receiver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Yannic on 29.11.2016.
 */

public class Positionsabgleich {

    private MainActivity activity;
    private Context context;
    private LocationManager manager;
    private Location location;
    private final String LOG_TAG = this.getClass().toString();
    private static Positionsabgleich self;

    public Positionsabgleich(final MainActivity mainActivity, final Context context) {
        this.activity = mainActivity;
        this.context = context;

        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        startLocationTracking();

        self = this;
    }

    private void startLocationTracking() {
        if (manager == null)
            return;

        final LocationListener listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    /**
     * checks the receiving Position with own Position and return the range between both devices.
     * @return meters between devices.
     */
    public double getRangeDiffernce(final NMEA nmea) {
        if(location == null) return 0;
        double lat1 = nmea.getLatitude();
        double lon1 = nmea.getLongitude();
        double lat2 = location.getLatitude();
        double lon2 = location.getLongitude();

        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static Positionsabgleich getReference() {
        if(self != null) {
            return self;
        }
        return null;
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
            Log.v(LOG_TAG, "LocationChanged");
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
    }
}
