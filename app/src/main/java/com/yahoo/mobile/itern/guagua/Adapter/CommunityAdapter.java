package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.itern.guagua.Activity.MainActivity;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.DrawerItemCommunity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmwang on 8/3/15.
 */
public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>
                        implements DraggableItemAdapter<CommunityAdapter.ViewHolder> {

    MainApplication mApp;
    MainActivity mActivity;
    List<ParseObject> mList;
    ParseUser mUser;

    Map<String, Integer> communityOrder;
    Gson gson;
    SharedPreferences mPref;

    boolean mEditMode = false;

    public void setEditMode(boolean enabled) {
        mEditMode = enabled;
    }
    public boolean getEditMode() {
        return mEditMode;
    }
    public void toggleEditMode() {
        mEditMode = !mEditMode;
    }

    public Map<String, Integer> getCommunityOrder() {
        return communityOrder;
    }

    public void updateCommunityOrder() {
        communityOrder.clear();
        for(int i = 0; i < mList.size(); i++) {
            ParseObject community = mList.get(i);
            communityOrder.put(community.getObjectId(), i);
        }
        String serializedCommunityOrder = gson.toJson(communityOrder);
        mPref.edit()
                .putString(Common.SHARED_COMMUNITY_KEY_ORDER, serializedCommunityOrder)
                .commit();
    }

    public CommunityAdapter(AppCompatActivity activity, List<ParseObject> list) {
        mActivity = (MainActivity) activity;
        mApp = (MainApplication) mActivity.getApplication();
        mUser = ParseUser.getCurrentUser();
        mList = list;

        mPref = mActivity.getSharedPreferences(Common.SHARED_COMMUNITY_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        if(mPref.contains(Common.SHARED_COMMUNITY_KEY_ORDER)) {
            String record = mPref.getString(Common.SHARED_COMMUNITY_KEY_ORDER, "");
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            communityOrder = gson.fromJson(record, type);
        }
        else {
            communityOrder = new HashMap<>();
        }

        setHasStableIds(true);
    }

    private boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx - 20;
        final int right = v.getRight() + tx + 20;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        if(!mEditMode ||  position == 0 || position == 1 || position == mList.size() + 2) {
            return false;
        }
        final View containerView = holder.item.getContainerView();
        final View dragHandleView = holder.item.getHandleView();

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder viewHolder, int i) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition || toPosition == 0 || toPosition == 1 || toPosition == mList.size() + 2) {
            return;
        }
        int fromIndex = fromPosition - 2;
        int toIndex = toPosition - 2;
        if(fromIndex < toIndex) {
            ParseObject from = mList.get(fromIndex);
            for(int i = fromIndex; i < toIndex; i++) {
                mList.set(i, mList.get(i + 1));
            }
            mList.set(toIndex, from);
        }
        else {
            ParseObject from = mList.get(fromIndex);
            for(int i = fromIndex; i > toIndex; i--) {
                mList.set(i, mList.get(i - 1));
            }
            mList.set(toIndex, from);
        }
        updateCommunityOrder();
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends AbstractDraggableItemViewHolder {

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(position == 0) {
            holder.item.setTitle("呱呱");
            holder.item.hideHandle();
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
            holder.item.hideHandle();
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
            holder.item.hideHandle();
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

            final int index = position - 2;
            final ParseObject community = mList.get(index);

            if(!mEditMode) {
                holder.item.setIcon(mActivity.getResources().getDrawable(R.drawable.pin));
                holder.item.hideHandle();
                holder.item.setIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
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
            else {
                Picasso.with(mActivity)
                        .load(R.drawable.delete)
                        .into(holder.item.mImgIcon);
                holder.item.showHandle();
                // delete a community
                holder.item.setIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseUtils.removeCommunityFromCurrentUser(community);
                        mList.remove(index);
                        updateCommunityOrder();
                        notifyItemRemoved(position);
                    }
                });
                holder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
            }


            final String title = community.getString(Common.OBJECT_COMMUNITY_TITLE);
            holder.item.setTitle(title);

        }

    }

    @Override
    public int getItemCount() {
        return mList.size() + 3;
    }

    @Override
    public long getItemId(int position) {
        if(position == 0) {
            return "fuck".hashCode();
        }
        else if(position == 1) {
            return "fuck1".hashCode();
        }
        else if(position == mList.size() + 2) {
            return "fuck2".hashCode();
        }
        else {
            final int index = position - 2;
            return mList.get(index).hashCode();
        }
    }

}
