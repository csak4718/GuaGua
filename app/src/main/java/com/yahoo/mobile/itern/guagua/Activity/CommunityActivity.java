package com.yahoo.mobile.itern.guagua.Activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.CommunityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.ExploreFragment;
import com.yahoo.mobile.itern.guagua.Fragment.MapFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.SnapshotReadyCallback {

    private final int STATE_EXPLORE = 0;
    private final int STATE_MAP = 1;
    private final int STATE_SEARCH = 2;
    private final int STATE_CREATE = 3;
    private int mCurState;

    private final String TAG = "CommunityActivity";

    public CommunityFragment mCommunityFragement;
    public MapFragment mMapFragment;
    public ExploreFragment mExploreFragement;

    public ParseObject mCurCommunity;
    private List<ParseObject> mCommunities = new ArrayList<>();

    private Context mContext;
    private ActionBar mActionBar;
    private Location mCurLocation;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private MainApplication mMainApplication;
    private MenuItem mExploreDone;
    private MenuItem mCreateDone;
    private GoogleMap mMap;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        mGoogleApiClient.connect();
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
        mCurState = STATE_EXPLORE;
        setContentView(R.layout.activity_community);
        mContext = this;
        setupActionBar();

        mMainApplication = (MainApplication)this.getApplication();
        mCommunityFragement = CommunityFragment.newInstance(this);
        mMapFragment = MapFragment.newInstance(this);
        mExploreFragement = ExploreFragment.newInstance(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mExploreFragement)
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
        Log.d(TAG, "GoogleApiClient connection has been established");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mCurLocation  = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "curLocation is " + mCurLocation);

        sortCommunityList();

        mMapFragment.setupMap();
        mExploreFragement.setupMap();
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
        //Log.d(TAG, "Location changed: " + location.toString());

        Location prevLocation = getCurrentLocation();
        setCurrentLocation(location);
        sortCommunityList();

        mExploreFragement.mAdapter.notifyDataSetChanged();

        if(prevLocation==null && location!=null) {
            mMapFragment.setupMap();
            mExploreFragement.setupMap();
        }
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
        sortCommunityList();

        mExploreFragement.mAdapter.flushFilter();
        mExploreFragement.mAdapter.notifyDataSetChanged();
    }

    public ParseObject getCurCommunity(){
        return mCurCommunity;
    }

    public Location getCurrentLocation(){
        return mCurLocation;
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    public void setLastLocation(Location location){
        mLastLocation = location;
    }

    public void setCurrentLocation(Location location){
        mCurLocation = location;
    }

    public void switchToMapFragment(){
        mCurState = STATE_MAP;
        mCreateDone.setVisible(false);
        mExploreDone.setVisible(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mMapFragment)
                .commit();
    }

    public void switchToCreateCommunity(){
        mCurState = STATE_CREATE;
        mExploreDone.setVisible(false);
        mCreateDone.setVisible(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mMapFragment)
                .commit();
    }

    public void switchToSearch(){
        mCurState = STATE_SEARCH;
    }


    public void switchToExploreFragment(){
        mCurState = STATE_EXPLORE;
        mExploreDone.setVisible(false);
        mCreateDone.setVisible(false);
        mExploreFragement.showMap();
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, mExploreFragement)
                .commit();
    }

    //sort communities with distance to mLastLocation
    public void sortCommunityList(){
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
        sortCommunityList();
        return mCommunities;
    }

    public void setupActionBar(){
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getResources().getString(R.string.drawer_view_explore));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void showCommunityDialog(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mMap.snapshot(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);

        mExploreDone = menu.findItem(R.id.item_explore_done);
        mExploreDone.setVisible(false);

        mCreateDone = menu.findItem(R.id.item_create_done);
        mCreateDone.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mCurState == STATE_EXPLORE)
                    this.finish();
                else
                    switchToExploreFragment();
                return true;

            case R.id.item_explore_done:
                switchToExploreFragment();
                return true;

            case R.id.item_create_done:
                showCommunityDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        //mMap.clear();
        mMap.setMyLocationEnabled(true);

        if(getLastLocation() == null) {
            setLastLocation(getCurrentLocation());
            Location location = this.getLastLocation();
            updateLocationOnMap(location, false);
        }
        else {
            Location location = this.getLastLocation();
            updateLocationOnMap(location, true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Location location = new Location("dummyprovider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                setLastLocation(location);
                mExploreFragement.mAdapter.notifyDataSetChanged();

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_community_pin))
                        .position(new LatLng(point.latitude, point.longitude)));
            }
        });
