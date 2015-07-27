package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

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
    public void onBindViewHolder(final CommentAdapter.ViewHolder holder, int position) {
        final ParseObject comment = mCommentList.get(position);
        final ParseUser user = comment.getParseUser(Common.OBJECT_COMMENT_USER);
        if(user != null)
        {
            user.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if(e == null) {
                        holder.txtCommentName.setText(user.getString(Common.OBJECT_USER_NICK));
                        ParseFile imgFile = user.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
                        ParseUtils.displayImage(imgFile, holder.imgCommentProfile);
                    }
                }
            });
        }

        final String commentMsg = comment.getString(Common.OBJECT_COMMENT_MSG);
        holder.txtCommentMsg.setText(commentMsg);

    }

    @Override
    public long getItemId(int position) {
        return mCommentList.get(position).hashCode();
    }

    @Override
    public int getItemCount() { return mCommentList.size(); }
}
