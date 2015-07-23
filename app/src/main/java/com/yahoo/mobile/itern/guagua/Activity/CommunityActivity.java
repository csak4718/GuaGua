package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yahoo.mobile.itern.guagua.Fragment.CommunityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.MapFragment;
import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by fanwang on 7/22/15.
 */
public class CommunityActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, new CommunityFragment())
                .commit();
    }

    public void switchToMapFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.community_content, new MapFragment())
                .commit();
    }

    public void gotoMainActivity() {
        Intent it = new Intent(this, MainActivity.class);

        it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }

}