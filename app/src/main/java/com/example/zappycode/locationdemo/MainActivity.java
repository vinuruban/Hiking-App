package com.example.zappycode.locationdemo;

import android.Manifest;
import android.accounts.AccountAuthenticatorResponse;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //CODE BELOW IS THE POP UP WHICH ASKS FOR THE LOCATION PERMISSION WHEN THE APP STARTS, USERS CAN CHOOSE TO ACCEPT/DENY REQUEST
        if (Build.VERSION.SDK_INT < 23) { //IF API < 23, PROVIDE LOCATION and we won't need to manually ask for permission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WASNT GRANTED, ASK FOR PERMISSION
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else { //IF PERMISSION IS GRANTED, PROVIDE LOCATION
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                //BELOW GETS LAST KNOWN LOCATION AT APP START
                Location lastKnownLocation =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocationInfo(lastKnownLocation);
                }
            }
        }

    }

    //WHEN THE USERS ACCEPT/DENY LOCATION REQUEST, THE CODE BELOW IS EXECUTED
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WAS GRANTED
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    //BELOW RETRIEVES DATA
    public void updateLocationInfo(Location location) {
        TextView latView = (TextView) findViewById(R.id.latView);
        TextView lngView = (TextView) findViewById(R.id.lngView);
        TextView accView = (TextView) findViewById(R.id.accView);
        TextView altView = (TextView) findViewById(R.id.altView);
        TextView addView = (TextView) findViewById(R.id.addView);

        //SET LAT
        latView.setText(location.getLatitude() + "");

        //SET LNG
        lngView.setText(location.getLongitude() + "");

        //SET ACC
        accView.setText(location.getAccuracy() + "");

        //SET ALT
        altView.setText(location.getAltitude() + "");

        //SET ADD
        //CODE BELOW CONVERTS THE LAT AND LONG TO ACTUAL ADDRESS
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault()); //Locale.getDefault() gets address from the specific country the phone is in
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                //Address[addressLines=[0:"398 Alexandra Ave, Rayners Lane, Harrow HA2 9UF, UK"],feature=398,admin=England,sub-admin=Greater London,locality=null,thoroughfare=Alexandra Avenue,postalCode=HA2 9UF,countryCode=GB,countryName=United Kingdom,hasLatitude=true,latitude=51.572310699999996,hasLongitude=true,longitude=-0.3708033,phone=null,url=null,extras=null]
                // ^ ^ code below will retrieve data from here. We will use getAddressLine(0) since that will simply get the full address
                // ^ ^ e.g. we can do getFeatureName(), getAdminArea(), getThoroughFare(), etc. instead of getAddressLine(0) to get the specifics
                if (addressList.get(0).getAddressLine(0) != null) {
                    String address = addressList.get(0).getAddressLine(0);
                    address = address.replaceAll(", ", ",\n");
                    addView.setText(address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
