package com.example.messagerie;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ResponseActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gmap;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyD0pAZ6Kn_PvYq2x8dGHphmgJPnqBhhAYw";


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response_layout);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }

    //Lance google maps avec un marker sur les coordonnées
    public void geoRDV(View view) {

        String lat = getIntent().getData().getQueryParameter("latt");
        String lng = getIntent().getData().getQueryParameter("long");

        //Label du lieu  pas affiché avec les dernières version de Maps
        Uri geoURI = Uri.parse("geo:0,0?q="+ Uri.encode(lat+","+lng+"(Lieu du rendez-vous)"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoURI);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    // A faire envoyer message de refus ou message d'acceptation (Facile)

    public void sendAcceptation(View v){

        String message = "Le rendez-vous a été accepté";
        sendResponse(message);

    }

    public void sendDiscard(View v){

            String message = "Le rendez-vous a été refusé";
            sendResponse(message);
    }


    public void sendResponse(String message)
    {
        if ( ContextCompat.checkSelfPermission(ResponseActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ResponseActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            String num = getIntent().getData().getQueryParameter("num");
            SmsManager.getDefault().sendTextMessage("+" + num, null, message, null, null);
            Toast.makeText(ResponseActivity.this, "Réponse envoyé" + num, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        String lat = getIntent().getData().getQueryParameter("latt");
        String lng = getIntent().getData().getQueryParameter("long");

        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);
        gmap.addMarker(markerOptions);


        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

}