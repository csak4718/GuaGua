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

import java.util.List;

/**
 * Created by fanwang on 7/31/15.
 */

public class CommunitySuggestionAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private List<ParseObject> mCommunityList;

    public CommunitySuggestionAdapter(Context context, int resourceId, List<ParseObject> communities) {
        super(context, resourceId, communities);
        mContext = context;
        mCommunityList = communities;
    }


    public View getView(int position, View curView, ViewGroup parent){
        View v = curView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.suggestion_item, null);
        }
        final ParseObject curCommunity = mCommunityList.get(position);

        if (curCommunity != null){
            TextView tt = (TextView) v.findViewById(R.id.txt_suggetion_title);
            tt.setText(curCommunity.getString("title"));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location location = new Location("dummyprovider");
                location.setLatitude(Double.parseDouble(curCommunity.getString("lat")));
                location.setLongitude(Double.parseDouble(curCommunity.getString("long")));

                ((CommunityActivity)mContext).setLastLocation(location);
                ((CommunityActivity)mContext).mMapFragment.updateLocation();
            }
        });

        return v;
    }

}
