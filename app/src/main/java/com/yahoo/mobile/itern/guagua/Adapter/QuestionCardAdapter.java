package com.yahoo.mobile.itern.guagua.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.CommentButton2;
import com.yahoo.mobile.itern.guagua.View.LikeButton;
import com.yahoo.mobile.itern.guagua.View.OptionButton;
import com.yahoo.mobile.itern.guagua.View.ShareButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmwang on 7/16/15.
 */
public class QuestionCardAdapter extends RecyclerView.Adapter<QuestionCardAdapter.ViewHolder>
        implements SwipeableItemAdapter<QuestionCardAdapter.ViewHolder> {


    private Handler mHandler = new Handler();
    private List<ParseObject> mAllQuestionList, mVisibleQuestionList, mFavoriteList, mVotedQuestionList;
    private Context mContext;
    private LayoutInflater mInflater;

    private Map<String, Map<String, Object>> cachedQuestion;
    private boolean mAmin = false;

    ShareDialog shareDialog;
    private static ViewHolder currentholder;
    private static ParseObject currentQuestion;

    public void notifyDataSetChangedWithCache() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                cachedQuestion.clear();
                updateVotedQuestionListSync();
                notifyDataSetChanged();
            }
        });
    }

    public void updateVotedQuestionListSync() {
        try {
            List<ParseObject> list = ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_VOTED_QUESTIONS).getQuery().find();
            if(list != null) {
                mVotedQuestionList.clear();
                mVotedQuestionList.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFavoriteListSync() {
        try {
            List<ParseObject> list = ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_LIKES).getQuery().find();
            if(list != null) {
                mFavoriteList.clear();
                mFavoriteList.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateVotedQuestionListAsync() {
        ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_VOTED_QUESTIONS).getQuery().findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && list != null) {
                    mVotedQuestionList.clear();
                    mVotedQuestionList.addAll(list);
                    notifyDataSetChangedWithCache();
                }
            }
        });
    }

    public void updateFavoriteListAsync() {
        ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_LIKES).getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && list != null) {
                    mFavoriteList.clear();
                    mFavoriteList.addAll(list);
                    notifyDataSetChangedWithCache();
                }
            }
        });
    }

    public QuestionCardAdapter(Context context, List<ParseObject> list, CallbackManager
            callbackManager) {
        super();

        cachedQuestion = new HashMap<>();

        mContext = context;
        mAllQuestionList = list;
        mVisibleQuestionList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mVotedQuestionList = new ArrayList<>();

        updateFavoriteListSync();
        updateVotedQuestionListSync();

        mVisibleQuestionList.addAll(mAllQuestionList);
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);

        shareDialog = new ShareDialog((Activity)mContext);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            public void onSuccess(Sharer.Result results) {
                Log.d("QCA","onsharecallback");
                ParseUtils.addShareNum(currentQuestion);
                int shareNum = currentholder.shareBtnPost.getShareNum();
                currentholder.shareBtnPost.addShareNum();
                cachedQuestion.get(currentQuestion.getObjectId()).put(Common.QUESTION_CARD_SHARE_NUM, shareNum + 1);
            }

            public void onCancel() {
                Log.d("QCA","On cancel");
            }

            public void onError(FacebookException e) {
                Log.d("QCA",e.toString());
            }
        });
    }

    public void flushFilter() {
        mVisibleQuestionList.clear();
        mVisibleQuestionList.addAll(mAllQuestionList);
    }
    public void setFilter(String queryText) {
        mVisibleQuestionList.clear();
        for(ParseObject question : mAllQuestionList) {
            if(question.getString(Common.OBJECT_POST_CONTENT).contains(queryText)
                    || question.getString(Common.OBJECT_POST_QA).contains(queryText)
                    || question.getString(Common.OBJECT_POST_QB).contains(queryText))
            {
                mVisibleQuestionList.add(question);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public QuestionCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_question, parent, false);
        ViewHolder vh = new ViewHolder(v);
        setQuestionPictureOnClickListener(vh);

        return vh;
    }

    //warper for voting A or B
    private void voteQuestionForA(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB, Map<String, Object> cache){
        voteQuestion(mQuestion, holder, voteA + 1, voteB, "A", cache);
        cache.put(Common.QUESTION_CARD_VOTE_OPTION, "A");
    }

    private void voteQuestionForB(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB, Map<String, Object> cache){
        voteQuestion(mQuestion, holder, voteA, voteB + 1, "B", cache);
        cache.put(Common.QUESTION_CARD_VOTE_OPTION, "B");
    }
    private void skipVoteQuestion(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB, Map<String, Object> cache){
        voteQuestion(mQuestion, holder, voteA, voteB, "N", cache);
        cache.put(Common.QUESTION_CARD_VOTE_OPTION, "N");
    }

    private void voteQuestion(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB, String option, Map<String, Object> cache) {
        final String objectId = mQuestion.getObjectId();

        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int)(voteB * 100.0 / (voteA + voteB));

        if(option == "A") {
            mQuestion.increment(Common.OBJECT_POST_QA_NUM);
        }
        if(option == "B") {
            mQuestion.increment(Common.OBJECT_POST_QB_NUM);
        }
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);

        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);
        holder.btnA.setVoted(true, true, option.equals("A"));
        holder.btnB.setVoted(true, true, option.equals("B"));

        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.layoutFuncButtons.setVisibility(View.VISIBLE);
        holder.layoutSkipBtn.setVisibility(View.GONE);

        ParseRelation<ParseUser> relation = mQuestion.getRelation(Common.OBJECT_POST_VOTED_USER);
        relation.add(ParseUser.getCurrentUser());
        mQuestion.saveInBackground();

        final ParseObject votedQuestion = new ParseObject(Common.OBJECT_VOTED_QUESTION);
        votedQuestion.put(Common.OBJECT_VOTED_QUESTION_QID, mQuestion.getObjectId());
        votedQuestion.put(Common.OBJECT_VOTED_QUESTION_OPTION, option);
        votedQuestion.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> votedRelation = user.getRelation(Common.OBJECT_USER_VOTED_QUESTIONS);
                    votedRelation.add(votedQuestion);
                    user.saveInBackground();
                }
                else {
                    e.printStackTrace();
                }
            }
        });


        mVotedQuestionList.add(votedQuestion);

        cache.put(Common.QUESTION_CARD_IS_VOTED, true);
        cache.put(Common.QUESTION_CARD_QA_NUM, voteA);
        cache.put(Common.QUESTION_CARD_QB_NUM, voteB);
    }

    private void resetCard(final ViewHolder holder) {
        //String option = getString(Common.OBJECT_VOTED_QUESTION_OPTION);
        //Boolean voteA = option.equals("A");
        holder.imgViewQuestionPicture.setVisibility(View.GONE);
        holder.btnA.setVoted(false, false, false);
        holder.btnB.setVoted(false, false, false);
        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void setCardVoted(final ViewHolder holder, String option) {
        holder.layoutFuncButtons.setVisibility(View.VISIBLE);
        holder.layoutSkipBtn.setVisibility(View.GONE);
        holder.btnA.setVoted(true, false, option.equals("A"));
        holder.btnB.setVoted(true, false, option.equals("B"));
        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
    /*
    private void skipCardVote(final ViewHolder holder, String option) {
        holder.layoutFuncButtons.setVisibility(View.VISIBLE);
        holder.btnShowResult.setVisibility(View.GONE);
        holder.btnA.setVoted(true, true, option.equals("A"));
        holder.btnB.setVoted(true, true, option.equals("B"));
        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }*/

    private void setCardNotVoted(final ViewHolder holder, final ParseObject mQuestion, final int voteA, final int voteB,
                                 final Map<String, Object> cache) {
        holder.layoutFuncButtons.setVisibility(View.GONE);
        holder.layoutSkipBtn.setVisibility(View.VISIBLE);
        holder.btnA.setVoted(false, false, false);
        holder.btnB.setVoted(false, false, false);

        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteQuestionForA(mQuestion, holder, voteA, voteB, cache);
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteQuestionForB(mQuestion, holder, voteA, voteB, cache);
            }
        });
        holder.btnShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipVoteQuestion(mQuestion, holder, voteA, voteB, cache);
                //skipCardVote(holder, "N");
            }
        });
        cache.put(Common.QUESTION_CARD_IS_VOTED, false);
    }

    private boolean isQuestionVoted(ParseObject mQuestion) {
        final String qid = mQuestion.getObjectId();

        for(ParseObject votedQuestion : mVotedQuestionList) {
            if(votedQuestion.getString(Common.OBJECT_VOTED_QUESTION_QID).equals(qid)) {
                return true;
            }
        }
        return false;
    }

    private String questionVotedOption(ParseObject mQuestion) {
        final String qid = mQuestion.getObjectId();

        for(ParseObject votedQuestion : mVotedQuestionList) {
            if(votedQuestion.getString(Common.OBJECT_VOTED_QUESTION_QID).equals(qid)) {
                return votedQuestion.getString("option");
            }
        }
        return "";
    }


    private void setupCardVotedAction(final ViewHolder holder, final ParseObject mQuestion, final ParseRelation<ParseUser> relation,
                                      final Map<String, Object> cache) {
        final int voteA = mQuestion.getInt(Common.OBJECT_POST_QA_NUM);
        final int voteB = mQuestion.getInt(Common.OBJECT_POST_QB_NUM);

        if (isQuestionVoted(mQuestion)) {
            setCardVoted(holder, questionVotedOption(mQuestion));
            cache.put(Common.QUESTION_CARD_IS_VOTED, true);
        } else {
            setCardNotVoted(holder, mQuestion, voteA, voteB, cache);
            cache.put(Common.QUESTION_CARD_IS_VOTED, false);
        }
    }

    private void displayUserParseImage(final ViewHolder holder, final String userName, final ParseUser user,
                                       final ImageView imgView, final Map<String, Object> cache) {
        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        imgView.setImageBitmap(bmp);
                        cache.put(Common.QUESTION_CARD_PROFILE_IMG, bmp);
                        setupProfileImgListener(holder, user, userName, bmp, user.getObjectId());
                    }
                }
            }
        });
    }

    private void setQuestionPictureOnClickListener(final ViewHolder holder) {
        holder.imgViewQuestionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                Bitmap bmp = ((BitmapDrawable) holder.imgViewQuestionPicture.getDrawable())
                        .getBitmap();

                ImageView picture = (ImageView) dialog.findViewById(R.id.img_view_dialog_picture);
                ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.img_btn_close);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                picture.setImageBitmap(bmp);
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor
                        ("#80000000")));
            }
        });
    }

    private void setQuestionPictureHeight(ViewHolder holder) {
        int width = holder.imgViewQuestionPicture.getWidth();
        int height = (int) (width * (9.0 / 16));
        holder.imgViewQuestionPicture.setMinimumHeight(height);
    }

    private void displayQuestionPictureParseImage(final ParseObject mQuestion, final ViewHolder holder, final Map<String, Object> cache) {
        ParseFile questionPicture = mQuestion.getParseFile(Common.OBJECT_POST_PICTURE);
        if(questionPicture != null) {
            questionPicture.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        if (bmp != null) {
                            holder.imgViewQuestionPicture.setImageBitmap(bmp);
                            holder.imgViewQuestionPicture.setVisibility(View.VISIBLE);
                            setQuestionPictureHeight(holder);
                            cache.put(Common.QUESTION_CARD_QUESTION_PICTURE, bmp);
                        }
                    }
                }
            });
        }
    }

    private void setupLikeButton(final ParseObject mQuestion, final ViewHolder holder, final Map<String, Object> cache) {
        //render BtnLike
        holder.liked = mFavoriteList.contains(mQuestion);
        if (holder.liked){
            holder.imgBtnLike.setImgLike();
        }else{
            holder.imgBtnLike.setImgDisLike();
        }

        holder.imgBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                ;
                if (currentUser != null) {
                    // do stuff with the user
                    currentUser.getRelation("likes");
                    ParseRelation<ParseObject> relation = currentUser.getRelation(Common
                            .OBJECT_USER_LIKES);
                    int currentNum = mQuestion.getInt(Common.OBJECT_POST_QLIKES);
                    if (holder.liked == false) {
                        Log.d("On click", "get like");
                        relation.add(mQuestion);
                        currentUser.saveInBackground();
                        mFavoriteList.add(mQuestion);
                        holder.imgBtnLike.setImgLike();
                        holder.liked = true;
                        holder.imgBtnLike.addBadgeCount();
                        mQuestion.put(Common.OBJECT_POST_QLIKES, currentNum + 1);
                        cache.put(Common.QUESTION_CARD_LIKE_NUM, currentNum+1);
                        if (mAmin == true) {
                            startLikeButtonAnimation2(v, holder.imgBtnLike.getImgHeight());
                        }

                        ParseUtils.userFollowingQuestion(ParseUser.getCurrentUser(), mQuestion);

                    } else {
                        Log.d("On click", "get dislike");
                        relation.remove(mQuestion);
                        currentUser.saveInBackground();
                        mFavoriteList.remove(mQuestion);
                        holder.imgBtnLike.setImgDisLike();
                        holder.liked = false;
                        holder.imgBtnLike.minusBadgeCount();
                        mQuestion.put(Common.OBJECT_POST_QLIKES, currentNum - 1);
                        cache.put(Common.QUESTION_CARD_LIKE_NUM, currentNum - 1);

                        ParseUtils.userUnFollowingQuestion(ParseUser.getCurrentUser(), mQuestion);
                    }
                    mQuestion.saveInBackground();

                } else {
                    // show the signup or login screen
                }
            }
        });
    }

    public void setupShareButton(final ParseObject mQuestion , final ViewHolder holder, final String objectId, final int shareNum, final Map<String, Object> cache){
        holder.shareBtnPost.setBadgeCount(shareNum);
        holder.shareBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("呱呱 - 投票結果")
                            .setContentDescription(holder.txtTitle.getText().toString())
                            .setContentUrl(Uri.parse("https://aqueous-falls-3271.herokuapp" +
                                    ".com/guagua/" + objectId + "/results/"))
                            .build();

                    shareDialog.show(linkContent);
                    currentholder = holder;
                    currentQuestion = mQuestion;
                }

            }
        });
    }

    private void setupProfileImgListener(final ViewHolder holder, final ParseUser user,
                                         final String userName, final Bitmap profileImg, final String userId) {
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoOtherUserProfileActivity(mContext, userId, userName, profileImg);
            }
        });
    }

    private void setupCommentButton(final ViewHolder holder, final String postId, final int commentCount) {

        holder.imgBtnComment.setBadgeCount(commentCount);
        holder.imgBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoCommentActivity(mContext, postId);
            }
        });
    }

    private void loadFromParse(final ParseObject mQuestion, final ViewHolder holder, final Map<String, Object> cache) {

        resetCard(holder);

        final String objectId = mQuestion.getObjectId();
        final ParseRelation<ParseUser> relation = mQuestion.getRelation(Common
                .OBJECT_POST_VOTED_USER);
        final ParseUser postUser = mQuestion.getParseUser(Common.OBJECT_POST_USER);
        final String questionContent = mQuestion.getString(Common.OBJECT_POST_CONTENT);
        final String optionA = mQuestion.getString(Common.OBJECT_POST_QA);
        final String optionB = mQuestion.getString(Common.OBJECT_POST_QB);
        final int voteA = mQuestion.getInt(Common.OBJECT_POST_QA_NUM);
        final int voteB = mQuestion.getInt(Common.OBJECT_POST_QB_NUM);
        final String voteOption = questionVotedOption(mQuestion);
        final Date mDate = mQuestion.getCreatedAt();
        final int likeNum = mQuestion.getInt(Common.OBJECT_POST_QLIKES);
        final int shareNum = mQuestion.getInt(Common.OBJECT_POST_SHARE_NUM);

        if(postUser != null) {
            postUser.fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null) {
                        String nickName = user.getString(Common.OBJECT_USER_NICK);
                        holder.txtName.setText(nickName);

                        displayUserParseImage(holder, nickName, user, holder.imgProfile, cache);

                        cache.put(Common.QUESTION_CARD_PARSE_USER, user);
                        cache.put(Common.QUESTION_CARD_NICK, nickName);
                        cache.put(Common.QUESTION_CARD_USER_ID, user.getObjectId());

                    }
                }
            });
        }
        else {

            final Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_account_circle_black_48dp);

            holder.txtName.setText("Fan Fan");
            holder.imgProfile.setImageBitmap(bmp);
            cache.put(Common.QUESTION_CARD_NICK, "Fan Fan");
            cache.put(Common.QUESTION_CARD_PROFILE_IMG, bmp);
        }
        displayQuestionPictureParseImage(mQuestion, holder, cache);
        holder.txtTitle.setText(questionContent);
        holder.txtDate.setText(date2String(mDate));
        holder.btnA.setVoteText(optionA);
        holder.btnB.setVoteText(optionB);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);

        cache.put(Common.QUESTION_CARD_CONTENT, questionContent);
        cache.put(Common.QUESTION_CARD_QA, optionA);
        cache.put(Common.QUESTION_CARD_QB, optionB);
        cache.put(Common.QUESTION_CARD_QA_NUM, voteA);
        cache.put(Common.QUESTION_CARD_QB_NUM, voteB);
        cache.put(Common.QUESTION_CARD_VOTE_OPTION, voteOption);
        cache.put(Common.QUESTION_CARD_DATE, mDate);
        cache.put(Common.QUESTION_CARD_LIKE_NUM, likeNum);
        cache.put(Common.QUESTION_CARD_SHARE_NUM, shareNum);


        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int) (voteB * 100.0 / (voteA + voteB));
        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);
        holder.imgBtnLike.setBadgeCount(likeNum);

        setupLikeButton(mQuestion, holder, cache);
        setupShareButton(mQuestion, holder, objectId, shareNum, cache);

        ParseRelation<ParseObject> commentsRelation = mQuestion.getRelation(Common.OBJECT_POST_COMMENTS);
        commentsRelation.getQuery().countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                setupCommentButton(holder, mQuestion.getObjectId(), count);
                cache.put(Common.QUESTION_CARD_COMMENTS_NUM, count);
            }
        });


        setupCardVotedAction(holder, mQuestion, relation, cache);
    }

    private void loadFromCache(Map<String, Object> cache, final ViewHolder holder, final ParseObject mQuestion) {
        final ParseUser user = (ParseUser) cache.get(Common.QUESTION_CARD_PARSE_USER);
        final String userId = (String) cache.get(Common.QUESTION_CARD_USER_ID);
        final String nickName = (String) cache.get(Common.QUESTION_CARD_NICK);
        final Bitmap profileImg = (Bitmap) cache.get(Common.QUESTION_CARD_PROFILE_IMG);
        final String questionContent = (String) cache.get(Common.QUESTION_CARD_CONTENT);
        final Bitmap questionPicture = (Bitmap) cache.get(Common.QUESTION_CARD_QUESTION_PICTURE);
        final String optionA = (String) cache.get(Common.QUESTION_CARD_QA);
        final String optionB = (String) cache.get(Common.QUESTION_CARD_QB);
        final int voteA = (int) cache.get(Common.QUESTION_CARD_QA_NUM);
        final int voteB = (int) cache.get(Common.QUESTION_CARD_QB_NUM);
        final Boolean isVoted = (Boolean) cache.get(Common.QUESTION_CARD_IS_VOTED);
        final String voteOption = (String) cache.get(Common.QUESTION_CARD_VOTE_OPTION);
        final int commentNum = (cache.containsKey(Common.QUESTION_CARD_COMMENTS_NUM))?(int) cache.get(Common.QUESTION_CARD_COMMENTS_NUM):0;
        final Date mDate = (Date) cache.get(Common.QUESTION_CARD_DATE);
        final int likeNum = (int) cache.get(Common.QUESTION_CARD_LIKE_NUM);
        final int shareNum = (int) cache.get(Common.QUESTION_CARD_SHARE_NUM);

        holder.txtName.setText(nickName);
        if(profileImg != null) {
            holder.imgProfile.setImageBitmap(profileImg);
            setupProfileImgListener(holder, user, nickName, profileImg, userId);
        }
        else {
            holder.imgProfile.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_account_circle_black_48dp));
        }
        if(questionPicture != null) {
            holder.imgViewQuestionPicture.setImageBitmap(questionPicture);
            holder.imgViewQuestionPicture.setVisibility(View.VISIBLE);
            setQuestionPictureHeight(holder);
        }
        holder.txtTitle.setText(questionContent);
        holder.txtDate.setText(date2String(mDate));
        holder.btnA.setVoteText(optionA);
        holder.btnB.setVoteText(optionB);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);
        holder.imgBtnLike.setBadgeCount(likeNum);

        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int) (voteB * 100.0 / (voteA + voteB));
        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);

        if(isVoted) {
            setCardVoted(holder, voteOption);
        }
        else {
            setCardNotVoted(holder, mQuestion, voteA, voteB, cache);
        }

        setupLikeButton(mQuestion, holder, cache);
        setupCommentButton(holder, mQuestion.getObjectId(), commentNum);
        setupShareButton(mQuestion, holder, mQuestion.getObjectId(), shareNum, cache);
    }

    public void startLikeButtonAnimation(View v, int Height){
        final ImageView img = new ImageView(mContext);
        img.setImageResource(R.drawable.ic_like);
        final int[] xy = new int[2];
        final int[] fxy = new int[2];
        v.getLocationInWindow(xy);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Height,Height);

        img.setLayoutParams(params);
        final ViewGroup rl = (ViewGroup) v.getRootView();
        View ap = rl.findViewById(R.id.action_search);
        ap.getLocationInWindow(fxy);
        int w = ap.getHeight();
        rl.addView(img);
        Log.d("Location", String.valueOf(xy[0]) + ' ' + String.valueOf(xy[1]));
        fxy[0] = fxy[0]+w/2;
        fxy[1] = fxy[1]+w/2;
        img.setX(xy[0]);
        img.setY(xy[1]);
        ValueAnimator am = ValueAnimator.ofFloat(0,(float)0.9);
        am.setDuration(1000);
        am.setRepeatCount(0);
        am.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rl.removeView(img);
            }
        });
        am.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue()))
                        .floatValue();
                //img.setMaxWidth((int) (18 * value));
                //img.getLayoutParams().width = (int)(200*value);
                //img.getLayoutParams().height = 200;
                //img.requestLayout();
                img.setTranslationX((float) (xy[0] + (fxy[0] - xy[0]) * value));
                img.setTranslationY((float) (xy[1] + (fxy[1] - xy[1]) * Math.sin((value) * Math
                        .PI / 2)));
                if (value < 0.8) {
                    img.setAlpha((int) (255 * (1 - value)));
                }
                // Set translation of your view here. Position can be calculated
                // out of value. This code should move the view in a half circle.

                /*if(value>0.5){
                    img.setImageAlpha((int)(80*(1-value)));
                }*/
            }
        });
        //img.setAnimation(am);
        //am.startNow();
        am.start();
    }

    public void startLikeButtonAnimation2(final View v, int Height){
        final ImageView img = new ImageView(mContext);
        img.setImageResource(R.drawable.ic_like);
        final int[] xy = new int[2];
        final int[] fxy = new int[2];
        v.getLocationInWindow(xy);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Height,Height);

        img.setLayoutParams(params);
        final ViewGroup rl = (ViewGroup) v.getRootView();
        View ap = rl.findViewById(R.id.action_search);
        rl.addView(img);
        fxy[0] = xy[0];
        fxy[1] = xy[1] - 300;
        img.setX(xy[0]);
        img.setY(xy[1]);
        ValueAnimator am = ValueAnimator.ofFloat(0,1);
        am.setDuration(1000);
        am.setRepeatCount(0);
        am.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rl.removeView(img);
            }
        });
        am.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue()))
                        .floatValue();
                double t = 2*Math.PI*value;
                v.getLocationOnScreen(xy);

                //img.setMaxWidth((int) (18 * value));
                //img.getLayoutParams().width = (int)(200*value);
                //img.getLayoutParams().height = 200;
                //img.requestLayout();
                img.setTranslationX((float) (xy[0] + 20*Math.sin(t)));
                img.setTranslationY((float) (xy[1] - 200*value));
                img.setAlpha((int) (255 * (1 - value)));
                // Set translation of your view here. Position can be calculated
                // out of value. This code should move the view in a half circle.

                /*if(value>0.5){
                    img.setImageAlpha((int)(80*(1-value)));
                }*/
            }
        });
        //img.setAnimation(am);
        //am.startNow();
        am.start();

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ParseObject mQuestion = mVisibleQuestionList.get(position);
        final String objectId = mQuestion.getObjectId();

        resetCard(holder);

        final boolean choiceQuestion = mQuestion.getBoolean(Common.OBJECT_POST_CHOICE_QUESTION);
        if(choiceQuestion) {
            holder.btnA.setVisibility(View.VISIBLE);
            holder.btnB.setVisibility(View.VISIBLE);
        }
        else {
            holder.btnA.setVisibility(View.GONE);
            holder.btnB.setVisibility(View.GONE);
        }

        if(cachedQuestion.get(objectId) == null) {
            Map<String, Object> cache = new HashMap<>();
            cachedQuestion.put(objectId, cache);
            loadFromParse(mQuestion, holder, cache);
        }
        else {
            loadFromCache(cachedQuestion.get(objectId), holder, mQuestion);
        }

        if(!choiceQuestion){
            holder.layoutSkipBtn.setVisibility(View.GONE);
            holder.layoutFuncButtons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return mVisibleQuestionList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mVisibleQuestionList.size();
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_BOTH;
    }

    @Override
    public void onSetSwipeBackground(ViewHolder holder, int position, int type) {
    }

    @Override
    public int onSwipeItem(ViewHolder holder, int position, int result) {
        return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    }

    @Override
    public void onPerformAfterSwipeReaction(ViewHolder holder, int position, int result, int reaction) {
    }

    public void setLikeAnimation(boolean b){
        mAmin = b;
    }

    public String date2String(Date d){
        Date now = new Date();
        String str_date;
        int day = (int) TimeUnit.DAYS.convert(now.getTime()-d.getTime(),TimeUnit.MILLISECONDS);
        if (day > 1){
            str_date = DateUtils.formatDateTime(mContext,d.getTime(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE );
        }else if(d.getYear()!= now.getYear()){
            str_date = DateUtils.formatDateTime(mContext,d.getTime(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        }else {
            str_date = DateUtils.getRelativeTimeSpanString(d.getTime()).toString();
        }
        return str_date;
    }



    public static class ViewHolder extends AbstractSwipeableItemViewHolder {
        public View mView;
        public ImageView imgProfile;
        public TextView txtName;
        public TextView txtTitle;
        public ImageView imgViewQuestionPicture;
        public TextView txtDate;
        public OptionButton btnA;
        public OptionButton btnB;
        public ShareButton shareBtnPost;
        public CommentButton2 imgBtnComment;
        public LikeButton imgBtnLike;
        public LinearLayout layoutFuncButtons;
        public LinearLayout layoutSkipBtn;
        public TextView btnShowResult;
        public Boolean liked = false;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            imgProfile = (ImageView) v.findViewById(R.id.imgProfile);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtTitle = (TextView) v.findViewById(R.id.title);
            imgViewQuestionPicture = (ImageView) v.findViewById(R.id.img_view_question_picture);
            txtDate = (TextView) v.findViewById(R.id.date);
            btnA = (OptionButton) v.findViewById(R.id.btnA);
            btnB = (OptionButton) v.findViewById(R.id.btnB);
            shareBtnPost = (ShareButton) v.findViewById(R.id.shareBtnPost);
            imgBtnComment = (CommentButton2) v.findViewById(R.id.imgBtnComment);
            imgBtnLike = (LikeButton) v.findViewById(R.id.imgBtnLike);
            layoutFuncButtons = (LinearLayout) v.findViewById(R.id.layout_function_buttons);
            layoutSkipBtn = (LinearLayout) v.findViewById(R.id.ghostbar);
            btnShowResult = (TextView) v.findViewById(R.id.btnShowResult);
        }
        @Override
        public View getSwipeableContainerView() {
            return mView;
        }

    }

}
