package com.yahoo.mobile.itern.guagua.Application;

import android.app.Application;

import com.parse.Parse;


/**
 * Created by cmwang on 7/16/15.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "iMyUdfPQnXeU1bTHi3f8jhRw5oCx40UxvMfcicno", "fwtpApBFDvfTtUHJ5nwrdqD8y5lVoU3nePIQmW6k");
        super.onCreate();
    }
}
