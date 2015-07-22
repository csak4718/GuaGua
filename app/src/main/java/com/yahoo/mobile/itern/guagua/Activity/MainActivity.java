package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yahoo.mobile.itern.guagua.Fragment.MainActivityFragment;
import com.yahoo.mobile.itern.guagua.Fragment.PostFragment;
import com.yahoo.mobile.itern.guagua.R;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MainActivityFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // For changing camera_btn img
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // To Handle Gallery Result
        Log.d("Photo", "in result");
        Log.d("Photo", String.valueOf(data == null));
        Log.d("Photo", String.valueOf(requestCode));
        if (data != null && requestCode == 1234) {
            Log.d("Photo", "in result");
            Uri selectedImageUri = data.getData();
            Log.d("Photo", "getdata");
            String[] fileColumn = { MediaStore.Images.Media.DATA };
            Log.d("Photo", "1");
            Cursor imageCursor = getContentResolver().query(selectedImageUri,
                    fileColumn, null, null, null);
            imageCursor.moveToFirst();
            int ori = imageCursor.getInt(0);
            Log.d("Photo",String.valueOf(ori));
            Log.d("Photo", "2");
            int fileColumnIndex = imageCursor.getColumnIndex(fileColumn[0]);
            String picturePath = imageCursor.getString(fileColumnIndex);
            Log.d("Photo", picturePath);
            //Bitmap pictureObject = BitmapFactory.decodeFile(picturePath);

            PostFragment myf = (PostFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            myf.change_Image(picturePath);

        }
    }
}
