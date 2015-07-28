package com.yahoo.mobile.itern.guagua.Activity;

import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.provider.MediaStore;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.yahoo.mobile.itern.guagua.Fragment.AddPostActivityFragment;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.Utils;


public class AddPostActivity extends ActionBarActivity {

    private AddPostActivityFragment addPostFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Utils.setCommunityActionBarColor(this);

        addPostFragment = new AddPostActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_post_content_frame, addPostFragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

            AddPostActivityFragment myf = (AddPostActivityFragment) getSupportFragmentManager().findFragmentById(R.id.add_post_content_frame);
            myf.change_Image(picturePath);

        }else if(data != null && requestCode == 12345){
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            Uri imageUri = data.getData();
            String[] fileColumn = { MediaStore.Images.Media.DATA };
            Cursor imageCursor = getContentResolver().query(imageUri,fileColumn, null, null, null);
            imageCursor.moveToFirst();
            int ori = imageCursor.getInt(0);
            Log.d("Photo",String.valueOf(ori));
            int fileColumnIndex = imageCursor.getColumnIndex(fileColumn[0]);
            String picturePath = imageCursor.getString(fileColumnIndex);
            Log.d("Photo", picturePath);
            Log.d("Photo",String.valueOf(imageUri));
            AddPostActivityFragment myf = (AddPostActivityFragment) getSupportFragmentManager().findFragmentById(R.id.add_post_content_frame);
            myf.change_Image(picturePath);
        }
    }
}
