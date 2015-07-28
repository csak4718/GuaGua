package com.yahoo.mobile.itern.guagua.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by fanwang on 7/22/15.
 */

public class CommunityFragment extends Fragment {
    private final String TAG = "CommunityFragment";
    private MainApplication mMainApplication;
    private Map<String,Drawable> badgeDrawables;

    Context mContext;
    TextView mCommunityTitle;
    Button mYesBtn;
    Button mNoBtn;
    ImageView mCommunityBadge;

    public CommunityFragment(Context context){
        mContext = context;
        mMainApplication = (MainApplication)((Activity)mContext).getApplication();
        badgeDrawables = new HashMap<>();
        badgeDrawables.put("TFLWNFWhrd", ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.badge_ytaiwan_big, null));
        badgeDrawables.put("44NgJyvIAA", ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.badge_ntu_big, null));
        badgeDrawables.put("tzlj2Wj2U8", ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.badge_ntust_big, null));
        badgeDrawables.put("el6mVLwzHE", ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.badge_ntnu_big, null));
        badgeDrawables.put("VifEC40TJO", ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.badge_sunnyvale_big, null));
    }

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);

        mCommunityTitle = (TextView)rootView.findViewById(R.id.txt_community_title);
        mYesBtn = (Button)rootView.findViewById(R.id.yes_button);
        mNoBtn  = (Button)rootView.findViewById(R.id.no_button);
        mCommunityBadge = (ImageView)rootView.findViewById(R.id.img_community_logo);

        mYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject curCommunity = ((CommunityActivity)mContext).getCurCommunity();
                if (curCommunity != null) {
                    ParseUtils.addCommunityToUser(curCommunity.getObjectId());
                    mMainApplication.currentViewingCommunity = curCommunity;
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

        onCommunityChange();
        return rootView;
    }

    public void onCommunityChange(){
        ParseObject belongCommunity = ((CommunityActivity)mContext).getCurCommunity();
        if(mCommunityTitle != null && belongCommunity != null) {
            mCommunityTitle.setText("Do you want to join \n" + belongCommunity.getString("title") + "?");
            mCommunityBadge.setImageDrawable(badgeDrawables.get(belongCommunity.getObjectId()));
        }
    }
}