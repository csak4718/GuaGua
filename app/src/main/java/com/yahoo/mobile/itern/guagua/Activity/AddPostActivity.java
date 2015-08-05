package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.ShareDuringPostEvent;
import com.yahoo.mobile.itern.guagua.Fragment.AddPostActivityFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import de.greenrobot.event.EventBus;


public class AddPostActivity extends ActionBarActivity {

    private AddPostActivityFragment addPostFragment;

    //These variables are used to implement FB sharing during posting
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ShareDuringPostEvent event){
        // produce URL in onEvent()
        Log.d("enableFBshare = ", String.valueOf(addPostFragment.getEnableFBshare()));
        if (addPostFragment.getEnableFBshare()){
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("呱呱 - 投票結果")
                        .setContentDescription(event.mPost.getString("prayer"))
                        .setContentUrl(Uri.parse("https://aqueous-falls-3271.herokuapp" +
                                ".com/guagua/" + event.mPost.getObjectId() + "/results/"))
                        .build();

                Log.d("objectId = ", event.mPost.getObjectId());

                shareDialog.show(linkContent);
            }

        }
        finish();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Utils.setCommunityActionBarColor(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        setupActionBar();

        // Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            public void onSuccess(Sharer.Result results) {
            }

            public void onCancel() {
            }

            public void onError(FacebookException e) {
            }
        });

        addPostFragment = new AddPostActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_post_content_frame, addPostFragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            addPostFragment.addPost();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    // For changing camera_btn img
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null &&  resultCode == RESULT_OK && requestCode == AddPostActivityFragment.ACTIVITY_SELECT_IMAGE) {
            Uri selectedImageUri = data.getData();
            addPostFragment.setImgViewUpload(selectedImageUri);
        }
        else if(data != null &&  resultCode == RESULT_OK && requestCode == AddPostActivityFragment.CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            addPostFragment.setImgViewUpload(photo);
        }
    }
}