/*
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                //Log.d("explore","camera change "+ position);
                LatLng point = position.target;
                Location location = new Location("dummyprovider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                setLastLocation(location);
            }
        });
        */
    }

    public void updateLocationOnMap(Location location, boolean showMarker){
        if(location != null) {
            LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));//zoom level(0-19)
            mExploreFragement.mAdapter.notifyDataSetChanged();

            if(showMarker) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_community_pin))
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));
                        //.title(this.getCurCommunity().getString("title")))
                        //.showInfoWindow();
            }
        }
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        if(mCurState==STATE_CREATE)
            showCommunityCreateDialog(bitmap);
        else
            showCommunitySelectDialog(bitmap);

    }

    public void showCommunitySelectDialog(Bitmap bitmap){
        String curCommunityTitle = mCurCommunity.getString("title");
        //View v = getLayoutInflater().inflate(R.layout.dialog_community_selected, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_community_selected);

        TextView title = (TextView)dialog.findViewById(R.id.txt_dialog_title);
        title.setText(getResources().getString(R.string.community_select_title));

        CircleImageView image = (CircleImageView) dialog.findViewById(R.id.img_dialog_community_miniature);
        image.setImageBitmap(cropBmpToRect(bitmap));

        TextView text = (TextView) dialog.findViewById(R.id.txt_dialog_content);
        text.setText(getResources().getString(R.string.community_select)+" \"" + curCommunityTitle + "\".");

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog_confirm_cummunity);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject curCommunity = getCurCommunity();
                ParseUtils.addCommunityToUser(curCommunity);
                mMainApplication.currentViewingCommunity = curCommunity;
                Utils.gotoMainActivity(CommunityActivity.this);
                dialog.dismiss();
                ((CommunityActivity)mContext).finish();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        dialog.show();
    }

    public void showCommunityCreateDialog(Bitmap bitmap){
        String curCommunityTitle = mCurCommunity.getString("title");
        //View v = getLayoutInflater().inflate(R.layout.dialog_community_selected, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_community_selected);

        TextView title = (TextView)dialog.findViewById(R.id.txt_dialog_title);
        title.setText(getResources().getString(R.string.community_create_title));

        CircleImageView image = (CircleImageView) dialog.findViewById(R.id.img_dialog_community_miniature);
        image.setImageBitmap(cropBmpToRect(bitmap));

        TextView text = (TextView) dialog.findViewById(R.id.txt_dialog_content);
        String newCommunityTitle = (String)mExploreFragement.mNewCommunityTitle.getText();
        text.setText(getResources().getString(R.string.community_create)+" \"" + newCommunityTitle + "\".");

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog_confirm_cummunity);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCommunityTitle = (String)mExploreFragement.mNewCommunityTitle.getText();
                ParseObject newCommunity = ParseUtils.createCommunity(newCommunityTitle , mLastLocation);
                mCurCommunity = newCommunity;

                ParseObject curCommunity = getCurCommunity();
                ParseUtils.addCommunityToUser(curCommunity);
                mMainApplication.currentViewingCommunity = curCommunity;
                Utils.gotoMainActivity(CommunityActivity.this);
                dialog.dismiss();
                ((CommunityActivity)mContext).finish();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        dialog.show();
    }


    public Bitmap cropBmpToRect(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }
}