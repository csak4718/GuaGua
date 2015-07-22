package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    private Button mBtnLoginFacebook;
    private ImageView mImgSplashLogo;
    private Handler mHandler = new Handler();

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
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                            }
                            gotoMainActivity();
                        }
                    }
                });
            }
        });
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

    private void gotoMainActivity() {
        Intent it = new Intent(this, MainActivity.class);

        it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
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
                    gotoMainActivity();
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
