package com.yahoo.mobile.itern.guagua.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 8/3/15.
 */
public class DrawerItemCommunity extends RelativeLayout {

    String mTitle;
    Drawable mIcon;

    View mView;
    LinearLayout mRoot;
    ImageView mImgIcon;
    TextView mTxtTitle;

    public DrawerItemCommunity(Context context) {
        super(context);
        initView(context);
    }
    public DrawerItemCommunity(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public DrawerItemCommunity(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.drawerItemCommunity,
                0, 0);
        try {
            mTitle = a.getString(R.styleable.drawerItemCommunity_communityTitle);
            mIcon = a.getDrawable(R.styleable.drawerItemCommunity_communityIcon);
            setTitle(mTitle);
            setIcon(mIcon);
        }
        finally {
            a.recycle();
        }

    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.drawer_community_item, this);
        mRoot = (LinearLayout) mView.findViewById(R.id.drawer_item_root);
        mTxtTitle = (TextView) mView.findViewById(R.id.drawer_item_title);
        mImgIcon = (ImageView) mView.findViewById(R.id.drawer_item_icon);
    }
    public void setTitle(final String title) {
        mTitle = title;
        if(mTitle != null) {
            mTxtTitle.setText(title);
        }
    }
    public void setIcon(final Drawable icon) {
        mIcon = icon;
        if(icon != null) {
            mImgIcon.setImageDrawable(mIcon);
        }
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
        mRoot.setOnClickListener(l);
    }
}
