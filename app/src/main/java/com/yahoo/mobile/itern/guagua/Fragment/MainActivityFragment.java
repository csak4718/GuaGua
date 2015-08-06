package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Adapter.QuestionCardAdapter;
import com.yahoo.mobile.itern.guagua.Event.QuestionEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View mView;
    private FloatingActionButton mBtnAddPost;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PullToRefreshView mPullToRefreshView;
    private QuestionCardAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private List<ParseObject> mList;

    private LinearLayout emptyCommunityRoot;

    public MainActivityFragment() {
    }

    public void setFilter(final String queryText) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.setFilter(queryText);
            }
        });
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

    public void onEvent(QuestionEvent event) {
        Log.d("eventbus", "" + event.questionList.size());
        refreshList(event.questionList);
    }

    private void refreshList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        
        mAdapter.flushFilter();
        mAdapter.notifyDataSetChangedWithCache();
        mPullToRefreshView.setRefreshing(false);

        mBtnAddPost.setColorNormal(Utils.getCurrentActionBarColor(getActivity()));
        if(Utils.isBrowsingAllCommunity(getActivity())) {
            mBtnAddPost.setVisibility(View.GONE);
        }
        else {
            mBtnAddPost.setVisibility(View.VISIBLE);
        }

        if(mList.size() == 0) {
            emptyCommunityRoot.setVisibility(View.VISIBLE);
        }
        else {
            emptyCommunityRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mBtnAddPost = (FloatingActionButton) mView.findViewById(R.id.btn_add_post);
        if(Utils.isBrowsingAllCommunity(getActivity())) {
            mBtnAddPost.setVisibility(View.GONE);
        }

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        emptyCommunityRoot = (LinearLayout) getView().findViewById(R.id.empty_community_root);

        mLayoutManager = new LinearLayoutManager(getActivity());

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        mList = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(getActivity(), mList);
        mAdapter.setLikeAnimation(true);

        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);

        // set up pull to refresh
        mPullToRefreshView = (PullToRefreshView) mView.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.post(new Runnable() {
                    @Override
                    public void run() {
                        ParseUtils.getCurrentCommunityQuestions(getActivity());
                    }
                });
            }
        });

        mBtnAddPost.attachToRecyclerView(mRecyclerView);
        mBtnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoAddPostActivity(getActivity());
            }
        });
        mBtnAddPost.setColorNormal(Utils.getCurrentActionBarColor(getActivity()));

        ParseUtils.getCurrentCommunityQuestions(getActivity());
    }
}
