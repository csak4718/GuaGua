package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.R;


/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityFragment extends Fragment {
    private final String TAG = "CommunityFragment";

    Button mYesBtn;
    Button mNoBtn;

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        mYesBtn = (Button)rootView.findViewById(R.id.yes_button);
        mNoBtn  = (Button)rootView.findViewById(R.id.no_button);

        mYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //[TODO] delivery message to MainActivity
                ((CommunityActivity)getActivity()).gotoMainActivity();
            }
        });

        mNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommunityActivity)getActivity()).switchToMapFragment();
            }
        });

        return rootView;
    }
}