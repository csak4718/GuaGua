package com.yahoo.mobile.itern.guagua.Activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
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
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    private RecyclerView communityRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private CommunityAdapter mAdapter;
    private List<ParseObject> mList;

    private ActionBar mActionBar;
    private MainActivityFragment mainFragment;
    private Handler handler = new Handler();
    private Runnable filterRunnable;

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }


    private void setupDrawerProfile() {
        LinearLayout mRoot;
        ImageView imgProfile;
        TextView txtName;
        ParseUser user = ParseUser.getCurrentUser();

        mRoot = (LinearLayout) findViewById(R.id.drawer_profile_root);
        imgProfile = (ImageView) findViewById(R.id.drawer_img_profile);
        txtName = (TextView) findViewById(R.id.drawer_txt_name);

        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Uri imgUri = Uri.parse(imgFile.getUrl());

        txtName.setText(user.getString(Common.OBJECT_USER_NICK));
        Picasso.with(this).load(imgUri.toString()).into(imgProfile);

        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileSettingActivity.class));
            }
        });
    }
    private void setupDrawerFollowing() {
        LinearLayout mRoot = (LinearLayout) findViewById(R.id.drawer_following_root);
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, PersonalPageActivity.class);
                it.putExtra(Common.EXTRA_PERSONAL, Common.EXTRA_PERSONAL_FOLLOWING);
                startActivity(it);
            }
        });

    }
    private void setupDrawerMyQuestion() {
        LinearLayout mRoot = (LinearLayout) findViewById(R.id.drawer_my_question_root);
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, PersonalPageActivity.class);
                it.putExtra(Common.EXTRA_PERSONAL, Common.EXTRA_PERSONAL_MY_QUESTION);
                startActivity(it);
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
        mLayoutManager = new LinearLayoutManager(this);
        mList = new ArrayList<>();
        mAdapter = new CommunityAdapter(this, mList);
        communityRecyclerView.setLayoutManager(mLayoutManager);
        communityRecyclerView.setAdapter(mAdapter);

        setupDrawerProfile();
        setupDrawerFollowing();
        setupDrawerMyQuestion();

    }

    private void setupActionBar() {

        mActionBar = getSupportActionBar();
        mActionBar.setElevation(0);

        Utils.setCommunityActionBarColor(this);

        ParseUtils.getUserCommunity(ParseUser.getCurrentUser());

        ParseObject community = ((MainApplication) getApplication()).currentViewingCommunity;
        if(community != null) {
            mActionBar.setTitle(community.getString(Common.OBJECT_COMMUNITY_TITLE));
        }

//

    }

    public void onEvent(UserCommunityEvent event) {
        Log.d("eventbus", "user community event" + event.communityList.size());
        mList.clear();
        mList.addAll(event.communityList);
        mAdapter.notifyDataSetChanged();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
        setupDrawerLayout();
        mainFragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mainFragment)
                .commit();
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
