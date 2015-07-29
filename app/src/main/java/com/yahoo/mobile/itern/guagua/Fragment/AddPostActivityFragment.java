package com.yahoo.mobile.itern.guagua.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;
import com.yahoo.mobile.itern.guagua.View.ActionBarTitle;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddPostActivityFragment extends Fragment {
    final int CAMERA_REQUEST = 12345;
    final int ACTIVITY_SELECT_IMAGE = 1234;

    View mView;
    EditText edtQuestion;
    EditText edtOptA;
    EditText edtOptB;
    Button btnPost;
    Button btnCancel;
    ImageButton btnCameraA;
    ImageButton btnCameraB;
    Switch btnSwitch;
    boolean enableFBshare=false;
    boolean aorb;

//    ImageButton imgBtnPost;
//    ImageButton imgBtnCancel;

    private HorizontalScrollView mScrollBannerBadge;
    private LinearLayout mBannerBadge;
    private ImageButton mImgBtnBadgeSearch;
    private boolean badgeBannerVisible = false;

    private ActionBarTitle mActionBarTitle;

//    private void setupActionBar() {
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setCustomView(R.layout.action_bar_post);
//
//
//
//
//        mActionBarTitle = new ActionBarTitle(this);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5AD3D2")));
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setElevation(0);
//        actionBar.setCustomView(mActionBarTitle);
////        hideBdgeBanner(0);
//
//        ParseUtils.getUserCommunity(ParseUser.getCurrentUser());
//
//        mActionBarTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!badgeBannerVisible) {
//                    showBdgeBanner(300);
//                } else {
//                    hideBdgeBanner(300);
//                }
//            }
//        });
//        mImgBtnBadgeSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.gotoCommunityActivity(MainActivity.this);
//            }
//        });
//        mBtnBadgeAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainApplication app = (MainApplication)getApplication();
//                if(app.currentViewingCommunity != null) {
//                    ParseUtils.getAllQuestions();
//                    app.currentViewingCommunity = null;
//                    mActionBarTitle.setText(getString(R.string.app_name));
//                    hideBdgeBanner(300);
//                }
//            }
//        });
//    }



    public AddPostActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_post, container, false);
        edtQuestion = (EditText) mView.findViewById(R.id.edt_question);
        edtOptA = (EditText) mView.findViewById(R.id.edt_optA);
        edtOptB = (EditText) mView.findViewById(R.id.edt_optB);
        btnPost = (Button) mView.findViewById(R.id.btn_post_question);
        btnCancel = (Button) mView.findViewById(R.id.btn_cancel);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_local_see_black_48dp);
        btnCameraA = (ImageButton) mView.findViewById(R.id.btn_cameraA);
        btnCameraA.setImageBitmap(sqr2circle(adjustBitmap(bm,bm.getHeight())));
        btnCameraB = (ImageButton) mView.findViewById(R.id.btn_cameraB);
        btnCameraB.setImageBitmap(sqr2circle(adjustBitmap(bm,bm.getHeight())));
        btnSwitch = (Switch) mView.findViewById(R.id.share_switch);


//        imgBtnPost = (ImageButton) mView.findViewById(R.id.imgBtn_post_question);
//        imgBtnCancel = (ImageButton) mView.findViewById(R.id.imgBtn_cancel);
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setCustomView(R.layout.action_bar_post);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);


        mScrollBannerBadge = (HorizontalScrollView) mView.findViewById(R.id.scroll_banner_badge);
        mBannerBadge = (LinearLayout) mView.findViewById(R.id.banner_badge);
        mImgBtnBadgeSearch = (ImageButton) mView.findViewById(R.id.img_btn_badge_search);




        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String question = edtQuestion.getText().toString();
                final String optionA = edtOptA.getText().toString();
                final String optionB = edtOptB.getText().toString();
                final ParseObject community = ((MainApplication) getActivity().getApplication()).currentViewingCommunity;
                ParseUtils.postQuestions(question, optionA, optionB, community);
                Utils.hideSoftKeyboard(getActivity());
                getActivity().finish();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(getActivity());
                getActivity().finish();
            }
        });
        btnCameraA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aorb = false;
                cameraORgallery();
                //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_black_48dp);
                //btnCamera.setImageBitmap(bm);
            }
        });
        btnCameraB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aorb = true;
                cameraORgallery();
                //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_black_48dp);
                //btnCamera.setImageBitmap(bm);

            }
        });

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) enableFBshare = true;
                else enableFBshare = false;
            }
        });

//        setupActionBar();

        return mView;
    }

    //Change button pic after select a picture
    public void change_Image(String url){
        Bitmap bm = BitmapFactory.decodeFile(url);
        Log.d("Photo", "here");
        if (aorb == false) {
            bm = adjustBitmap(bm, btnCameraA.getHeight());
            btnCameraA.setImageBitmap(bm);
        }else{
            bm = adjustBitmap(bm, btnCameraB.getHeight());
            btnCameraB.setImageBitmap(bm);
        }
    }

    //Adjust bitmap
    private Bitmap adjustBitmap(Bitmap srcBmp, int side){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth()/2 - srcBmp.getHeight()/2, 0, srcBmp.getHeight(), srcBmp.getHeight());

        }else{
            dstBmp = Bitmap.createBitmap(srcBmp, 0,srcBmp.getHeight()/2 - srcBmp.getWidth()/2, srcBmp.getWidth(),srcBmp.getWidth());
        }
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(srcBmp,srcBmp.getWidth()/2 - srcBmp.getHeight()/2,0,srcBmp.getHeight(),srcBmp.getHeight());

        }else{
            dstBmp = Bitmap.createBitmap(srcBmp,0,srcBmp.getHeight()/2 - srcBmp.getWidth()/2,srcBmp.getWidth(),srcBmp.getWidth());
        }
        dstBmp = Bitmap.createScaledBitmap(dstBmp,side,side,true);
        return sqr2circle(dstBmp);
    }

    //Turn it to cirlcle
    private Bitmap sqr2circle(Bitmap bm){
        Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bm.getWidth(),bm.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bm.getWidth() / 2, bm.getHeight() / 2, bm.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, rect, rect, paint);
        return output;
    }

    private void cameraORgallery(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        getActivity().startActivityForResult(i ,CAMERA_REQUEST);
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        Log.d("Photo", "before act");
                        getActivity().startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
                        Log.d("Photo", "after act");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
