package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.FbPictureEvent;
import com.yahoo.mobile.itern.guagua.Event.UserProfileEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.FbUtils;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class LoginActivity extends AppCompatActivity {

    private Button mBtnLoginFacebook;
    private ImageView mImgSplashLogo;
    private Handler mHandler = new Handler();
    private String mNickName;
    private String mFbId;

    private void gotoNextActivity() {
        Intent it = getIntent();
        if(it.getAction().equals("android.intent.action.VIEW")) {
            Intent notiIntent = new Intent(this, NotificationActivity.class);
            notiIntent.setData(it.getData());
            startActivity(notiIntent);
        }
        else {
            Utils.gotoMainActivity(this);
        }
        finish();
    }

    private void restoreUserSetting(ParseUser user) {
        final MainApplication app = (MainApplication)getApplication();
        final ParseObject community = user.getParseObject(Common.OBJECT_USER_LAST_VIEWING_COMMUNITY);
        if(community != null) {
            community.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    app.currentViewingCommunity = parseObject;
                    gotoNextActivity();
                }
            });
        }
        else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.gotoMainActivity(LoginActivity.this);
                    gotoNextActivity();
                }
            }, 700);
        }
    }

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
                                ParseUtils.linkInstallationWithUser();
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                restoreUserSetting(user);
                                ParseUtils.linkInstallationWithUser();
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
    }
    public void onEvent(final FbPictureEvent event) {
        ParseUser.getCurrentUser().put(Common.OBJECT_USER_FB_NAME, mNickName);
        ParseUtils.updateUserProfile(mNickName, mFbId, event.mPic);
        startActivity(new Intent(this, CommunityActivity.class));
        finish();
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
        Utils.setCommunityStatusBarColor(this);

        setContentView(R.layout.activity_login);
        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mImgSplashLogo = (ImageView) findViewById(R.id.img_splash_logo);

        if(ParseUser.getCurrentUser() == null) {
            setupLoginButton();
            showLoginAnimation();
        }
        else {
            restoreUserSetting(ParseUser.getCurrentUser());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
