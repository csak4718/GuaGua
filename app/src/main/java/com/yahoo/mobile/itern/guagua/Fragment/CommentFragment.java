package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yahoo.mobile.itern.guagua.Adapter.CommentAdapter2;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentFragment extends Fragment {

    private ParseObject mQuestion;

    private View mView;
    private EditText mEdtCommentText;
    private ImageButton mBtnCommentSend;
    private String mPostObjectId;
    private RecyclerView mRecyclerView;
    private CommentAdapter2 mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseObject> mList,mLikeList;

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

    private void refreshList() {
        ParseRelation<ParseObject> commentsRelation = mQuestion.getRelation(Common.OBJECT_POST_COMMENTS);
        ParseQuery<ParseObject> query = commentsRelation.getQuery();
        query.orderByAscending(Common.PARSE_COMMON_CREATED);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mList.clear();
                mList.addAll(list);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mList.size() - 1);
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mLikeList = ParseUser.getCurrentUser().getRelation(Common.OBJECT_USER_COMMENT_LIKES).getQuery().find();
        } catch (ParseException e){
            e.printStackTrace();
            getActivity().finish();
        }

        mView = inflater.inflate(R.layout.fragment_comment2, container, false);
        mEdtCommentText = (EditText) mView.findViewById(R.id.edt_comment_text);
        mBtnCommentSend = (ImageButton) mView.findViewById(R.id.btn_comment_send);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view_comment);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mList = new ArrayList<>();

        mAdapter = new CommentAdapter2(getActivity(), mList, mLikeList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        ParseQuery<ParseObject> query = ParseQuery.getQuery(Common.OBJECT_POST);
        try {
            mQuestion = query.get(mPostObjectId);
            refreshList();
        } catch (ParseException e) {
            e.printStackTrace();
            getActivity().finish();
        }

//        ParseUtils.getPostComments(mPostObjectId);

        mBtnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = mEdtCommentText.getText().toString();
                if (commentMsg.length() > 0) {

                    ParseUser currentUser = ParseUser.getCurrentUser();

                    final ParseObject mComment = new ParseObject(Common.OBJECT_COMMENT);
                    mComment.put(Common.OBJECT_COMMENT_POSTID, mQuestion.getObjectId());
                    mComment.put(Common.OBJECT_COMMENT_MSG, commentMsg);
                    mComment.put(Common.OBJECT_COMMENT_USER, currentUser);
                    mComment.put(Common.OBJECT_COMMENT_USER_ID, currentUser.getObjectId());
                    mComment.put(Common.OBJECT_COMMENT_LIKES, 0);

                    mComment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseRelation<ParseObject> commentsRelation = mQuestion.getRelation(Common.OBJECT_POST_COMMENTS);
                                commentsRelation.add(mComment);
                                mQuestion.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        refreshList();
                                    }
                                });
                            }
                        }
                    });

                    mEdtCommentText.setText("");
                    mEdtCommentText.clearFocus();
                    Utils.hideSoftKeyboard(getActivity());
                }
            }
        });

        return mView;
    }
}
