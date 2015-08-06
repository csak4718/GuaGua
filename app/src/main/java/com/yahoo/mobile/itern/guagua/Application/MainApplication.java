package com.yahoo.mobile.itern.guagua.Application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;


/**
 * Created by cmwang on 7/16/15.
 */
public class MainApplication extends Application {

    public ParseObject currentViewingCommunity = null;

    @Override
    public void onCreate() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "iMyUdfPQnXeU1bTHi3f8jhRw5oCx40UxvMfcicno", "fwtpApBFDvfTtUHJ5nwrdqD8y5lVoU3nePIQmW6k");
        ParseFacebookUtils.initialize(this);

        FlurryAgent.init(this, "G4GPJ92FFBWHGZCH8WCK");
        super.onCreate();
    }
}
