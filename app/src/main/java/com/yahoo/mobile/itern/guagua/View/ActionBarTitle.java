package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/26/15.
 */
public class ActionBarTitle extends RelativeLayout {

    View mView;
    LinearLayout mRoot;
    Button mTitle;
    View mIndicator;

    public ActionBarTitle(Context context) {
        super(context);
        initView(context);
    }

    public ActionBarTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ActionBarTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.action_bar_main, this);
        mRoot = (LinearLayout) mView.findViewById(R.id.action_bar_main_root);
        mTitle = (Button) mView.findViewById(R.id.btn_action_bar_title);
        mIndicator = (View) mView.findViewById(R.id.action_bar_indicator);
    }

    public void setText(String text) {
        mTitle.setText(text);
    }

    public void animateExpand() {
        RotateAnimation animation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        mIndicator.startAnimation(animation);
    }
    public void animateCollapse() {
        RotateAnimation animation = new RotateAnimation(180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        mIndicator.startAnimation(animation);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
        mTitle.setOnClickListener(l);
    }
}
