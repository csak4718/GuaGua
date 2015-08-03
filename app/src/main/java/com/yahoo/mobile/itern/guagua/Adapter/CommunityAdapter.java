package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Activity.MainActivity;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.DrawerItemCommunity;

import java.util.List;

/**
 * Created by cmwang on 8/3/15.
 */
public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    MainApplication mApp;
    MainActivity mActivity;
    List<ParseObject> mList;
    ParseUser mUser;

    public CommunityAdapter(AppCompatActivity activity, List<ParseObject> list) {
        mActivity = (MainActivity) activity;
        mApp = (MainApplication) mActivity.getApplication();
        mUser = ParseUser.getCurrentUser();
        mList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public DrawerItemCommunity item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (DrawerItemCommunity) itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new DrawerItemCommunity(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(position == 0) {
            holder.item.setTitle("呱呱");
            holder.item.setIcon(mActivity.getResources().getDrawable(R.drawable.pin));
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mApp.currentViewingCommunity != null) {
                        ParseUtils.getAllQuestions();
                        mApp.currentViewingCommunity = null;
                        mActivity.getSupportActionBar().setTitle(mActivity.getString(R.string.app_name));
                        Utils.setCommunityActionBarColor(mActivity);
                        mActivity.closeDrawer();
                    }
                }
            });
        }
        else if(position == 1) {
            holder.item.setTitle("Taiwan");
            holder.item.setIcon(mActivity.getResources().getDrawable(R.drawable.pin));
            // Get Taiwan Community
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_COMMUNITY);
            query.getInBackground("wtgxgSpmNH", new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject community, ParseException e) {
                    if (e == null) {
                        holder.item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ParseUtils.getCommunityQuestions(community);

                                mApp.currentViewingCommunity = community;
                                mUser.put(Common.OBJECT_USER_LAST_VIEWING_COMMUNITY, community);
                                mUser.saveInBackground();

                                mActivity.getSupportActionBar().setTitle(community.getString(Common.OBJECT_COMMUNITY_TITLE));
                                Utils.setCommunityActionBarColor(mActivity);
                                mActivity.closeDrawer();
                            }
                        });
                    }
                }
            });
        }
        else if(position == mList.size() + 2) {
            holder.item.setIcon(mActivity.getResources().getDrawable(R.drawable.pin_explore));
            holder.item.setTitle("Explore");
            holder.item.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Utils.gotoCommunityActivity(mActivity);
                    mActivity.closeDrawer();
                }
            });
        }
        else {
            final ParseObject community = mList.get(position - 2);
            holder.item.setIcon(mActivity.getResources().getDrawable(R.drawable.pin));
            final String title = community.getString(Common.OBJECT_COMMUNITY_TITLE);
            holder.item.setTitle(title);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUtils.getCommunityQuestions(community);
                    mApp.currentViewingCommunity = community;
                    mUser.put(Common.OBJECT_USER_LAST_VIEWING_COMMUNITY, community);
                    mUser.saveInBackground();

                    mActivity.getSupportActionBar().setTitle(community.getString(Common.OBJECT_COMMUNITY_TITLE));
                    Utils.setCommunityActionBarColor(mActivity);
                    mActivity.closeDrawer();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size() + 3;
    }

}
