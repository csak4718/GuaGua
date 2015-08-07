package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/31/15.
 */
public class CommentButton2 extends LinearLayout {

    Context mContext;
    View mView;
    LinearLayout mRoot;
    TextView badgeCount;

    public CommentButton2(Context context) {
        super(context);
        initView(context);
    }

    public CommentButton2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CommentButton2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.button_comment2, this);
        mRoot = (LinearLayout) mView.findViewById(R.id.btn_comment_root);
        badgeCount = (TextView) mView.findViewById(R.id.badgeCount);
    }

    public void setBadgeCount(int count) {
        badgeCount.setText(String.valueOf(count));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
    }
}
