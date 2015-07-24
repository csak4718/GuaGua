package com.yahoo.mobile.itern.guagua.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;


/**
 * Created by fanwang on 7/16/15.
 */

public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener, OnMapReadyCallback {
    private final String TAG = "MapFragment";
    private View mView;


    private MapView mMapView;
    private TextView mSearchBarView;
    private Button mNextButton;
    private Location mLastLocation;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    private ParseObject mCurCommunity;
    private List<ParseObject> mCommunities = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        setUpMap();
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

        ParseUtils.getAllCommunities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mSearchBarView = (TextView)rootView.findViewById(R.id.map_search_bar);
        mSearchBarView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchBarView.getWindowToken(), 0);

                    new SearchClicked(mSearchBarView.getText().toString()).execute();
                    mSearchBarView.setText("", TextView.BufferType.EDITABLE);
                    return true;
                }
                return false;
            }
        });

        mNextButton = (Button)rootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUtils.addCommunityToUser(mCurCommunity.getObjectId());
                Utils.gotoMainActivity(getActivity());
            }
        });

        mMapView = (MapView)rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


        MapsInitializer.initialize(getActivity());
        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed: " + location.toString());
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(userLocation).title("Cur"));

        getCommunityIndex();
    }

    private void setUpMap() {
        mMapView.getMapAsync(this);
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
                        .title("Your community"));
                System.out.println(point.latitude + "---" + point.longitude);


                Location location = new Location("dummyprovider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                mLastLocation = location;
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

        setUpMap();
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


    public void onEvent(CommunityEvent event) {
        Log.d("eventbus", "" + event.communityList.size());
        refreshList(event.communityList);
    }

    private void refreshList(List<ParseObject> list) {
        mCommunities.clear();
        mCommunities.addAll(list);
    }

    public int getCommunityIndex(){
        double minDistance = 10E10;
        ParseObject belongCommunity = null;

        for(ParseObject com:mCommunities){
            Location comLocation = new Location(LocationManager.GPS_PROVIDER);
            comLocation.setLatitude(Double.parseDouble(com.getString("lat")));
            comLocation.setLongitude(Double.parseDouble(com.getString("long")));

            double distance = mLastLocation.distanceTo(comLocation);
            Log.d("Community",""+distance+" meters to "+com.get("title"));

            if(distance < minDistance) {
                minDistance = distance;
                belongCommunity = com;
                mCurCommunity = com;
            }
        }

        mNextButton.setText(belongCommunity.getString("title"));
        return 1;
    }

    private class SearchClicked extends AsyncTask<Void, Void, Boolean> {
        private String toSearch;
        private Address address;

        public SearchClicked(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.UK);
                List<Address> results = geocoder.getFromLocationName(toSearch, 1);

                if (results.size() == 0) {
                    return false;
                }

                address = results.get(0);
                Log.d("Search result", "" + address.getLatitude() + " " + address.getLongitude());

                Location location = new Location("dummyprovider");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                mLastLocation = location;


            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return false;
            }
            return true;
        }


        protected void onPostExecute(Boolean found){
            if(found) {
                LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(lastLatLng)
                        .title("Your community"));
            }
        }

    }

}
