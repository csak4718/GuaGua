package com.yahoo.mobile.itern.guagua.Fragment;

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
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.List;
import java.util.Locale;


/**
 * Created by fanwang on 7/16/15.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "MapFragment";

    private CommunityActivity mCommunityActivity;
    private MapView mMapView;
    private SearchView mSearchBarView;
    private Button mNextButton;
    private GoogleMap mMap;

    @Override
    public void onResume(){
        super.onResume();
        setUpMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCommunityActivity = (CommunityActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        /*mSearchBarView = (TextView)rootView.findViewById(R.id.map_search_bar);
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
        });*/

        mNextButton = (Button)rootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCommunityActivity.getCurCommunity() != null) {
                    ParseUtils.addCommunityToUser(mCommunityActivity.getCurCommunity().getObjectId());
                    Utils.gotoMainActivity(getActivity());
                }
            }
        });

        mMapView = (MapView)rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getActivity());
        mMapView.getMapAsync(this);
        return rootView;
    }


    public void setUpMap() {
        if(mMapView != null)
            mMapView.getMapAsync(this);
        return;
    }

    //callback of getMapAsync()
    @Override
    public void onMapReady(final GoogleMap map) {
        this.mMap = map;

        mMap.clear();
        mMap.setMyLocationEnabled(false);

        Location lastLocation = mCommunityActivity.getLastLocation();
        if(lastLocation != null) {
            Log.d(TAG, "my location:" + lastLocation.toString());
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
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
                mCommunityActivity.setLastLocation(location);
            }
        });
    }

    public void onCommunityChange(ParseObject belongCommunity){
        if(mNextButton != null && belongCommunity!=null)
            mNextButton.setText(belongCommunity.getString("title"));
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
                mCommunityActivity.setLastLocation(location);


            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return false;
            }
            return true;
        }


        protected void onPostExecute(Boolean found){
            if(found) {
                Location location = mCommunityActivity.getLastLocation();
                LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(lastLatLng)
                        .title("Your community"));
            }
        }

    }

}
