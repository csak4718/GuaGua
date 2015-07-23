package com.yahoo.mobile.itern.guagua.Util;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.Event.CommentEvent;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.Event.QuestionEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 7/16/15.
 */
public class ParseUtils {
    static public void testParse() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
    static public void getAllQuestions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_POST);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, ParseException e) {
                if (e == null) {
                    Log.d("questions", "Retrieved " + questionList.size() + " questions");
                    EventBus.getDefault().post(new QuestionEvent(questionList));
                } else {
                    Log.d("questions", "Error: " + e.getMessage());
                }
            }
        });
    }
    /*
     * get comments related to a question
     */
    static public void getPostComments(String postObjectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_COMMENT);
        query.whereEqualTo("PostId", postObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> commentList, ParseException e) {
                if (e == null) {
                    Log.d("comments", "Retrieved " + commentList.size() + " comments");
                    EventBus.getDefault().post(new CommentEvent(commentList));
                } else {
                    Log.d("comments", "Error: " + e.getMessage());
                }
            }
        });
    }
    static public void postQuestions(String question, String optionA, String optionB) {
        ParseObject mPost = new ParseObject(Common.OBJECT_POST);
        mPost.put(Common.OBJECT_POST_CONTENT, question);
        mPost.put(Common.OBJECT_POST_QA, optionA);
        mPost.put(Common.OBJECT_POST_QB, optionB);
        mPost.put(Common.OBJECT_POST_QA_NUM, 0);
        mPost.put(Common.OBJECT_POST_QB_NUM, 0);
        mPost.put(Common.OBJECT_POST_USER, ParseUser.getCurrentUser());
        mPost.saveInBackground();
    }
    static public void postComment(String comment, final String postId, final Boolean refreshList) {
        ParseObject mComment = new ParseObject(Common.OBJECT_COMMENT);
        mComment.put(Common.OBJECT_COMMENT_POSTID, postId);
        mComment.put(Common.OBJECT_COMMENT_MSG, comment);
        mComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (refreshList) {
                        getPostComments(postId);
                    }
                } else {

                }
            }
        });
    }

    static public void getAllCommunities() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Community");
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, ParseException e) {
                if (e == null) {
                    Log.d("communities", "Retrieved " + questionList.size() + " communities");
                    EventBus.getDefault().post(new CommunityEvent(questionList));
                } else {
                    Log.d("communities", "Error: " + e.getMessage());
                }
            }
        });


    }
}
