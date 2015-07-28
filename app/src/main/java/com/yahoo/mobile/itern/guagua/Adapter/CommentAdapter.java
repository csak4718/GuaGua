package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
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
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MainViewHolder> {

    private Context mContext;
    private List<ParseObject> mCommentList;
    private LayoutInflater mInflater;

    private enum ViewType {
        MY_COMMENT,
        OTHERS_COMMENT;
    }

    public CommentAdapter(Context context, List<ParseObject> list) {
        super();
        mContext = context;
        mCommentList = list;
        mInflater = LayoutInflater.from(context);
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }
    public static class OtherViewHolder extends MainViewHolder {
        public ImageView imgCommentProfile;
        public TextView txtCommentName;
        public TextView txtCommentMsg;
        public OtherViewHolder(View itemView) {
            super(itemView);
            imgCommentProfile = (ImageView) itemView.findViewById(R.id.img_comment_profile);
            txtCommentName = (TextView) itemView.findViewById(R.id.txt_comment_name);
            txtCommentMsg = (TextView) itemView.findViewById(R.id.txt_comment_msg);
        }
    }
    public static class MyViewHolder extends MainViewHolder {
        public TextView txtCommentMsg;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtCommentMsg = (TextView) itemView.findViewById(R.id.txt_my_comment_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final ParseObject comment = mCommentList.get(position);
        final String currentUserId = ParseUser.getCurrentUser().getObjectId();
        final String commentUid = comment.getString(Common.OBJECT_COMMENT_USER_ID);
        if(commentUid != null && commentUid.equals(currentUserId)) {
            return ViewType.MY_COMMENT.ordinal();
        }
        else {
            return ViewType.OTHERS_COMMENT.ordinal();
        }
    }

    private void resetOtherViewHolder(OtherViewHolder holder) {
        holder.imgCommentProfile.setImageBitmap(
                BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.ic_account_circle_black_48dp));
        holder.txtCommentName.setText("Fan Fan");
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MainViewHolder vh;
        if(viewType == ViewType.MY_COMMENT.ordinal()) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_layout_right, parent, false);
            vh = new MyViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_layout_left, parent, false);
            vh = new OtherViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder mainViewHolder, int position) {

        if(mainViewHolder.getItemViewType() == ViewType.MY_COMMENT.ordinal()) {
            final MyViewHolder holder = (MyViewHolder) mainViewHolder;
            final ParseObject comment = mCommentList.get(position);
            final String commentMsg = comment.getString(Common.OBJECT_COMMENT_MSG);
            holder.txtCommentMsg.setText(commentMsg);
        }
        if(mainViewHolder.getItemViewType() == ViewType.OTHERS_COMMENT.ordinal()) {
            final OtherViewHolder holder = (OtherViewHolder) mainViewHolder;
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
            else {
                resetOtherViewHolder(holder);
            }

            final String commentMsg = comment.getString(Common.OBJECT_COMMENT_MSG);
            holder.txtCommentMsg.setText(commentMsg);
        }
    }

    @Override
    public long getItemId(int position) {
        return mCommentList.get(position).hashCode();
    }

    @Override
    public int getItemCount() { return mCommentList.size(); }
}
