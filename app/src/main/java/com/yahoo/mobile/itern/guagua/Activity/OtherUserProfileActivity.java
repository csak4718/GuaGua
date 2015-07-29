package com.yahoo.mobile.itern.guagua.Activity;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;

public class OtherUserProfileActivity extends ActionBarActivity {

    private String mUserName;
    private Bitmap mProfileImg;
    private ImageView mImgProfilePic;
    private Button mBtnViewOnFb;

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mUserName);
        Utils.setCommunityActionBarColor(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserName = getIntent().getStringExtra(Common.EXTRA_USER_NICKNAME);
        mProfileImg = getIntent().getParcelableExtra(Common.EXTRA_USER_PROFILE_IMG);

        setupActionBar();

        setContentView(R.layout.activity_other_user_profile);

        mImgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);
        mBtnViewOnFb = (Button) findViewById(R.id.btn_view_other_on_fb);
        mImgProfilePic.setImageBitmap(mProfileImg);

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
