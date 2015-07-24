package com.yahoo.mobile.itern.guagua.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.inputmethod.InputMethodManager;

import com.parse.ParseUser;
import com.yahoo.mobile.itern.guagua.Activity.CommunityActivity;
import com.yahoo.mobile.itern.guagua.Activity.LoginActivity;
import com.yahoo.mobile.itern.guagua.Activity.MainActivity;

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
}
