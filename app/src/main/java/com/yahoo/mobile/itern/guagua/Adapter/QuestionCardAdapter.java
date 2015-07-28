package com.yahoo.mobile.itern.guagua.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.OptionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmwang on 7/16/15.
 */
public class QuestionCardAdapter extends RecyclerView.Adapter<QuestionCardAdapter.ViewHolder>
        implements SwipeableItemAdapter<QuestionCardAdapter.ViewHolder> {


    private List<ParseObject> mAllQuestionList, mVisibleQuestionList, mFavoriteList;
    private Context mContext;
    private LayoutInflater mInflater;

    private Map<String, Map<String, Object>> cachedQuestion;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public void notifyDataSetChangedWithCache() {
        cachedQuestion = new HashMap<>();
        notifyDataSetChanged();
    }

    public QuestionCardAdapter(Context context, List<ParseObject> list) {
        super();

        cachedQuestion = new HashMap<>();

        mContext = context;
        mAllQuestionList = list;
        mVisibleQuestionList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mVisibleQuestionList.addAll(mAllQuestionList);
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);


        try {
            mFavoriteList = ParseUser.getCurrentUser().getRelation(Common.OBJECT_POST_LIKES).getQuery().find();
            Log.d("Get Likes", String.valueOf(mFavoriteList.size()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity)mContext);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            public void onSuccess(Sharer.Result results){}
            public void onCancel(){}
            public void onError(FacebookException e){}
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



    public static class ViewHolder extends AbstractSwipeableItemViewHolder {
        public View mView;
        public ImageView imgProfile;
        public TextView txtName;
        public TextView txtTitle;
        public OptionButton btnA;
        public OptionButton btnB;
        public ImageButton shareBtnPost;
        public ImageButton imgBtnComment;
        public ImageButton imgBtnLike;
        public LinearLayout layoutFuncButtons;
        public Boolean liked = false;

        public ViewHolder(View v) {
            super(v);
            Log.d("QDA", "Create viewhoder");
            mView = v;
            imgProfile = (ImageView) v.findViewById(R.id.imgProfile);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtTitle = (TextView) v.findViewById(R.id.title);
            btnA = (OptionButton) v.findViewById(R.id.btnA);
            btnB = (OptionButton) v.findViewById(R.id.btnB);
            shareBtnPost = (ImageButton)v.findViewById(R.id.shareBtnPost);
            imgBtnComment = (ImageButton) v.findViewById(R.id.imgBtnComment);
            imgBtnLike = (ImageButton) v.findViewById(R.id.imgBtnLike);

            layoutFuncButtons = (LinearLayout) v.findViewById(R.id.layout_function_buttons);

        }
        @Override
        public View getSwipeableContainerView() {
            return mView;
        }

    }

    @Override
    public QuestionCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_question, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void voteQuestion(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB, Map<String, Object> cache) {
        final String objectId = mQuestion.getObjectId();

        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int)(voteB * 100.0 / (voteA + voteB));
        mQuestion.put("A", voteA);
        mQuestion.put("B", voteB);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);

        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);
        holder.btnA.setVoted(true, true);
        holder.btnB.setVoted(true, true);

        holder.layoutFuncButtons.setVisibility(View.VISIBLE);

        ParseRelation<ParseUser> relation = mQuestion.getRelation(Common.OBJECT_POST_VOTED_USER);
        relation.add(ParseUser.getCurrentUser());

        mQuestion.saveInBackground();

        cache.put(Common.QUESTION_CARD_IS_VOTED, true);
        cache.put(Common.QUESTION_CARD_QA_NUM, voteA);
        cache.put(Common.QUESTION_CARD_QB_NUM, voteB);
    }

    private void resetCard(final ViewHolder holder) {
        holder.btnA.setVoted(false, false);
        holder.btnB.setVoted(false, false);
        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
    }

    private void setCardVoted(final ViewHolder holder) {
        holder.layoutFuncButtons.setVisibility(View.VISIBLE);
        holder.btnA.setVoted(true, false);
        holder.btnB.setVoted(true, false);
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
    private void setCardNotVoted(final ViewHolder holder, final ParseObject mQuestion, final int voteA, final int voteB,
                                 final Map<String, Object> cache) {
        holder.layoutFuncButtons.setVisibility(View.INVISIBLE);
        holder.btnA.setVoted(false, false);
        holder.btnB.setVoted(false, false);

        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteQuestion(mQuestion, holder, voteA + 1, voteB, cache);
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteQuestion(mQuestion, holder, voteA, voteB + 1, cache);
            }
        });
        cache.put(Common.QUESTION_CARD_IS_VOTED, false);
    }

    private void setupCardVotedAction(final ViewHolder holder, final ParseObject mQuestion, final ParseRelation<ParseUser> relation,
                                      final Map<String, Object> cache) {
        relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> votedUser, ParseException e) {

                final int voteA = mQuestion.getInt(Common.OBJECT_POST_QA_NUM);
                final int voteB = mQuestion.getInt(Common.OBJECT_POST_QB_NUM);

                if (votedUser.contains(ParseUser.getCurrentUser())) {
                    setCardVoted(holder);
                    cache.put(Common.QUESTION_CARD_IS_VOTED, true);
                } else {
                    setCardNotVoted(holder, mQuestion, voteA, voteB, cache);
                    cache.put(Common.QUESTION_CARD_IS_VOTED, false);
                }
            }
        });
    }

    private void displayImage(ParseFile img, final ImageView imgView, final Map<String, Object> cache) {
        img.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        imgView.setImageBitmap(bmp);
                        cache.put(Common.QUESTION_CARD_PROFILE_IMG, bmp);
                    }
                }
            }
        });
    }

    private void setupLikeButton(final ParseObject mQuestion, final ViewHolder holder) {
        //render BtnLike
        holder.liked = mFavoriteList.contains(mQuestion);
        if (holder.liked){
            holder.imgBtnLike.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            holder.imgBtnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        holder.imgBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    currentUser.getRelation("likes");
                    ParseRelation<ParseObject> relation = currentUser.getRelation(Common.OBJECT_POST_LIKES);
                    if (holder.liked == false){
                        Log.d("On click","get like");
                        relation.add(mQuestion);
                        currentUser.saveInBackground();
                        mFavoriteList.add(mQuestion);
                        holder.imgBtnLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                        holder.liked = true;
                    }else{
                        Log.d("On click","get dislike");
                        relation.remove(mQuestion);
                        currentUser.saveInBackground();
                        mFavoriteList.remove(mQuestion);
                        holder.imgBtnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.liked = false;
                    }


                } else {
                    // show the signup or login screen
                }
            }
        });
    }

    private void loadFromParse(final ParseObject mQuestion, final ViewHolder holder, final Map<String, Object> cache) {

        resetCard(holder);

        final String objectId = mQuestion.getObjectId();
        final ParseRelation<ParseUser> relation = mQuestion.getRelation(Common.OBJECT_POST_VOTED_USER);
        final ParseUser postUser = mQuestion.getParseUser(Common.OBJECT_POST_USER);
        final String questionContent = mQuestion.getString(Common.OBJECT_POST_CONTENT);
        final String optionA = mQuestion.getString(Common.OBJECT_POST_QA);
        final String optionB = mQuestion.getString(Common.OBJECT_POST_QB);
        final int voteA = mQuestion.getInt(Common.OBJECT_POST_QA_NUM);
        final int voteB = mQuestion.getInt(Common.OBJECT_POST_QB_NUM);

        if(postUser != null) {
            postUser.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if(e == null) {
                        holder.txtName.setText(postUser.getString(Common.OBJECT_USER_NICK));
                        ParseFile imgFile = postUser.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                        displayImage(imgFile, holder.imgProfile, cache);

                        cache.put(Common.QUESTION_CARD_NICK, postUser.getString(Common.OBJECT_USER_NICK));

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
        holder.txtTitle.setText(questionContent);
        holder.btnA.setVoteText(optionA);
        holder.btnB.setVoteText(optionB);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);

        cache.put(Common.QUESTION_CARD_CONTENT, questionContent);
        cache.put(Common.QUESTION_CARD_QA, optionA);
        cache.put(Common.QUESTION_CARD_QB, optionB);
        cache.put(Common.QUESTION_CARD_QA_NUM, voteA);
        cache.put(Common.QUESTION_CARD_QB_NUM, voteB);

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
                }
            }
        });


        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int) (voteB * 100.0 / (voteA + voteB));
        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);


        holder.imgBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoCommentActivity(mContext, objectId);
            }
        });

        setupLikeButton(mQuestion, holder);
        setupCardVotedAction(holder, mQuestion, relation, cache);
    }

    private void loadFromCache(Map<String, Object> cache, final ViewHolder holder, final ParseObject mQuestion) {
        final String nickName = (String) cache.get(Common.QUESTION_CARD_NICK);
        final Bitmap profileImg = (Bitmap) cache.get(Common.QUESTION_CARD_PROFILE_IMG);
        final String questionContent = (String) cache.get(Common.QUESTION_CARD_CONTENT);
        final String optionA = (String) cache.get(Common.QUESTION_CARD_QA);
        final String optionB = (String) cache.get(Common.QUESTION_CARD_QB);
        final int voteA = (int) cache.get(Common.QUESTION_CARD_QA_NUM);
        final int voteB = (int) cache.get(Common.QUESTION_CARD_QB_NUM);
        final Boolean isVoted = (Boolean) cache.get(Common.QUESTION_CARD_IS_VOTED);

        holder.txtName.setText(nickName);
        if(profileImg != null) {
            holder.imgProfile.setImageBitmap(profileImg);
        }
        else {
            holder.imgProfile.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_account_circle_black_48dp));
        }
        holder.txtTitle.setText(questionContent);
        holder.btnA.setVoteText(optionA);
        holder.btnB.setVoteText(optionB);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);

        int progressA = (int)(voteA * 100.0 / (voteA + voteB));
        int progressB = (int) (voteB * 100.0 / (voteA + voteB));
        holder.btnA.setProgress(progressA);
        holder.btnB.setProgress(progressB);

        if(isVoted) {
            setCardVoted(holder);
        }
        else {
            setCardNotVoted(holder, mQuestion, voteA, voteB, cache);
        }

        setupLikeButton(mQuestion, holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ParseObject mQuestion = mVisibleQuestionList.get(position);
        final String objectId = mQuestion.getObjectId();

        if(cachedQuestion.get(objectId) == null) {
            Map<String, Object> cache = new HashMap<>();
            cachedQuestion.put(objectId, cache);
            loadFromParse(mQuestion, holder, cache);
        }
        else {
            loadFromCache(cachedQuestion.get(objectId), holder, mQuestion);
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
}
