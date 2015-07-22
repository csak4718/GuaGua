package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseKeys;

import java.util.List;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<ParseObject> mCommentList;
    private LayoutInflater mInflater;

    public CommentAdapter(Context context, List<ParseObject> list) {
        super();
        mContext = context;
        mCommentList = list;
        mInflater = LayoutInflater.from(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgCommentProfile;
        public TextView txtCommentName;
        public TextView txtCommentMsg;
        public ViewHolder(View itemView) {
            super(itemView);
            imgCommentProfile = (ImageView) itemView.findViewById(R.id.img_comment_profile);
            txtCommentName = (TextView) itemView.findViewById(R.id.txt_comment_name);
            txtCommentMsg = (TextView) itemView.findViewById(R.id.txt_comment_msg);
        }
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        final ParseObject comment = mCommentList.get(position);
        final String commentMsg = comment.getString(ParseKeys.OBJECT_COMMENT_MSG);
        holder.txtCommentMsg.setText(commentMsg);

    }

    @Override
    public long getItemId(int position) {
        return mCommentList.get(position).hashCode();
    }

    @Override
    public int getItemCount() { return mCommentList.size(); }
}
