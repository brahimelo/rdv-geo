package com.example.messagerie;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSMS(View view) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            GPSTracker gps = new GPSTracker(this);
            // check if GPS location can get Location
            if (gps.canGetLocation()) {

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.d("Your Location", "latitude:" + gps.getLatitude()
                            + ", longitude: " + gps.getLongitude());

                    double lon = gps.getLongitude();
                    double lat = gps.getLatitude();

                    Toast.makeText(context, lon + " -- " + lat, duration).show();
                }
            }
        }
    }
}
