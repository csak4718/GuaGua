package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by dwkung on 7/30/15.
 */
public class CommentSizeEvent {
    public final List<ParseObject> commentList;
    public CommentSizeEvent(List<ParseObject> list) {
        commentList = list;
    }
}
