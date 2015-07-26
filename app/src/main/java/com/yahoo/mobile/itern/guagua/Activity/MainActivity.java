package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Event.UserCommunityEvent;
import com.yahoo.mobile.itern.guagua.Fragment.MainActivityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.PostFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.ActionBarTitle;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    private LinearLayout mBannerBadge;
    private MainActivityFragment mainFragment;
    private Handler handler = new Handler();
    private Runnable filterRunnable;
    private ImageButton mImgBtnBadgeSearch;
    private boolean badgeBannerVisible = false;

    private void setupActionBar() {
        final ActionBarTitle actionBarTitle = new ActionBarTitle(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(R.color.purple));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarTitle);
        hideBdgeBanner(0);

        ParseUtils.getUserCommunity(ParseUser.getCurrentUser());

        actionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!badgeBannerVisible) {
                    actionBarTitle.animateExpand();
                    showBdgeBanner(300);
                }
                else {
                    actionBarTitle.animateCollapse();
                    hideBdgeBanner(300);

                }
            }
        });
        mImgBtnBadgeSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utils.gotoCommunityActivity(MainActivity.this);
            }
        });
    }

    private void hideBdgeBanner(int duration) {
        float scale = getResources().getDisplayMetrics().density;
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -100 * scale);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        mBannerBadge.startAnimation(animation);
        badgeBannerVisible = false;
    }
    private void showBdgeBanner(int duration) {
        float scale = getResources().getDisplayMetrics().density;
        TranslateAnimation animation = new TranslateAnimation(0, 0, -100 * scale, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        mBannerBadge.startAnimation(animation);
        badgeBannerVisible = true;
    }

    public void onEvent(UserCommunityEvent event) {
        Log.d("eventbus", "user community event" + event.communityList.size());
        for(int i = mBannerBadge.getChildCount() - 1; i >= 2; i--) {
            mBannerBadge.removeViewAt(i);
        }
        for(ParseObject community : event.communityList) {
            mBannerBadge.addView(Utils.createNewBadge(this, community.getString(Common.OBJECT_COMMUNITY_TITLE)));
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
        mBannerBadge = (LinearLayout) findViewById(R.id.banner_badge);
        mImgBtnBadgeSearch = (ImageButton) findViewById(R.id.img_btn_badge_search);
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
            startActivity(new Intent(this, ProfileSettingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    // For changing camera_btn img
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // To Handle Gallery Result
        Log.d("Photo", "in result");
        Log.d("Photo", String.valueOf(data == null));
        Log.d("Photo", String.valueOf(requestCode));
        if (data != null && requestCode == 1234) {
            Log.d("Photo", "in result");
            Uri selectedImageUri = data.getData();
            Log.d("Photo", "getdata");
            String[] fileColumn = { MediaStore.Images.Media.DATA };
            Log.d("Photo", "1");
            Cursor imageCursor = getContentResolver().query(selectedImageUri,
                    fileColumn, null, null, null);
            imageCursor.moveToFirst();
            int ori = imageCursor.getInt(0);
            Log.d("Photo",String.valueOf(ori));
            Log.d("Photo", "2");
            int fileColumnIndex = imageCursor.getColumnIndex(fileColumn[0]);
            String picturePath = imageCursor.getString(fileColumnIndex);
            Log.d("Photo", picturePath);
            //Bitmap pictureObject = BitmapFactory.decodeFile(picturePath);

            PostFragment myf = (PostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            myf.change_Image(picturePath);

        }
    }
}
