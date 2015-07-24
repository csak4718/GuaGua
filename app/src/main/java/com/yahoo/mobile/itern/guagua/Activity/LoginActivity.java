package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Event.FbPictureEvent;
import com.yahoo.mobile.itern.guagua.Event.UserProfileEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.FbUtils;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class LoginActivity extends ActionBarActivity {

    private Button mBtnLoginFacebook;
    private ImageView mImgSplashLogo;
    private Handler mHandler = new Handler();
    private String mNickName;
    private String mFbId;

    private void setupLoginButton() {

        mBtnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
//                permissions.add("user_status");
                permissions.add("user_friends");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                FbUtils.getUserProfile(AccessToken.getCurrentAccessToken());
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                Utils.gotoCommunityActivity(LoginActivity.this);
                                finish();
                            }

                        }
                    }
                });
            }
        });
    }

    public void onEvent(UserProfileEvent event) {
        Log.d("eventbus", "Get userprofile event");
//        Intent it = new Intent(LoginActivity.this, ProfileSettingActivity.class);
//        it.putExtra("classFrom", LoginActivity.class.toString());
//        it.putExtra("id", event.mFbId);
//        it.putExtra("nickname", event.mNickName);
//        startActivity(it);
//        finish();
        mNickName = event.mNickName;
        mFbId = event.mFbId;
        FbUtils.getFbProfilePicture(mFbId);
        startActivity(new Intent(this, CommunityActivity.class));
        finish();
    }
    public void onEvent(final FbPictureEvent event) {
        ParseUtils.updateUserProfile(mNickName, event.mPic);
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

    private void showLoginAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -200);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBtnLoginFacebook.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImgSplashLogo.setAnimation(animation);

        animation.startNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mImgSplashLogo = (ImageView) findViewById(R.id.img_splash_logo);

        if(ParseUser.getCurrentUser() == null) {
            setupLoginButton();
            showLoginAnimation();
        }
        else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.gotoCommunityActivity(LoginActivity.this);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
