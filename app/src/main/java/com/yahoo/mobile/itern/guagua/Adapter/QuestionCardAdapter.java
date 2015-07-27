package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Fragment.CommentFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
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


    private List<ParseObject> mAllQuestionList, mVisibleQuestionList;
    private Map<String, Boolean> voted;
    private Context mContext;
    private LayoutInflater mInflater;

    public QuestionCardAdapter(Context context, List<ParseObject> list) {
        super();
        mContext = context;
        mAllQuestionList = list;
        mVisibleQuestionList = new ArrayList<>();
        mVisibleQuestionList.addAll(mAllQuestionList);
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);

        voted = new HashMap<>();
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
        public ImageButton imgBtnComment;
        public ImageButton imgBtnLike;
        public ImageButton imgBtnShare;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            imgProfile = (ImageView) v.findViewById(R.id.imgProfile);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtTitle = (TextView) v.findViewById(R.id.title);
            btnA = (OptionButton) v.findViewById(R.id.btnA);
            btnB = (OptionButton) v.findViewById(R.id.btnB);
            imgBtnComment = (ImageButton) v.findViewById(R.id.imgBtnComment);
            imgBtnShare = (ImageButton) v.findViewById(R.id.imgBtnShare);
            imgBtnLike = (ImageButton) v.findViewById(R.id.imgBtnLike);
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

    private void voteQuestion(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB) {
        final String objectId = mQuestion.getObjectId();
        if(voted.get(objectId)) {
            return;
        }
        mQuestion.put("A", voteA);
        mQuestion.put("B", voteA);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);
        holder.btnA.setVoted(true);
        holder.btnB.setVoted(true);
        holder.imgBtnComment.setVisibility(View.VISIBLE);
        holder.imgBtnLike.setVisibility(View.VISIBLE);
        holder.imgBtnShare.setVisibility(View.VISIBLE);
        voted.put(objectId, true);
        mQuestion.saveInBackground();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ParseObject mQuestion = mVisibleQuestionList.get(position);
        final ParseUser postUser = mQuestion.getParseUser(Common.OBJECT_POST_USER);
        final String objectId = mQuestion.getObjectId();
        final int voteA = mQuestion.getInt(Common.OBJECT_POST_QA_NUM);
        final int voteB = mQuestion.getInt(Common.OBJECT_POST_QB_NUM);
        if(postUser != null) {
            postUser.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if(e == null) {
                        holder.txtName.setText(postUser.getString(Common.OBJECT_USER_NICK));
                        ParseFile imgFile = postUser.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                        ParseUtils.displayImage(imgFile, holder.imgProfile);
                    }
                }
            });
        }
        else {
            holder.txtName.setText("Fan Fan");
            holder.imgProfile.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_account_circle_black_48dp));
        }
        holder.txtTitle.setText(mQuestion.getString(Common.OBJECT_POST_CONTENT));
        holder.btnA.setVoteText(mQuestion.getString(Common.OBJECT_POST_QA));
        holder.btnB.setVoteText(mQuestion.getString(Common.OBJECT_POST_QB));
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);
        if(voted.get(objectId) == null) {
            voted.put(objectId, false);
        }
        if(voted.get(objectId)) {
            holder.imgBtnComment.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgBtnComment.setVisibility(View.GONE);
        }
        holder.btnA.setVoted(voted.get(objectId));
        holder.btnB.setVoted(voted.get(objectId));

        holder.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteQuestion(mQuestion, holder, voteA + 1, voteB);
            }
        });
        holder.btnB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                voteQuestion(mQuestion, holder, voteA, voteB + 1);
            }
        });
        holder.imgBtnComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("main")
                        .replace(R.id.content_frame, CommentFragment.newInstance(objectId))
                        .commit();
            }
        });
        holder.imgBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    currentUser.getRelation("likes");
                    ParseRelation<ParseObject> relation = currentUser.getRelation(Common.OBJECT_POST_LIKES);
                    relation.add(mQuestion);
                    currentUser.saveInBackground();
                    holder.imgBtnLike.setBackground(mContext.getDrawable(R.drawable.ic_favorite_black_24dp));
                } else {
                    // show the signup or login screen
                }
            }
        });
        //holder.imgBtn
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
