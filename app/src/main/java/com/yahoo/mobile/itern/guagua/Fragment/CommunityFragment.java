package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;


/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityFragment extends Fragment {
    private final String TAG = "CommunityFragment";

    CommunityActivity mCommunityActivity;
    TextView mCommunityTitle;
    Button mYesBtn;
    Button mNoBtn;

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        mCommunityActivity = (CommunityActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);

        mCommunityTitle = (TextView)rootView.findViewById(R.id.txt_community_title);
        mYesBtn = (Button)rootView.findViewById(R.id.yes_button);
        mNoBtn  = (Button)rootView.findViewById(R.id.no_button);

        mYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommunityActivity.getCurCommunity() != null) {
                    ParseUtils.addCommunityToUser(mCommunityActivity.getCurCommunity().getObjectId());
                    Utils.gotoMainActivity(getActivity());
                }
            }
        });

        mNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommunityActivity) getActivity()).switchToMapFragment();
            }
        });

        onCommunityChange(mCommunityActivity.getCurCommunity());
        return rootView;
    }

    public void onCommunityChange(ParseObject belongCommunity){
        if(mCommunityTitle != null && belongCommunity != null)
            mCommunityTitle.setText("Do you want to join " + belongCommunity.getString("title") + "?");
    }
}