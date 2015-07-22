package com.yahoo.mobile.itern.guagua.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseKeys;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

public class ProfileSettingActivity extends ActionBarActivity {

    ParseUser user;
    Button mBtnLogout;
    Button mBtnSaveProfile;
    EditText mEdtNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        final String nickName = getIntent().getStringExtra("nickname");

        setContentView(R.layout.activity_profile_setting);
        mBtnLogout = (Button) findViewById(R.id.btn_log_out);
        mBtnSaveProfile = (Button) findViewById(R.id.btn_save_profile);
        mEdtNickName = (EditText) findViewById(R.id.edt_setting_nickname);
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
                String nickName = mEdtNickName.getText().toString();
                user.put(ParseKeys.OBJECT_USER_NICK, nickName);
                user.saveInBackground();
                Utils.gotoMainActivity(ProfileSettingActivity.this);
                finish();
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
