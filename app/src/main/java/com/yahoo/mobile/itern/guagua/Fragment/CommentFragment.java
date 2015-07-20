package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentFragment extends Fragment {
    View mView;
    ParseObject mParseObject;

    public static Fragment newInstance(String objectId) {
        Fragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("objectId", objectId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_comment, container, false);


        return mView;
    }
}
