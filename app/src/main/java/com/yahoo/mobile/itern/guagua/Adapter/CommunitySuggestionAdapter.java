package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanwang on 7/31/15.
 */

public class CommunitySuggestionAdapter extends ArrayAdapter<ParseObject> {
    private final int MAX_SIZE = 10;
    private Context mContext;
    private List<ParseObject> mAllCommunityList;
    private List<ParseObject> mVisibleCommunityList;
    private String mFilter="";

    public CommunitySuggestionAdapter(Context context, int resourceId, List<ParseObject> communities) {
        super(context, resourceId, communities);
        mContext = context;
        mAllCommunityList = communities;
        mVisibleCommunityList = new ArrayList<ParseObject>();
        mVisibleCommunityList.addAll(mAllCommunityList);
    }


    public View getView(int position, View curView, ViewGroup parent){
        View v = curView;
        if (v == null) {
            mVisibleCommunityList.clear();
            mVisibleCommunityList.addAll(mAllCommunityList);
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        }

        final ParseObject curCommunity = mVisibleCommunityList.get(position);
        if (curCommunity != null){
            TextView communityTitle = (TextView) v.findViewById(R.id.txt_suggetion_title);
            communityTitle.setText(curCommunity.getString("title"));

            TextView communityDistance = (TextView) v.findViewById(R.id.txt_suggestion_distance);
            communityDistance.setText("" + curCommunity.getInt("distance") / 1000 + " km");

        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = new Location("dummyprovider");
                location.setLatitude(Double.parseDouble(curCommunity.getString("lat")));
                location.setLongitude(Double.parseDouble(curCommunity.getString("long")));

                ((CommunityActivity)mContext).setLastLocation(location);
                ((CommunityActivity)mContext).updateLocationOnMap(location, true);
                ((CommunityActivity)mContext).mCurCommunity = curCommunity;
                ((CommunityActivity)mContext).showCommunityDialog();
            }
        });

        return v;
    }

    @Override
    public int getCount() {
        int origSize = mVisibleCommunityList.size();
        if(origSize < MAX_SIZE)
            return origSize;
        else
            return MAX_SIZE;
    }

    public void flushFilter() {
        mFilter = "";
        notifyDataSetChanged();
    }

    public void setFilter(String queryText) {
        mFilter = queryText;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged(){
        if(mFilter.equals("")){
            mVisibleCommunityList.clear();
            mVisibleCommunityList.addAll(mAllCommunityList);
        }
        else{
            mVisibleCommunityList.clear();
            for(ParseObject community : mAllCommunityList) {
                if(community.getString(Common.OBJECT_COMMUNITY_TITLE).toLowerCase().contains( mFilter.toLowerCase() )) {
                    mVisibleCommunityList.add(community);
                }
            }
        }
        super.notifyDataSetChanged();
    }

}
