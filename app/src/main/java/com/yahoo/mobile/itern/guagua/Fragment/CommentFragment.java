package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Adapter.CommentAdapter;
import com.yahoo.mobile.itern.guagua.Event.CommentEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentFragment extends Fragment {

    private ParseObject mQuestion;

    private View mView;
    private EditText mEdtCommentText;
    private Button mBtnCommentSend;
    private String mPostObjectId;
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseObject> mList;

    public static Fragment newInstance(String objectId) {
        Fragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("objectId", objectId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostObjectId = getArguments().getString("objectId");
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(CommentEvent event) {
        Log.d("eventbus", "" + event.commentList.size());
        refreshList(event.commentList);
    }

    private void refreshList(List<ParseObject> commentList) {
        mList.clear();
        mList.addAll(commentList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mList.size() - 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_comment, container, false);
        mEdtCommentText = (EditText) mView.findViewById(R.id.edt_comment_text);
        mBtnCommentSend = (Button) mView.findViewById(R.id.btn_comment_send);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view_comment);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mList = new ArrayList<>();
        mAdapter = new CommentAdapter(getActivity(), mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ParseUtils.getPostComments(mPostObjectId);

        mBtnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = mEdtCommentText.getText().toString();
                if (commentMsg.length() > 0) {
                    ParseUtils.postComment(commentMsg, mPostObjectId, true);
                    mEdtCommentText.setText("");
                    mEdtCommentText.clearFocus();
                    Utils.hideSoftKeyboard(getActivity());
                }
            }
        });

        return mView;
    }
}
