package com.yahoo.mobile.itern.guagua.Activity;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yahoo.mobile.itern.guagua.Fragment.MainActivityFragment;
import com.yahoo.mobile.itern.guagua.R;


public class MainActivity extends ActionBarActivity {

    private LinearLayout mBannerBadge;

    private void setupActionBar() {
        View actionBarView = getLayoutInflater().inflate(R.layout.actionbar_main, null);
        Button btnActionBarTitle = (Button) actionBarView.findViewById(R.id.btn_action_bar_title);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView,
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        actionBar.setBackgroundDrawable(new ColorDrawable(R.color.purple));

        btnActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBannerBadge.getVisibility() == View.GONE) {
                    mBannerBadge.setVisibility(View.VISIBLE);
                }
                else {
                    mBannerBadge.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerBadge = (LinearLayout) findViewById(R.id.banner_badge);
        setupActionBar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MainActivityFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
