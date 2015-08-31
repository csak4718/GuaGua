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
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Adapter.QuestionCardAdapter;
import com.yahoo.mobile.itern.guagua.Event.MyQuestionsEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by fanwang on 7/23/15.
 */

public class MyQuestionFragment extends Fragment {
    private View mRootView;
    private ParseUser mUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseObject> mQuestions;
    private QuestionCardAdapter mAdapter;
    CallbackManager callbackManager;

    private LinearLayout emptyQuestionRoot;

    public static Fragment newInstance(CallbackManager callbackManager) {
        MyQuestionFragment fragment = new MyQuestionFragment();
        fragment.callbackManager = callbackManager;
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mUserId = getArguments().getString("objectId");
        mUser = ParseUser.getCurrentUser();
        ParseUtils.getMyQuestions(mUser);
        mQuestions = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(getActivity(), mQuestions, callbackManager);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_question, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.collection_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        emptyQuestionRoot = (LinearLayout) mRootView.findViewById(R.id.empty_my_question_root);
        //ParseUtils.getAllCollections(mUserId);
    }

    public void onEvent(MyQuestionsEvent event) {
        Log.d("eventbus", "" + event.myQuestionsList.size());
        refreshList(event.myQuestionsList);
    }

    private void refreshList(List<ParseObject> list) {
        mQuestions.clear();
        mQuestions.addAll(list);
        mAdapter.flushFilter();
        mAdapter.notifyDataSetChanged();

        if(mQuestions.size() == 0) {
            emptyQuestionRoot.setVisibility(View.VISIBLE);
        }
        else {
            emptyQuestionRoot.setVisibility(View.GONE);
        }
    }
}
