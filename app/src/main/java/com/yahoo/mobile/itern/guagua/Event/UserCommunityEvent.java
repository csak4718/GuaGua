package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/24/15.
 */
public class UserCommunityEvent {
    public final List<ParseObject> communityList;
    public UserCommunityEvent(List<ParseObject> list) {
        communityList = list;
    }
}
