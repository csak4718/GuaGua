package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/19/15.
 */
public class OptionButton extends RelativeLayout {
    View mView;
    FrameLayout mRoot;
    ProgressBar progressBarVote;
    TextView txtVoteNum;
    TextView txtVoteText;
    Boolean isVoted;
    int mProgress;

    public OptionButton(Context context) {
        super(context);
        initView(context);
    }

    public OptionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OptionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.button_option, this);
        mRoot = (FrameLayout) mView.findViewById(R.id.layout_root);
        progressBarVote = (ProgressBar) mView.findViewById(R.id.prg_bar_vote);
        txtVoteNum = (TextView) mView.findViewById(R.id.txtVoteNum);
        txtVoteText = (TextView) mView.findViewById(R.id.txtVoteText);
        txtVoteNum.setVisibility(INVISIBLE);
        isVoted = false;
        this.setClickable(true);
    }

    private void gotoProgress() {
        new Thread() {
            @Override
            public void run() {
                for(int progress = 1; progress <= mProgress; progress++) {
                    progressBarVote.setProgress(progress);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void setVoted(boolean voted, boolean animation) {
        if(voted) {
            txtVoteNum.setVisibility(VISIBLE);
            if(animation) {
                gotoProgress();
            }
            else {
                progressBarVote.setProgress(mProgress);
            }
            isVoted = true;
        }
        else {
            txtVoteNum.setVisibility(INVISIBLE);
            progressBarVote.setProgress(0);
            isVoted = false;
        }
    }

    public void setVoteNumVisibility(int visibility) {
        txtVoteNum.setVisibility(visibility);
    }
    public void setProgress(int progress) {mProgress = progress;}
    public void setVoteNum(int voteNum) {
        txtVoteNum.setText(Integer.toString(voteNum));
    }
    public void setVoteText(String voteText) {
        txtVoteText.setText(voteText);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
    }

}
