package com.yahoo.mobile.itern.guagua.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.CommunityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.CommunityListFragment;
import com.yahoo.mobile.itern.guagua.Fragment.MapFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "CommunityActivity";

    public CommunityFragment mCommunityFragement;
    public MapFragment mMapFragment;
    public CommunityListFragment mCommunityListFragement;

    public ParseObject mCurCommunity;
    private List<ParseObject> mCommunities = new ArrayList<>();

    private ActionBar mActionBar;
    private Location mCurLocation;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private MainApplication mMainApplication;
    private MenuItem mExploreDone;

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

        setupActionBar();

        mMainApplication = (MainApplication)this.getApplication();
        mCommunityFragement = CommunityFragment.newInstance(this);
        mMapFragment = MapFragment.newInstance(this);
        mCommunityListFragement = CommunityListFragment.newInstance(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mCommunityListFragement)
                .commit();

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            Utils.displayPromptForEnablingGPS(this);

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
        mCurLocation  = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        findCurrentCommunity();


        mMapFragment.setupMap();
        mCommunityListFragement.setupMap();
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
        if(getLastLocation() == null)
            setLastLocation(location);
        findCurrentCommunity();
    }


    public void findCurrentCommunity(){
        updateCommunityList();
        if(mCommunities.size()>0)
            mCurCommunity = mCommunities.get(0);
    }

    public void onCommunityChange(){
        mCommunityFragement.onCommunityChange();
        mMapFragment.onCommunityChange();
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
        findCurrentCommunity();
        return mCurCommunity;
    }

    public Location getCurrentLocation(){
        //findCurrentCommunity();
        return mCurLocation;
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    public void setLastLocation(Location location){
        mLastLocation = location;
    }


    public void switchToMapFragment(){
        mExploreDone.setVisible(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mMapFragment)
                .commit();
    }

    public void switchToCommunityFragment(){
        mExploreDone.setVisible(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mCommunityListFragement)
                .commit();
    }

    //sort communities with distance to mLastLocation
    public void updateCommunityList(){
        for(ParseObject com:mCommunities){
            Location comLocation = new Location(LocationManager.GPS_PROVIDER);
            comLocation.setLatitude(Double.parseDouble(com.getString("lat")));
            comLocation.setLongitude(Double.parseDouble(com.getString("long")));
            if(mLastLocation != null)
                com.put("distance", (int)mLastLocation.distanceTo(comLocation));
            else
                com.put("distance", (int)1E15);
        }

        Collections.sort(mCommunities, new Comparator<ParseObject>() {
            @Override
            public int compare(ParseObject lhs, ParseObject rhs) {
                return lhs.getInt("distance") - rhs.getInt("distance");
            }
        });
    }

    public List<ParseObject> getAllCommunities(){
        updateCommunityList();
        return mCommunities;
    }

    public void setupActionBar(){
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Explore");
        mActionBar.setHomeButtonEnabled(true);
    }

    public void showCommunityDialog(){
        String curCommunityTitle = mCurCommunity.getString("title");

        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("Community Selected")
                .setMessage("You have selected \"" + curCommunityTitle + "\" community.\n" +
                        "Please confirm if you want to join \"" + curCommunityTitle + "\" community.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject curCommunity = getCurCommunity();
                        ParseUtils.addCommunityToUser(curCommunity.getObjectId());
                        mMainApplication.currentViewingCommunity = curCommunity;
                        Utils.gotoMainActivity(CommunityActivity.this);
                    }
                })
                .setNegativeButton("resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);

        mExploreDone = menu.findItem(R.id.item_explore_done);
        mExploreDone.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_explore_done:
                switchToCommunityFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}