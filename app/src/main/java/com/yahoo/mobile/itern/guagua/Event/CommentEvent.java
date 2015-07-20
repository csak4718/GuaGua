package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentEvent {
    public final List<ParseObject> commentList;
    public CommentEvent(List<ParseObject> list) {
        commentList = list;
    }
}
