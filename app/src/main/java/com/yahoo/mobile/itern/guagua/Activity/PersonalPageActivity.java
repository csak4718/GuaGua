package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.yahoo.mobile.itern.guagua.Fragment.MyFavoriteFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;

public class PersonalPageActivity extends ActionBarActivity {

    public String tab;

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Utils.setCommunityActionBarColor(this);
        Utils.setCommunityStatusBarColor(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        setupActionBar();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                tab = null;
            } else {
                tab = extras.getString(Common.EXTRA_PERSONAL);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MyFavoriteFragment fragment = MyFavoriteFragment.newInstance(tab);
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        } else{
            tab = (String) savedInstanceState.getSerializable(Common.EXTRA_PERSONAL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_page, menu);
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
            startActivity(new Intent(this, ProfileSettingActivity.class));
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
