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

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Adapter.QuestionCardAdapter;
import com.yahoo.mobile.itern.guagua.Event.CollectionEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by fanwang on 7/23/15.
 */

public class CollectionFragment extends Fragment {
    private View mRootView;
    private ParseUser mUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseObject> mCollection;
    private QuestionCardAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mUserId = getArguments().getString("objectId");

        mCollection = new ArrayList<>();
        mAdapter = new QuestionCardAdapter(getActivity(), mCollection);

        mUser = ParseUser.getCurrentUser();
        ParseUtils.getAllCollections(mUser);
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
        mRootView = inflater.inflate(R.layout.fragment_collection, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.collection_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //ParseUtils.getAllCollections(mUserId);
    }

    public void onEvent(CollectionEvent event) {
        Log.d("eventbus", "" + event.collectionList.size());
        refreshList(event.collectionList);
    }

    private void refreshList(List<ParseObject> list) {
        mCollection.clear();
        mCollection.addAll(list);
        mAdapter.flushFilter();
        mAdapter.notifyDataSetChangedWithCache();
    }
}
