package com.yahoo.mobile.itern.guagua.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.Event.CollectionEvent;
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
        mComment.put(Common.OBJECT_COMMENT_USER, ParseUser.getCurrentUser());
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
            public void done(List<ParseObject> communityList, ParseException e) {
                if (e == null) {
                    Log.d("communities", "Retrieved " + communityList.size() + " communities");
                    EventBus.getDefault().post(new CommunityEvent(communityList));
                } else {
                    Log.d("communities", "Error: " + e.getMessage());
                }
            }
        });
    }
    static public void displayImage(ParseFile img, final ImageView imgView) {
        img.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        imgView.setImageBitmap(bmp);
                    }
                }
            }
        });
    }
    static public void getAllCollections(String uid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Prayer");
        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Colleciton");

        query.orderByDescending("updatedAt");
        //query.whereEqualTo("uid",uid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> collectionList, ParseException e) {
                if (e == null) {
                    Log.d("collections", "Retrieved " + collectionList.size() + " collections");
                    EventBus.getDefault().post(new CollectionEvent(collectionList));
                } else {
                    Log.d("collections", "Error: " + e.getMessage());
                }
            }
        });
    }
}
