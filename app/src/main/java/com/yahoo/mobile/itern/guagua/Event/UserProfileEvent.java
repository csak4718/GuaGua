package com.yahoo.mobile.itern.guagua.Event;

/**
 * Created by cmwang on 7/22/15.
 */
public class UserProfileEvent {
    public String mFbId;
    public String mNickName;
    public UserProfileEvent(String fbId, String nickName) {
        mFbId = fbId;
        mNickName = nickName;
    }
}
