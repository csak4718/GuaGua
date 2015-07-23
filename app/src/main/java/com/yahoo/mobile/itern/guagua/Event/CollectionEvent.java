package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/20/15.
 */
public class CollectionEvent {
    public final List<ParseObject> collectionList;
    public CollectionEvent(List<ParseObject> list) {
        collectionList = list;
    }
}
