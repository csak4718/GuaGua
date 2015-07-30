package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

/**
 * Created by dwkung on 7/30/15.
 */
public class ShareDuringPostEvent {
    public final ParseObject mPost;
    public ShareDuringPostEvent(ParseObject post){
        mPost = post;
    }
}
