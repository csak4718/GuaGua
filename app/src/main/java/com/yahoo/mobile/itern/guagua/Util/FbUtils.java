package com.yahoo.mobile.itern.guagua.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.yahoo.mobile.itern.guagua.Event.FbPictureEvent;
import com.yahoo.mobile.itern.guagua.Event.UserProfileEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by cmwang on 7/22/15.
 */
public class FbUtils {
    static public void getUserProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    String fbId = jsonObject.getString("id");
                    String nickName = jsonObject.getString("name");
                    Log.d("fb_name", nickName);
                    Log.d("fb_id", fbId);
                    EventBus.getDefault().post(new UserProfileEvent(fbId, nickName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }
    static public void getFbProfilePicture(final String uid) {
        Runnable getPicRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    URL imageURL = new URL("https://graph.facebook.com/" + uid + "/picture?type=large");
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    EventBus.getDefault().post(new FbPictureEvent(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(getPicRunnable)
                .start();
    }
}
