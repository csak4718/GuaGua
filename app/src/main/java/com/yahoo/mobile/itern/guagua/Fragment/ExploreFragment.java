package com.yahoo.mobile.itern.guagua.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.Adapter.CommunitySuggestionAdapter;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;


/**
 * Created by fanwang on 7/22/15.
 */

public class ExploreFragment extends Fragment {
    private final String TAG = "CommunityFragment";
    private MainApplication mMainApplication;
    private Context mContext;

    private GoogleMap mMap;
    private MapView mMapView;
    public CommunitySuggestionAdapter mAdapter;
    private SearchView mCommunitySearchView;
    private ListView mSuggestionList;
    private LinearLayout mEditLocationLayout;


    public ExploreFragment() {
    }

    public static ExploreFragment newInstance(Context context)
    {
        ExploreFragment communitylistFragment = new ExploreFragment();
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
        mMapView.getMapAsync((CommunityActivity)mContext);


        mCommunitySearchView = (SearchView)rootView.findViewById(R.id.search_community);
        setupCommunitySearchView();

        mSuggestionList = (ListView)rootView.findViewById(R.id.list_community_suggestion);
        mAdapter = new CommunitySuggestionAdapter(mContext, R.layout.suggestion_item, ((CommunityActivity)mContext).getAllCommunities());
        mSuggestionList.setAdapter(mAdapter);

        return rootView;
    }

    public void setupMap() {
        if(mMapView != null)
            mMapView.getMapAsync((CommunityActivity)mContext);//onMapReadyCallback
        return;
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