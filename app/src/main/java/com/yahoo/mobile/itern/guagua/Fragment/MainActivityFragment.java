package com.yahoo.mobile.itern.guagua.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;
import com.yalantis.phoenix.PullToRefreshView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    View mView;
    ListView mListView;
    PullToRefreshView mPullToRefreshView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) mView.findViewById(R.id.list_view);
        mPullToRefreshView = (PullToRefreshView) mView.findViewById(R.id.pull_to_refresh);
        String[] item = new String[] {"abc", "ggg"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,item);
        mListView.setAdapter(adapter);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 1);
            }
        });


        return mView;
    }
}
