package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


    private Intent getOpenFbIntent(String fbId) {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + fbId));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + fbId));
        }
    }

    private void setupViewOnFbButton() {
        String text = "View " + mUserName + " on Facebook";
        mBtnViewOnFb.setText(text);
        mBtnViewOnFb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = getOpenFbIntent("");
                startActivity(it);
            }
        });
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
