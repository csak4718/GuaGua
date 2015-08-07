package com.yahoo.mobile.itern.guagua.Application;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;


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

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });



        FlurryAgent.init(this, "G4GPJ92FFBWHGZCH8WCK");
        super.onCreate();
    }
}
