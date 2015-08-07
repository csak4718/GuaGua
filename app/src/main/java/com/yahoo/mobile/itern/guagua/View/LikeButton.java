package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/31/15.
 */
public class LikeButton extends LinearLayout {

    Context mContext;
    View mView;
    ImageView img;
    LinearLayout mRoot;
    TextView badgeCount;

    public LikeButton(Context context) {
        super(context);
        initView(context);
    }

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.button_like, this);
        mRoot = (LinearLayout) mView.findViewById(R.id.btn_like_root);
        badgeCount = (TextView) mView.findViewById(R.id.badgeCount);
        img = (ImageView) mView.findViewById(R.id.img);
    }

    public void setBadgeCount(int count) {
        badgeCount.setText(String.valueOf(count));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
    }

    public void setImgLike(){
        img.setImageResource(R.drawable.ic_like);
    }
    public void setImgDisLike(){
        img.setImageResource(R.drawable.ic_like1);
    }
    public void addBadgeCount(){
        badgeCount.setText(String.valueOf(Integer.valueOf(badgeCount.getText().toString()) + 1));
    }
    public void minusBadgeCount(){
        badgeCount.setText(String.valueOf(Integer.valueOf(badgeCount.getText().toString()) - 1));
    }
    public int getImgHeight(){
        return img.getHeight();
    }
}
