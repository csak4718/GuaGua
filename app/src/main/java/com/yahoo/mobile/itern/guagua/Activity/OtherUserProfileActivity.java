package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Event.CommentEvent;
import com.yahoo.mobile.itern.guagua.Event.OtherUserProfileEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import de.greenrobot.event.EventBus;

public class OtherUserProfileActivity extends ActionBarActivity {

    private ParseUser mUser;
    private String mUserId;
    private String mUserName;
    private Bitmap mUserProfileImg;


    private ImageView mImgProfilePic;
    private Button mBtnViewOnFb;

    public OtherUserProfileActivity() {
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mUserName);
        Utils.setCommunityActionBarColor(this);
    }


    private Intent getOpenFbIntent(String fbId) {
        String facebookUrl = "https://www.facebook.com/" + fbId;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + facebookUrl));
//            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + fbId));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
        }
    }

    private void setupViewOnFbButton() {
        String text = "View " + mUserName + " on Facebook";
        mBtnViewOnFb.setText(text);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(mUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    mUser = user;
                    final String fbId = user.getString(Common.OBJECT_USER_FB_ID);
                    mBtnViewOnFb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = getOpenFbIntent(fbId);
                            startActivity(it);
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = getIntent().getStringExtra(Common.EXTRA_USER_ID);
        mUserName = getIntent().getStringExtra(Common.EXTRA_USER_NICKNAME);
        mUserProfileImg = getIntent().getParcelableExtra(Common.EXTRA_USER_PROFILE_IMG);

        setupActionBar();

        setContentView(R.layout.activity_other_user_profile);

        mImgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);
        mBtnViewOnFb = (Button) findViewById(R.id.btn_view_other_on_fb);
        mImgProfilePic.setImageBitmap(mUserProfileImg);

        setupViewOnFbButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
