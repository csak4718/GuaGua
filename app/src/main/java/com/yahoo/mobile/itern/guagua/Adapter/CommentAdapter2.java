package com.yahoo.mobile.itern.guagua.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Common;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmwang on 7/20/15.
 */
public class CommentAdapter2 extends RecyclerView.Adapter<CommentAdapter2.MainViewHolder> {

    private Context mContext;
    private List<ParseObject> mCommentList,mCommentLikeList;
    private LayoutInflater mInflater;

    private enum ViewType {
        MY_COMMENT,
        OTHERS_COMMENT;
    }


    public CommentAdapter2(Context context, List<ParseObject> list, List<ParseObject> likelist) {
        super();
        mContext = context;
        mCommentList = list;
        mCommentLikeList = likelist;
        mInflater = LayoutInflater.from(context);
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }
    public static class OtherViewHolder extends MainViewHolder {
        public ImageView imgCommentProfile;
        public ImageView imgCommentLike;
        public View boxLike;
        public TextView txtCommentName;
        public TextView txtCommentMsg;
        public TextView txtDate;
        public TextView numLikes;
        public boolean liked = false;
        public OtherViewHolder(View itemView) {
            super(itemView);
            imgCommentProfile = (ImageView) itemView.findViewById(R.id.img_comment_profile);
            txtCommentName = (TextView) itemView.findViewById(R.id.txt_comment_name);
            txtCommentMsg = (TextView) itemView.findViewById(R.id.txt_comment_msg);
            txtDate = (TextView) itemView.findViewById(R.id.txt_comment_date);
            boxLike = itemView.findViewById(R.id.comment_like_box);
            numLikes = (TextView) itemView.findViewById(R.id.num_comment_likes);
            imgCommentLike = (ImageView) itemView.findViewById(R.id.img_comment_like);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return ViewType.OTHERS_COMMENT.ordinal();
        /*
        final ParseObject comment = mCommentList.get(position);
        final String currentUserId = ParseUser.getCurrentUser().getObjectId();
        final String commentUid = comment.getString(Common.OBJECT_COMMENT_USER_ID);
        if(commentUid != null && commentUid.equals(currentUserId)) {
            return ViewType.MY_COMMENT.ordinal();
        }
        else {
            return ViewType.OTHERS_COMMENT.ordinal();
        }*/
    }

    private void resetOtherViewHolder(OtherViewHolder holder) {
        holder.imgCommentProfile.setImageBitmap(
                BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.ic_account_circle_black_48dp));
        holder.txtCommentName.setText("Fan Fan");
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*
        MainViewHolder vh;
        if(viewType == ViewType.MY_COMMENT.ordinal()) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_ver2, parent, false);
            vh = new MyViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_ver2, parent, false);
            vh = new OtherViewHolder(v);
        }*/
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_ver2, parent, false);
        MainViewHolder vh = new OtherViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder mainViewHolder, int position) {

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
        final Date date = comment.getCreatedAt();
        final int likes = comment.getInt(Common.OBJECT_COMMENT_LIKES);
        holder.txtCommentMsg.setText(commentMsg);
        holder.txtDate.setText(date2String(date));
        holder.numLikes.setText(String.valueOf(likes));

        setUpBoxLike(holder, comment);
    }

    @Override
    public long getItemId(int position) {
        return mCommentList.get(position).hashCode();
    }

    @Override
    public int getItemCount() { return mCommentList.size(); }

    private void setUpBoxLike(final OtherViewHolder holder, final ParseObject comment){
        holder.liked = mCommentLikeList.contains(comment);
        if (holder.liked){
            holder.imgCommentLike.setImageResource(R.drawable.ic_like);
            holder.numLikes.setTextColor(Color.RED);
        }else{
            holder.imgCommentLike.setImageResource(R.drawable.ic_like1);
            holder.numLikes.setTextColor(Color.GRAY);
        }

        holder.boxLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseRelation relation = currentUser.getRelation(Common.OBJECT_USER_COMMENT_LIKES);
                if (holder.liked){
                    holder.liked = false;
                    ParseUtils.likeComment(comment, false);
                    holder.numLikes.setTextColor(Color.GRAY);
                    holder.imgCommentLike.setImageResource(R.drawable.ic_like1);
                    holder.numLikes.setText(String.valueOf(Integer.valueOf(holder.numLikes.getText().toString
                            ()) - 1));
                    relation.remove(comment);
                    currentUser.saveInBackground();

                }else{
                    holder.liked = true;
                    ParseUtils.likeComment(comment, true);
                    holder.numLikes.setTextColor(Color.RED);
                    holder.imgCommentLike.setImageResource(R.drawable.ic_like);
                    holder.numLikes.setText(String.valueOf(Integer.valueOf(holder.numLikes.getText().toString
                            ()) + 1));
                    relation.add(comment);
                    currentUser.saveInBackground();
                }
                Log.d("CA2","in boxLike onCLick");
            }
        });
    }

    public String date2String(Date d){
        Date now = new Date();
        String str_date;
        int day = (int) TimeUnit.DAYS.convert(now.getTime()-d.getTime(),TimeUnit.MILLISECONDS);
        if (day > 1){
            str_date = DateUtils.formatDateTime(mContext, d.getTime(), DateUtils.FORMAT_SHOW_TIME
                    | DateUtils.FORMAT_SHOW_DATE);
        }else if(d.getYear()!= now.getYear()){
            str_date = DateUtils.formatDateTime(mContext,d.getTime(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        }else {
            str_date = DateUtils.getRelativeTimeSpanString(d.getTime()).toString();
        }
        return str_date;
    }
}
