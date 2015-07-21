package com.yahoo.mobile.itern.guagua.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yahoo.mobile.itern.guagua.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by fanwang on 7/16/15.
 */

public class GuaMapFragment extends MapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener, OnMapReadyCallback {
    private final String TAG = "GuaMapFragment";
    private View mView;
    private Location mLastLocation;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;


    @Override
    public void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
        setUpMapIfNeeded();
    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps){
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        setUpMapIfNeeded();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed: "+location.toString());
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(userLocation).title("Cur"));
    }

    private void setUpMapIfNeeded() {
        //if(mMap == null)
        getMapAsync(this);
        return;
    }

    //callback of getMapAsync()
    @Override
    public void onMapReady(final GoogleMap map) {
        this.mMap = map;

        mMap.clear();
        mMap.setMyLocationEnabled(false);

        if(mLastLocation != null) {
            Log.d(TAG, "my location:" + mLastLocation.toString());
            LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 18));//zoom level(0-19)
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title("Costume Marker"));
                System.out.println(point.latitude + "---" + point.longitude);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        setUpMapIfNeeded();
        Log.d(TAG, "mLastLocation:"+mLastLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection has failed");
    }
}
