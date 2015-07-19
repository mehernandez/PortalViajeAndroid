package com.equinox.mariohernandez.portalviaje;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class CrearUbicacionMapActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng coord;

    private Location loc;


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ubicacion_map);

        final String dataInicial = getIntent().getStringExtra("loc");

        buildGoogleApiClient();

        if(dataInicial != null){

            setUpMapIfNeeded2();

            final  String[] dat = dataInicial.split("_");

            coord = new LatLng(Double.parseDouble(dat[0]), Double.parseDouble(dat[1]));

            mMap.addMarker(new MarkerOptions().position(coord).title("Tu ubicación"));

            //  mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(coord, 14.0f) );


            Button btnNo = (Button)findViewById(R.id.btn_marcador_crearUbicacionMap);
            btnNo.setText("No, Volver");

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_CANCELED, new Intent());
                    finish();
                }
            });



            Button btnSi = (Button)findViewById(R.id.btn_gps_crearUbicacionMap);
            btnSi.setText("Si, Continuar");

            btnSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setResult(RESULT_OK, new Intent().putExtra("coord", "dir_" + dataInicial));
                    finish();
                }
            });


        }else {


            setUpMapIfNeeded();

            findViewById(R.id.btn_gps_crearUbicacionMap).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setResult(RESULT_OK, new Intent().putExtra("coord", "gps_" + loc.getLatitude() + "_" + loc.getLongitude()));
                    finish();
                }
            });

            findViewById(R.id.btn_marcador_crearUbicacionMap).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setResult(RESULT_OK, new Intent().putExtra("coord", "gps_" + coord.latitude + "_" + coord.longitude));
                    finish();
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMapIfNeeded2() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {



        mMap.setMyLocationEnabled(true);


        if(loc != null) {
            coord = new LatLng(loc.getLatitude(), loc.getLongitude());

            mMap.addMarker(new MarkerOptions().position(coord).title("Tu ubicación"));


        }else{
            coord = new LatLng(4.601586, -74.06527399999999);

            mMap.addMarker(new MarkerOptions().position(coord).title("Universidad de los Andes"));
        }

        //  mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(coord, 14.0f) );

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Tu ubicación"));
                coord = latLng;

            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {

        loc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Toast.makeText(this,"Location detected",Toast.LENGTH_SHORT);

        if(loc != null) {

            mMap.clear();
            coord = new LatLng(loc.getLatitude(), loc.getLongitude());

            mMap.addMarker(new MarkerOptions().position(coord).title("Tu ubicación"));


        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
