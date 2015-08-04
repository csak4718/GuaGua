package com.yahoo.mobile.itern.guagua.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.R;

import java.util.List;
import java.util.Locale;


/**
 * Created by fanwang on 7/16/15.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "MapFragment";

    private Context mContext;
    private MapView mMapView;
    private SearchView mSearchView;
    private View mRootView;
    private GoogleMap mMap;

    public MapFragment() {
    }

    public static MapFragment newInstance(Context context)
    {
        MapFragment mapFragment = new MapFragment();
        mapFragment.mContext = context;
        return mapFragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        setupMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView)mRootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(getActivity());
        mMapView.getMapAsync(this);

        mSearchView = (SearchView)mRootView.findViewById(R.id.map_search_view);
        setupSearchView();
        return mRootView;
    }



    public void setupSearchView(){
        if(mSearchView != null){
            //set text color
            TextView searchText = (TextView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            if (searchText!=null) {
                searchText.setTextColor(Color.BLACK);
                searchText.setHintTextColor(Color.BLACK);
            }

            mSearchView.setQueryHint("Search Here");
            mSearchView.setSubmitButtonEnabled(false);
            mSearchView.setIconified(false);

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    new SearchClicked(query).execute();


                    View view = ((CommunityActivity)mContext).getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    public void setupMap() {
        if(mMapView != null)
            mMapView.getMapAsync(this);
        return;
    }

    //callback of getMapAsync()
    @Override
    public void onMapReady(final GoogleMap map) {
        this.mMap = map;

        mMap.clear();
        mMap.setMyLocationEnabled(true);

        Location lastLocation = ((CommunityActivity)mContext).getLastLocation();
        if(lastLocation != null) {
            Log.d(TAG, "my location:" + lastLocation.toString());
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title(((CommunityActivity)mContext).getCurCommunity().getString("title")))
                    .showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 18));//zoom level(0-19)
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(((CommunityActivity)mContext).getCurCommunity().getString("title")))
                        .showInfoWindow();

                Location location = new Location("dummyprovider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                ((CommunityActivity)mContext).setLastLocation(location);
            }
        });
    }

    //call for search result
    public void updateLocation(){
        Location location = ((CommunityActivity)mContext).getLastLocation();
        LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));

        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title(((CommunityActivity)mContext).getCurCommunity().getString("title")))
                .showInfoWindow();

        ((CommunityActivity)mContext).updateCommunityList();
    }

    public void onCommunityChange(){
        Location location = ((CommunityActivity)mContext).getLastLocation();
        LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title(((CommunityActivity) mContext).getCurCommunity().getString("title")))
                    .showInfoWindow();
        }
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
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault() );
                List<Address> results = geocoder.getFromLocationName(toSearch, 1);

                if (results.size() == 0) {
                    return false;
                }

                address = results.get(0);
                Log.d("Search result", "" + address.getLatitude() + " " + address.getLongitude());

                Location location = new Location("dummyprovider");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                ((CommunityActivity)mContext).setLastLocation(location);


            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return false;
            }
            return true;
        }


        protected void onPostExecute(Boolean found) {
            if (found) {
                updateLocation();

            }
        }
    }

}
