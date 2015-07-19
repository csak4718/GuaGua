package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/19/15.
 */
public class OptionButton extends RelativeLayout {
    View mView;
    RelativeLayout mRoot;
    TextView txtVoteNum;
    TextView txtVoteText;
    Boolean isVoted;

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
        mView = inflater.inflate(R.layout.option_button, this);
        mRoot = (RelativeLayout) mView.findViewById(R.id.layout_root);
        txtVoteNum = (TextView) mView.findViewById(R.id.txtVoteNum);
        txtVoteText = (TextView) mView.findViewById(R.id.txtVoteText);
        txtVoteNum.setVisibility(INVISIBLE);
        isVoted = false;
        this.setClickable(true);
    }

    public void setVoted(boolean voted) {
        if(voted) {
            txtVoteNum.setVisibility(VISIBLE);
            mRoot.setBackgroundResource(R.drawable.opton_button_after_pressed);
            isVoted = true;
        }
        else {
            txtVoteNum.setVisibility(INVISIBLE);
            mRoot.setBackgroundResource(R.drawable.option_button);
            isVoted = false;
        }
    }

    public void setVoteNumVisibility(int visibility) {
        txtVoteNum.setVisibility(visibility);
    }

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
