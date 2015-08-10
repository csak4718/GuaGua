package com.yahoo.mobile.itern.guagua.Activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.itern.guagua.Adapter.CommunityAdapter;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.UserCommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.MainActivityFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    private RecyclerView communityRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private CommunityAdapter mCommunityAdapter;
    private RecyclerView.Adapter mWrappedCommunityAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private List<ParseObject> mList;

    private ActionBar mActionBar;
    private MainActivityFragment mainFragment;
    private Handler handler = new Handler();
    private Runnable filterRunnable;


    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }


    private void setupDrawerProfile() {

        ImageView imgProfile;
        TextView txtName;
        TextView txtViewPersonal;
        ParseUser user = ParseUser.getCurrentUser();

        imgProfile = (ImageView) findViewById(R.id.drawer_img_profile);
        txtName = (TextView) findViewById(R.id.drawer_txt_name);
        txtViewPersonal = (TextView) findViewById(R.id.drawer_txt_view_personal);

        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Uri imgUri = Uri.parse(imgFile.getUrl());

        txtName.setText(user.getString(Common.OBJECT_USER_NICK));
        txtViewPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, PersonalPageActivity.class);
                it.putExtra(Common.EXTRA_PERSONAL, Common.EXTRA_PERSONAL_FOLLOWING);
                startActivity(it);
            }
        });
        Picasso.with(this).load(imgUri.toString()).into(imgProfile);


    }


    private void setupEditModeButton() {
        final ImageButton imgBtnEdit;
        imgBtnEdit = (ImageButton) findViewById(R.id.img_btn_edit_mode);
        imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommunityAdapter.getEditMode()) {
                    imgBtnEdit.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_pen));
                } else {
                    imgBtnEdit.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                }
                mCommunityAdapter.toggleEditMode();
                mCommunityAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupDrawerLayout() {

        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        communityRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_community);
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mLayoutManager = new LinearLayoutManager(this);

        mList = new ArrayList<>();
        mCommunityAdapter = new CommunityAdapter(this, mList);
        mWrappedCommunityAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mCommunityAdapter);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        communityRecyclerView.setLayoutManager(mLayoutManager);
        communityRecyclerView.setAdapter(mWrappedCommunityAdapter);
        communityRecyclerView.setItemAnimator(animator);

        mRecyclerViewDragDropManager.attachRecyclerView(communityRecyclerView);

        setupDrawerProfile();
        setupEditModeButton();

    }

    private void setupActionBar() {

        mActionBar = getSupportActionBar();
        mActionBar.setElevation(0);

        Utils.setCommunityActionBarColor(this);
        Utils.setCommunityStatusBarColor(this);

        ParseUtils.getUserCommunity(ParseUser.getCurrentUser());

        ParseObject community = ((MainApplication) getApplication()).currentViewingCommunity;
        if(community != null) {
            mActionBar.setTitle(community.getString(Common.OBJECT_COMMUNITY_TITLE));
        }

    }

    public void onEvent(UserCommunityEvent event) {
        Log.d("eventbus", "user community event" + event.communityList.size());
        mList.clear();
        mList.addAll(event.communityList);

        Map<String, Integer> communityOrder = mCommunityAdapter.getCommunityOrder();
        for(int i = 0; i < mList.size(); i++) {
            ParseObject community = mList.get(i);
            String key = community.getObjectId();
            if(communityOrder.containsKey(key)) {
                int toIndex = communityOrder.get(key);
                Collections.swap(mList, i, toIndex);
            }
        }
        mCommunityAdapter.updateCommunityOrder();
        mCommunityAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (communityRecyclerView != null) {
            communityRecyclerView.setItemAnimator(null);
            communityRecyclerView.setAdapter(null);
            communityRecyclerView = null;
        }

        if (mWrappedCommunityAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedCommunityAdapter);
            mWrappedCommunityAdapter = null;
        }
        mCommunityAdapter = null;
        mLayoutManager = null;
        super.onDestroy();
    }

    private void launchFromNotification(Intent it) {
        Uri uri = it.getData();
        String objectId = uri.getPath().substring(1);
        Log.d("Question id", objectId);
        ParseUtils.getQuestion(objectId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        setupActionBar();
        setupDrawerLayout();

        mainFragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mainFragment)
                .commit();

        Intent it = getIntent();
        if(it != null && it.getAction() != null && it.getAction().equals("android.intent.action.VIEW")) {
            launchFromNotification(it);
        }
        else {
            ParseUtils.getCurrentCommunityQuestions(this);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mainFragment.setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                handler.removeCallbacks(filterRunnable);
                filterRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mainFragment.setFilter(newText);
                    }
                };
                handler.postDelayed(filterRunnable, 300);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
