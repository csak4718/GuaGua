package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.CommunityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.MapFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by fanwang on 7/22/15.
 */
public class CommunityActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "CommunityActivity";

    CommunityFragment mCommunityFragement = new CommunityFragment();
    MapFragment mMapFragment = new MapFragment();

    public ParseObject mCurCommunity;
    private List<ParseObject> mCommunities = new ArrayList<>();

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop(){
        EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mCommunityFragement)
                .commit();

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps){
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ParseUtils.getAllCommunities();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        findCurrentCommunity();
        mMapFragment.setUpMap();
        Log.d(TAG, "mLastLocation:" + mLastLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed: " + location.toString());
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

        findCurrentCommunity();
    }

    public void findCurrentCommunity(){
        if(mLastLocation == null)
            return;

        double minDistance = 10E15;

        for(ParseObject com:mCommunities){
            Location comLocation = new Location(LocationManager.GPS_PROVIDER);
            comLocation.setLatitude(Double.parseDouble(com.getString("lat")));
            comLocation.setLongitude(Double.parseDouble(com.getString("long")));

            double distance = mLastLocation.distanceTo(comLocation);
            Log.d("Community",""+distance+" meters to "+com.get("title"));

            if(distance < minDistance) {
                minDistance = distance;
                mCurCommunity = com;
            }
        }

        mCommunityFragement.onCommunityChange(mCurCommunity);
        //mMapFragment.onCommunityChange(mCurCommunity);
        return ;
    }


    public void onEvent(CommunityEvent event) {
        Log.d("eventbus", "" + event.communityList.size());
        refreshList(event.communityList);
    }

    private void refreshList(List<ParseObject> list) {
        mCommunities.clear();
        mCommunities.addAll(list);
        findCurrentCommunity();
    }

    public ParseObject getCurCommunity(){
        return mCurCommunity;
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    public void setLastLocation(Location location){
        mLastLocation = location;
    }


    public void switchToMapFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mMapFragment)
                .commit();
    }

    public void switchToCommunityFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mCommunityFragement)
                .commit();
    }

}