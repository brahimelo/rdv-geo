package com.example.messagerie;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response_layout);

        getIntent().getData();

        String num = getIntent().getData().getQueryParameter("num");

        TextView coord = (TextView) findViewById(R.id.coord);
        coord.setText(String.format("Répondre à %s", num));

    }

    //Lance google maps avec un marker sur les coordonnées
    public void geoRDV(View view) {
        Log.d("TESTA", "Salut");
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
}