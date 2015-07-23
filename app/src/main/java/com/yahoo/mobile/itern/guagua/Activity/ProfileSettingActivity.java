package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.itern.guagua.Event.FbPictureEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.FbUtils;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.io.ByteArrayOutputStream;

import de.greenrobot.event.EventBus;

public class ProfileSettingActivity extends ActionBarActivity {

    ParseUser user;
    Button mBtnLogout;
    Button mBtnSaveProfile;
    EditText mEdtNickName;
    ImageView mImgProfilePic;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();

        setContentView(R.layout.activity_profile_setting);
        mImgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);
        mBtnLogout = (Button) findViewById(R.id.btn_log_out);
        mBtnSaveProfile = (Button) findViewById(R.id.btn_save_profile);
        mEdtNickName = (EditText) findViewById(R.id.edt_setting_nickname);

        Intent it = getIntent();

        final String classFrom = it.getStringExtra("classFrom");
        String nickName = "";

        if(classFrom != null && classFrom.equals(LoginActivity.class.toString())) {
            nickName = it.getStringExtra("nickname");
            String mFbId = it.getStringExtra("id");
            FbUtils.getFbProfilePicture(mFbId);
        }
        else {
            nickName = user.getString(Common.OBJECT_USER_NICK);
            ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
            Log.d("parse imgfile url", imgFile.getUrl());
            Uri imgUri = Uri.parse(imgFile.getUrl());
            if(mImgProfilePic != null) {
                Picasso.with(this).load(imgUri.toString()).into(mImgProfilePic);
            }
        }


        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.userLogout(ProfileSettingActivity.this);
                ProfileSettingActivity.this.finish();
            }
        });
        mEdtNickName.setText(nickName);
        mBtnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nickName = mEdtNickName.getText().toString();

                Bitmap profilePic = ((BitmapDrawable)mImgProfilePic.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                profilePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytearray= stream.toByteArray();
                final ParseFile imgFile = new ParseFile(user.getUsername() + "_profile.jpg", bytearray);
                imgFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        user.put(Common.OBJECT_USER_NICK, nickName);
                        user.put(Common.OBJECT_USER_PROFILE_PIC, imgFile);
                        user.saveInBackground();
                    }
                });

                Utils.gotoMainActivity(ProfileSettingActivity.this);
                finish();
            }
        });

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

    public void onEvent(final FbPictureEvent event) {
        Log.d("eventbus", "get fb pic");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mImgProfilePic.setImageBitmap(event.mPic);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
