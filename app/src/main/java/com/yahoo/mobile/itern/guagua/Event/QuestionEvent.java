package com.yahoo.mobile.itern.guagua.Event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by cmwang on 7/16/15.
 */
public class QuestionEvent {
    public final List<ParseObject> questionList;
    public QuestionEvent(List<ParseObject> list) {
        questionList = list;
    }
}
