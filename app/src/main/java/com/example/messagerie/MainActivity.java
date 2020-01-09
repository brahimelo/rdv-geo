package com.example.messagerie;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText addnumText = (EditText) findViewById(R.id.editTextNum);
        addnumText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP && addnumText.length()>0)
                {
                    EditText destNums = (EditText) findViewById(R.id.dests);
                    destNums.append(addnumText.getText().toString() + '\n');
                    addnumText.setText("");
                    addnumText.clearFocus();
                    return true;
                }
                    return false;
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSMS(View view) throws IOException {

        double lng;
        double lat;

        EditText adressText = (EditText) findViewById(R.id.adress);

        if(adressText.length()!=0) {

            double[] coord = this.getLocationFromAddress(adressText.getText().toString());
            lat = coord[0];
            lng = coord[1];

        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            GPSTracker gps = new GPSTracker(this);
            // Est-ce que le GPS peut avoir la localisation
            if (gps.canGetLocation()) {

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.d("Your Location", "latitude:" + gps.getLatitude()
                            + ", longitude: " + gps.getLongitude());

                     lng = gps.getLongitude();
                     lat = gps.getLatitude();


                    TextView numView = (TextView) findViewById(R.id.editTextNum);
                    String num = numView.getText().toString();

                     String message = "Vous avez une nouvelle invitation ! Pour y répondre suivez ce lien : " +
                             "http://elojacquit.fr/map?num="+ num + "&latt="+ lat + "&long=" + lng;

                     actionSendSMS(message, num);
                }
            }
        }
    }
    static final int PICK_CONTACT_REQUEST = 1;

    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        if (requestCode == PICK_CONTACT_REQUEST) {

            if (resultCode == RESULT_OK) {

                Uri contactUri = resultIntent.getData();
                String[] projection = {Phone.NUMBER};

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(column);

                EditText destNums = (EditText) findViewById(R.id.dests);
                destNums.append(number + '\n');
            }
        }
    }



    // Transforme une adresse en coordonnées
    public double[] getLocationFromAddress(String strAddress) throws IOException {
        Geocoder coder = new Geocoder(this,Locale.FRANCE);
        List<Address> address;
            address = coder.getFromLocationName(strAddress, 1);

            if (address.size()>0) {
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            return new double[]{lat, lng};
            }

            return null;
    }

    // Vérifie le numéro et envoie un message
    public void actionSendSMS(String message, String num) {

        // Demande de permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

        } else {
            // Verification format numéro
            if (num.matches("[0-9]+") && num.length() >= 4) {
                SmsManager.getDefault().sendTextMessage(num, null, message, null, null);
                Toast.makeText(MainActivity.this, "Message envoyé à " + num, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, num + " : numéro invalide", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
