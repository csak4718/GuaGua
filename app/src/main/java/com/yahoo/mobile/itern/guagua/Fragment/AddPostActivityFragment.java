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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddPostActivityFragment extends Fragment {
    public static final int CAMERA_REQUEST = 12345;
    public static final int ACTIVITY_SELECT_IMAGE = 1234;

    View mView;
    EditText edtQuestion;

    LinearLayout optionContainer;
    EditText edtOptA;
    EditText edtOptB;

    ImageButton btnCameraA;
    ImageButton btnCameraB;
    Switch btnShareFbSwitch;
    Switch btnTwoChoiceSwitch;
    boolean enableFBshare=false;
    boolean aorb;

    private Boolean mTwoChoiceQuestion = true;

    public AddPostActivityFragment() {
    }

    public boolean getEnableFBshare(){
        return enableFBshare;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_post, container, false);
        edtQuestion = (EditText) mView.findViewById(R.id.edt_question);
        optionContainer = (LinearLayout) mView.findViewById(R.id.option_container);
        edtOptA = (EditText) mView.findViewById(R.id.edt_optA);
        edtOptB = (EditText) mView.findViewById(R.id.edt_optB);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_local_see_black_48dp);
        btnCameraA = (ImageButton) mView.findViewById(R.id.btn_cameraA);
        btnCameraA.setImageBitmap(sqr2circle(adjustBitmap(bm,bm.getHeight())));
        btnCameraB = (ImageButton) mView.findViewById(R.id.btn_cameraB);
        btnCameraB.setImageBitmap(sqr2circle(adjustBitmap(bm,bm.getHeight())));
        btnTwoChoiceSwitch = (Switch) mView.findViewById(R.id.switch_two_choice);
        btnShareFbSwitch = (Switch) mView.findViewById(R.id.share_switch);

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

        btnShareFbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) enableFBshare = true;
                else enableFBshare = false;
            }
        });
        btnTwoChoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTwoChoiceQuestion = isChecked;
                if(mTwoChoiceQuestion) {
                    optionContainer.setVisibility(View.VISIBLE);
                }
                else {
                    optionContainer.setVisibility(View.GONE);
                }
            }
        });

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
    public void change_Image(Bitmap bm){
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
                .setTitle("Select a picture to upload")
                .setMessage("From camera or gallery?")
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

    public void addPost(){
        final String question = edtQuestion.getText().toString();
        String optionA = "";
        String optionB = "";

        if(mTwoChoiceQuestion) {
            optionA = edtOptA.getText().toString();
            optionB = edtOptB.getText().toString();
        }

        final ParseObject community = ((MainApplication) getActivity().getApplication()).currentViewingCommunity;
        ParseUtils.postQuestions(question, optionA, optionB, community, mTwoChoiceQuestion);
        Utils.hideSoftKeyboard(getActivity());
    }
}
