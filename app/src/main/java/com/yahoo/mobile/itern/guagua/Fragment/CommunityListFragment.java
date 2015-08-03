package com.yahoo.mobile.itern.guagua.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.Adapter.CommunitySuggestionAdapter;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;


/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityListFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "CommunityFragment";
    private MainApplication mMainApplication;
    private Context mContext;

    private GoogleMap mMap;
    private MapView mMapView;
    private CommunitySuggestionAdapter mAdapter;
    private SearchView mCommunitySearchView;
    private ListView mSuggestionList;
    private LinearLayout mEditLocationLayout;


    public CommunityListFragment() {
    }

    public static CommunityListFragment newInstance(Context context)
    {
        CommunityListFragment communitylistFragment = new CommunityListFragment();
        communitylistFragment.mContext = context;
        return communitylistFragment;
    }


    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        mContext = getActivity();
        mMainApplication = (MainApplication)((Activity)mContext).getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_list, container, false);

        mEditLocationLayout = (LinearLayout)rootView.findViewById(R.id.layout_edit_community);
        mEditLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommunityActivity)mContext).switchToMapFragment();
            }
        });


        mMapView = (MapView)rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getActivity());
        mMapView.getMapAsync(this);


        mCommunitySearchView = (SearchView)rootView.findViewById(R.id.search_community);
        setupCommunitySearchView();

        mSuggestionList = (ListView)rootView.findViewById(R.id.list_community_suggestion);
        mAdapter = new CommunitySuggestionAdapter(mContext, R.layout.suggestion_item, ((CommunityActivity)mContext).getAllCommunities());
        mSuggestionList.setAdapter(mAdapter);

        return rootView;
    }

    public void setupMap() {
        if(mMapView != null)
            mMapView.getMapAsync(this);//onMapReadyCallback
        return;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap.clear();
        mMap.setMyLocationEnabled(true);

        Location curLocation = ((CommunityActivity)mContext).getCurrentLocation();
        updateLocation(curLocation, false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Location location = new Location("dummyprovider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                ((CommunityActivity)mContext).setLastLocation(location);
                mAdapter.notifyDataSetChanged();

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(((CommunityActivity)mContext).getCurCommunity().getString("title")))
                        .showInfoWindow();
            }
        });

        mMapView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View arg0, DragEvent arg1) {
                return false;
            }
        });
    }

    //make sure the curComunity is setup
    public void updateLocation(Location location, boolean showMarker){
        if(location != null) {

            LatLng curLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocationLatLng, 15));//zoom level(0-19)
            mAdapter.notifyDataSetChanged();

            if(showMarker) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(((CommunityActivity) mContext).getCurCommunity().getString("title")))
                        .showInfoWindow();
            }

        }


    }

    public void setupCommunitySearchView(){
        if(mCommunitySearchView != null){
            //set text color
            TextView searchText = (TextView) mCommunitySearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            if (searchText!=null) {
                searchText.setTextColor(Color.BLACK);
                searchText.setHintTextColor(Color.BLACK);
            }

            mCommunitySearchView.setSubmitButtonEnabled(false);
            mCommunitySearchView.setIconified(true);

            mCommunitySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }
}