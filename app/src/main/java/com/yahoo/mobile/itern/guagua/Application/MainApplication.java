package com.yahoo.mobile.itern.guagua.Application;

import android.app.Application;
import android.util.Log;
import android.content.res.Configuration;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.Util.Common;

import java.util.List;
import java.util.Locale;


/**
 * Created by cmwang on 7/16/15.
 */
public class MainApplication extends Application {

    public ParseObject currentViewingCommunity = null;

    @Override
    public void onCreate() {
        setLocale();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "iMyUdfPQnXeU1bTHi3f8jhRw5oCx40UxvMfcicno", "fwtpApBFDvfTtUHJ5nwrdqD8y5lVoU3nePIQmW6k");
        ParseFacebookUtils.initialize(this);

        ParsePush.subscribeInBackground("");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        FlurryAgent.init(this, "G4GPJ92FFBWHGZCH8WCK");
        super.onCreate();
    }

    public void setLocale() {
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        overwriteConfigurationLocale(config, locale);
    }

    private void overwriteConfigurationLocale(Configuration config, Locale locale) {
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
