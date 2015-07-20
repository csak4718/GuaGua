package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.View.OptionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmwang on 7/16/15.
 */
public class QuestionCardAdapter extends RecyclerView.Adapter<QuestionCardAdapter.ViewHolder>
        implements SwipeableItemAdapter<QuestionCardAdapter.ViewHolder> {


    private List<ParseObject> mQuestionList;
    private Map<String, Boolean> voted;
    private Context mContext;
    private LayoutInflater mInflater;

    public QuestionCardAdapter(Context context, List<ParseObject> list) {
        super();
        mContext = context;
        mQuestionList = list;
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);

        voted = new HashMap<>();
    }

    public static class ViewHolder extends AbstractSwipeableItemViewHolder {
        public View mView;
        public TextView txtTitle;
        public OptionButton btnA;
        public OptionButton btnB;
        public ImageButton imgBtnComment;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            txtTitle = (TextView) v.findViewById(R.id.title);
            btnA = (OptionButton) v.findViewById(R.id.btnA);
            btnB = (OptionButton) v.findViewById(R.id.btnB);
            imgBtnComment = (ImageButton) v.findViewById(R.id.imgBtnComment);
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
                .inflate(R.layout.question_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void voteQuestion(ParseObject mQuestion, ViewHolder holder, int voteA, int voteB) {
        final String objectId = mQuestion.getObjectId();
        if(voted.get(objectId)) {
            return;
        }
        mQuestion.put("A", voteA);
        holder.btnA.setVoteNum(voteA);
        holder.btnB.setVoteNum(voteB);
        holder.btnA.setVoted(true);
        holder.btnB.setVoted(true);
        holder.imgBtnComment.setVisibility(View.VISIBLE);
        voted.put(objectId, true);
        mQuestion.saveInBackground();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ParseObject mQuestion = mQuestionList.get(position);
        final String objectId = mQuestion.getObjectId();
        final int voteA = mQuestion.getInt("A");
        final int voteB = mQuestion.getInt("B");
        holder.txtTitle.setText(mQuestion.getString("prayer"));
        holder.btnA.setVoteText(mQuestion.getString("QA"));
        holder.btnB.setVoteText(mQuestion.getString("QB"));
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
    }

    @Override
    public long getItemId(int position) {
        return mQuestionList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
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
