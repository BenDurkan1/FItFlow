package com.example.FitFlow.Other;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

public class TrackingUtil {

    public static boolean hasLocationPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );
        } else {
            return EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            );
        }
    }



        public static float calculatePolylineLength(List<LatLng> polyline) {
            float distance = 0f;
            for (int i = 0; i < polyline.size() - 2; i++) {
                LatLng pos1 = polyline.get(i);
                LatLng pos2 = polyline.get(i + 1);

                float[] result = new float[1];
                Location.distanceBetween(
                        pos1.latitude,
                        pos1.longitude,
                        pos2.latitude,
                        pos2.longitude,
                        result
                );
                distance += result[0];
            }

            return distance;
        }


    public static String getFormattedStopWatchTime(long ms, boolean includeMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(ms);

        ms -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);

        ms -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);

        if (!includeMillis) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        ms -= TimeUnit.SECONDS.toMillis(seconds);

        ms /= 10;

        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, ms);
    }


}
