package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;

import java.util.List;

/**
 * Created by cmwang on 7/16/15.
 */
public class QuestionCardAdapter extends RecyclerView.Adapter<QuestionCardAdapter.ViewHolder> {


    private List<ParseObject> mQuestionList;
    private Context mContext;
    private LayoutInflater mInflater;

    public QuestionCardAdapter(Context context, List<ParseObject> list) {
        super();
        mContext = context;
        mQuestionList = list;
        mInflater = LayoutInflater.from(context);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView txtTitle;
        public Button btnA;
        public Button btnB;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            txtTitle = (TextView) v.findViewById(R.id.title);
            btnA = (Button) v.findViewById(R.id.btnA);
            btnB = (Button) v.findViewById(R.id.btnB);
        }
    }

    @Override
    public QuestionCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject mQuestion = mQuestionList.get(position);
        holder.txtTitle.setText(mQuestion.getString("prayer"));
        holder.btnA.setText(mQuestion.getString("QA"));
        holder.btnB.setText(mQuestion.getString("QB"));
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }
}
