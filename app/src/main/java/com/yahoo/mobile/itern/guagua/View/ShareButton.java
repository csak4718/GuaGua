package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/31/15.
 */
public class ShareButton extends LinearLayout {

    Context mContext;
    View mView;
    LinearLayout mRoot;
    TextView badgeCount;

    public ShareButton(Context context) {
        super(context);
        initView(context);
    }

    public ShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.button_share, this);
        mRoot = (LinearLayout) mView.findViewById(R.id.btn_share_root);
        badgeCount = (TextView) mView.findViewById(R.id.badgeCount);

    }

    public void setBadgeCount(int count) {
        badgeCount.setText(String.valueOf(count));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
    }

    public void addShareNum(){
        int temp = Integer.valueOf(badgeCount.getText().toString());
        String str;
        if ((temp + 1) == 0){
            str = "Like";
        } else {
            str = String.valueOf(temp + 1);
        }
        badgeCount.setText(str);
    }
    public int getShareNum(){
        return Integer.valueOf(badgeCount.getText().toString());
    }
    public void setTextAlpha(int i){
        int color = badgeCount.getCurrentTextColor();
        int newColor = Color.argb(i, Color.red(color), Color.green(color), Color
                .blue(color));
    }
}
