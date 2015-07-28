package com.yahoo.mobile.itern.guagua.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Activity.CommentActivity;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.Activity.LoginActivity;
import com.yahoo.mobile.itern.guagua.Activity.MainActivity;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;

/**
 * Created by cmwang on 7/20/15.
 */
public class Utils {
    static public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    static public void gotoMainActivity(Context context) {
        Intent it = new Intent(context, MainActivity.class);
        context.startActivity(it);
    }
    static public void gotoCommunityActivity(Context context) {
        Intent it = new Intent(context, CommunityActivity.class);
        context.startActivity(it);
    }
    static public void gotoCommentActivity(Context context, String postId) {
        Intent it = new Intent(context, CommentActivity.class);
        it.putExtra(Common.EXTRA_COMMENT_POSTID, postId);
        context.startActivity(it);
    }
    static public boolean isBrowsingAllCommunity(Activity activity) {
        return getCurrentViewingCommunity(activity) == null;
    }
    static public ParseObject getCurrentViewingCommunity(Activity activity) {
        MainApplication app = (MainApplication) activity.getApplication();
        return app.currentViewingCommunity;
    }
    static public void userLogout(Context context) {
        ParseUser.logOut();
        Intent it = new Intent(context, LoginActivity.class);
        context.startActivity(it);
    }
    static public Bitmap sqr2circle(Bitmap bm){
        Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bm.getWidth(),bm.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bm.getWidth() / 2,bm.getHeight() / 2, bm.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, rect, rect, paint);
        return output;
    }

    static public void displayPromptForEnablingGPS(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "\"Gua Gua\" would like to collect your current location data.";

        builder.setMessage(message)
                .setPositiveButton("Agree",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                ((CommunityActivity)activity).onResume();
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Disagree",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                gotoMainActivity(activity);
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}
