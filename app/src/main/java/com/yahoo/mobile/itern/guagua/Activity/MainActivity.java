package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.UserCommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.MainActivityFragment;

import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.ActionBarTitle;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    private HorizontalScrollView mScrollBannerBadge;
    private LinearLayout mBannerBadge;
    private MainActivityFragment mainFragment;
    private Handler handler = new Handler();
    private Runnable filterRunnable;
    private Button mImgBtnBadgeSearch;
    private Button mBtnBadgeAll;
    private Button mBtnBadgeTaiwan;
    private ActionBarTitle mActionBarTitle;
    private boolean badgeBannerVisible = false;

    private void setupActionBar() {
        mActionBarTitle = new ActionBarTitle(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
        actionBar.setCustomView(mActionBarTitle);

        Utils.setCommunityActionBarColor(this);

        ParseUtils.getUserCommunity(ParseUser.getCurrentUser());

        ParseObject community = ((MainApplication) getApplication()).currentViewingCommunity;
        if(community != null) {
            mActionBarTitle.setText(community.getString(Common.OBJECT_COMMUNITY_TITLE));
        }

        mActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!badgeBannerVisible) {
                    showBdgeBanner(300);
                } else {
                    hideBdgeBanner(300);
                }
            }
        });
        mImgBtnBadgeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoCommunityActivity(MainActivity.this);
            }
        });
        mBtnBadgeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication app = (MainApplication)getApplication();
                if(app.currentViewingCommunity != null) {
                    ParseUtils.getAllQuestions();
                    app.currentViewingCommunity = null;
                    mActionBarTitle.setText(getString(R.string.app_name));
                    Utils.setCommunityActionBarColor(MainActivity.this);
                    hideBdgeBanner(300);
                }
            }
        });

        // Get Taiwan Community
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_COMMUNITY);
        query.getInBackground("wtgxgSpmNH", new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject community, ParseException e) {
                if (e == null) {
                    mBtnBadgeTaiwan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParseUtils.getCommunityQuestions(community);
                            MainApplication app = (MainApplication) getApplication();
                            ParseUser user = ParseUser.getCurrentUser();
                            app.currentViewingCommunity = community;
                            user.put(Common.OBJECT_USER_LAST_VIEWING_COMMUNITY, community);
                            user.saveInBackground();

                            mActionBarTitle.setText(community.getString(Common.OBJECT_COMMUNITY_TITLE));
                            Utils.setCommunityActionBarColor(MainActivity.this);

                            hideBdgeBanner(300);
                        }
                    });
                }
            }
        });
    }

    private void hideBdgeBanner(int duration) {

        mActionBarTitle.animateCollapse();
        mScrollBannerBadge.setVisibility(View.GONE);

//        float scale = getResources().getDisplayMetrics().density;
//        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -100 * scale);
//        animation.setDuration(duration);
//        animation.setFillAfter(true);
//        animation.setFillEnabled(true);
//        mBannerBadge.startAnimation(animation);
        badgeBannerVisible = false;
    }
    private void showBdgeBanner(int duration) {

        mActionBarTitle.animateExpand();
        mScrollBannerBadge.setVisibility(View.VISIBLE);

//        float scale = getResources().getDisplayMetrics().density;
//        TranslateAnimation animation = new TranslateAnimation(0, 0, -100 * scale, 0);
//        animation.setDuration(duration);
//        animation.setFillAfter(true);
//        animation.setFillEnabled(true);
//        mBannerBadge.startAnimation(animation);
        badgeBannerVisible = true;
    }

    private void setBadgeBackground(final Button button, ParseFile logo, ParseObject community) {
        logo.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        button.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
                    }
                }
            }
        });
    }

    private Button createNewBadge(final ParseObject community) {
        String title = community.getString(Common.OBJECT_COMMUNITY_TITLE);
        Button button = new Button(this);
        button.setTextColor(Color.WHITE);
        button.setGravity(Gravity.CENTER);
        button.setPadding(0, 0, 0, 0);

        ParseFile logo = community.getParseFile(Common.OBJECT_COMMUNITY_LOGO_SMALL);
        if(logo != null) {
            setBadgeBackground(button, logo, community);
        }
        else {
            button.setBackgroundResource(R.drawable.badge);
            button.setText(title);
        }

        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (50 * scale + 0.5f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pixels, pixels);
        params.gravity = Gravity.CENTER;
        int marginPixels = (int)(5 * scale + 0.5f);
        params.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
        button.setLayoutParams(params);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ParseUtils.getCommunityQuestions(community);
                MainApplication app = (MainApplication)getApplication();
                ParseUser user = ParseUser.getCurrentUser();
                app.currentViewingCommunity = community;
                user.put(Common.OBJECT_USER_LAST_VIEWING_COMMUNITY, community);
                user.saveInBackground();

                mActionBarTitle.setText(community.getString(Common.OBJECT_COMMUNITY_TITLE));
                Utils.setCommunityActionBarColor(MainActivity.this);

                hideBdgeBanner(300);
            }
        });

        return button;
    }

    public void onEvent(UserCommunityEvent event) {
        Log.d("eventbus", "user community event" + event.communityList.size());
        for(int i = mBannerBadge.getChildCount() - 1; i >= 3; i--) {
            mBannerBadge.removeViewAt(i);
        }
        for(ParseObject community : event.communityList) {
            mBannerBadge.addView(createNewBadge(community));
        }
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
        mScrollBannerBadge = (HorizontalScrollView) findViewById(R.id.scroll_banner_badge);
        mBannerBadge = (LinearLayout) findViewById(R.id.banner_badge);

        mImgBtnBadgeSearch = (Button) findViewById(R.id.img_btn_badge_search);
        mBtnBadgeAll = (Button) findViewById(R.id.btn_badge_all);
        mBtnBadgeTaiwan = (Button) findViewById(R.id.btn_badge_taiwan);

        setupActionBar();
        mainFragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mainFragment)
                .commit();
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
        int id = item.getItemId();


        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_profile) {
            //startActivity(new Intent(this, ProfileSettingActivity.class));
            startActivity(new Intent(this, PersonalPageActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
