package com.yahoo.mobile.itern.guagua.Activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Adapter.QuestionCardAdapter;
import com.yahoo.mobile.itern.guagua.Event.QuestionEvent;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private QuestionCardAdapter mAdapter;

    private List<ParseObject> mList;

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Utils.gotoMainActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setCommunityActionBarColor(this);
        Utils.setCommunityStatusBarColor(this);

        mList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new QuestionCardAdapter(this, mList);
        mAdapter.setLikeAnimation(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        Uri uri = getIntent().getData();
        String objectId = uri.getPath().substring(1);
        Log.d("Question id", objectId);
        ParseUtils.getQuestion(objectId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Utils.gotoMainActivity(this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
