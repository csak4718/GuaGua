package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseUser;

/**
 * Created by cmwang on 7/29/15.
 */
public class OtherUserProfileEvent {
    public ParseUser mUser;

    public OtherUserProfileEvent(ParseUser user) {
        mUser = user;
    }
}
