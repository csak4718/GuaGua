package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by fanwang on 7/22/15.
 */
public class CommunityEvent {
    public final List<ParseObject> communityList;
    public CommunityEvent(List<ParseObject> list) {
        communityList = list;
    }
}
