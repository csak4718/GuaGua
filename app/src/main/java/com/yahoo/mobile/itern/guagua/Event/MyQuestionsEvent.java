package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/20/15.
 */
public class MyQuestionsEvent {
    public final List<ParseObject> myQuestionsList;
    public MyQuestionsEvent(List<ParseObject> list) {
        myQuestionsList = list;
    }
}
