package com.yahoo.mobile.itern.guagua.Util;

import android.app.Activity;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.Event.CollectionEvent;
import com.yahoo.mobile.itern.guagua.Event.CommentEvent;
import com.yahoo.mobile.itern.guagua.Event.CommunityEvent;
import com.yahoo.mobile.itern.guagua.Event.MyQuestionsEvent;
import com.yahoo.mobile.itern.guagua.Event.QuestionEvent;
import com.yahoo.mobile.itern.guagua.Event.UserCommunityEvent;

import java.io.ByteArrayOutputStream;
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

    static public void updateUserProfile(final String nickName, Bitmap profilePic) {
        final ParseUser user = ParseUser.getCurrentUser();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytearray= stream.toByteArray();
        final ParseFile imgFile = new ParseFile(user.getUsername() + "_profile.jpg", bytearray);
        imgFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put(Common.OBJECT_USER_NICK, nickName);
                user.put(Common.OBJECT_USER_PROFILE_PIC, imgFile);
                user.saveInBackground();
            }
        });

    }
    static public void getUserCommunity(ParseUser user) {
        ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_USER_COMMUNITY_RELATION);
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    EventBus.getDefault().post(new UserCommunityEvent(list));
                }
            }
        });
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

    static public void getCurrentCommunityQuestions(Activity activity) {
        MainApplication app = (MainApplication) activity.getApplication();
        getCommunityQuestions(app.currentViewingCommunity);
    }

    static public void getCommunityQuestions(ParseObject community) {

        if(community == null) {
            getAllQuestions();
            return;
        }

        ParseRelation<ParseObject> relation = community.getRelation(Common.OBJECT_COMMUNITY_POSTS);
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("questions", "Retrieved " + list.size() + " community questions");
                    EventBus.getDefault().post(new QuestionEvent(list));
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
        query.orderByAscending("updatedAt");
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

    static public void postQuestions(String question, String optionA, String optionB, final ParseObject community) {
        final ParseObject mPost = new ParseObject(Common.OBJECT_POST);
        final ParseUser user = ParseUser.getCurrentUser();
        mPost.put(Common.OBJECT_POST_CONTENT, question);
        mPost.put(Common.OBJECT_POST_QA, optionA);
        mPost.put(Common.OBJECT_POST_QB, optionB);
        mPost.put(Common.OBJECT_POST_QA_NUM, 0);
        mPost.put(Common.OBJECT_POST_QB_NUM, 0);
        mPost.put(Common.OBJECT_POST_USER, ParseUser.getCurrentUser());

        mPost.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(community != null) {
                    ParseRelation<ParseObject> relation = community.getRelation(Common.OBJECT_COMMUNITY_POSTS);
                    relation.add(mPost);
                    community.saveInBackground();
                }
                ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_POST_MQ);
                relation.add(mPost);
                user.saveInBackground();
            }
        });
    }
    static public void postComment(String comment, final String postId, final Boolean refreshList) {

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseObject mComment = new ParseObject(Common.OBJECT_COMMENT);
        mComment.put(Common.OBJECT_COMMENT_POSTID, postId);
        mComment.put(Common.OBJECT_COMMENT_MSG, comment);
        mComment.put(Common.OBJECT_COMMENT_USER, currentUser);
        mComment.put(Common.OBJECT_COMMENT_USER_ID, currentUser.getObjectId());
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
        query.whereNotEqualTo("objectId", "wtgxgSpmNH");
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
    static public void getAllCollections(ParseUser user) {
        ParseQuery<ParseObject> query = user.getRelation(Common.OBJECT_POST_LIKES).getQuery();
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

    static public void getMyQuestions(ParseUser user) {
        ParseQuery<ParseObject> query = user.getRelation(Common.OBJECT_POST_MQ).getQuery();
        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Colleciton");

        query.orderByDescending("updatedAt");
        //query.whereEqualTo("uid",uid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> myQuestionsList, ParseException e) {
                if (e == null) {
                    Log.d("myQuesions", "Retrieved " + myQuestionsList.size() + " collections");
                    EventBus.getDefault().post(new MyQuestionsEvent(myQuestionsList));
                } else {
                    Log.d("myQuesions", "Error: " + e.getMessage());
                }
            }
        });
    }

    static public void addCommunityToUser(final String communityObjectId){

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation(Common.OBJECT_USER_COMMUNITY_RELATION);
        relation.add(ParseObject.createWithoutData(Common.OBJECT_COMMUNITY, communityObjectId));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getUserCommunity(ParseUser.getCurrentUser());
            }
        });

    }
}
