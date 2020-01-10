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

public class ResponseActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response_layout);

        getIntent().getData();


        String num = getIntent().getData().getQueryParameter("num");
        TextView coord = (TextView) findViewById(R.id.coord);
        coord.setText(String.format("Répondre à +%s", num));

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

}