package com.yahoo.mobile.itern.guagua.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.itern.guagua.Application.MainApplication;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;
import com.yahoo.mobile.itern.guagua.Util.Utils;

import java.io.File;


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

    FrameLayout imgPreviewRoot;
    ImageView imgViewUpload;
    ImageButton imgBtnPreviewDelete;
    Uri mImageUri;

    ImageButton imgBtnCamera;
    ImageButton imgBtnPicture;
    Switch btnShareFbSwitch;
    Switch btnTwoChoiceSwitch;
    boolean enableFBshare = false;
    boolean postWithPicture = false;

    private Boolean mTwoChoiceQuestion = true;

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

        imgPreviewRoot = (FrameLayout) mView.findViewById(R.id.img_preview_root);
        imgViewUpload = (ImageView) mView.findViewById(R.id.img_view_upload);
        imgBtnPreviewDelete = (ImageButton) mView.findViewById(R.id.img_btn_preview_delete);

        imgBtnCamera = (ImageButton) mView.findViewById(R.id.img_btn_camera);
        imgBtnPicture = (ImageButton) mView.findViewById(R.id.img_btn_picture);

        btnTwoChoiceSwitch = (Switch) mView.findViewById(R.id.switch_two_choice);
        btnShareFbSwitch = (Switch) mView.findViewById(R.id.share_switch);

        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromCamera();
            }
        });
        imgBtnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromGallery();
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

        imgBtnPreviewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postWithPicture = false;
                imgPreviewRoot.setVisibility(View.GONE);
            }
        });

        postWithPicture = false;

        return mView;
    }

    public void setImgViewUpload(Uri uri) {

        mImageUri = uri;

        Picasso.with(getActivity())
                .load(uri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        postWithPicture = true;
    }

    public void setImgViewUpload() {

        getActivity().getContentResolver().notifyChange(mImageUri, null);

        Picasso.with(getActivity())
                .load(mImageUri)
                .resize(640, 480)
                .centerInside()
                .into(imgViewUpload);
        imgPreviewRoot.setVisibility(View.VISIBLE);
        postWithPicture = true;
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= getActivity().getExternalCacheDir();
        return File.createTempFile(part, ext, tempDir);
    }

    private void getPictureFromCamera() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photo;
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
            mImageUri = Uri.fromFile(photo);
            i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            getActivity().startActivityForResult(i, CAMERA_REQUEST);
        }
        catch(Exception e)
        {
            Log.v("guagua", "Can't create file to take picture!");
            Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
        }
    }
    private void getPictureFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    public boolean addPost(){
        final String question = edtQuestion.getText().toString();
        String optionA = "";
        String optionB = "";

        Bitmap bitmap = null;
        if(postWithPicture) {
            bitmap = ((BitmapDrawable) imgViewUpload.getDrawable()).getBitmap();
        }
        if(bitmap == null && question.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.post_toast_question_empty), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if(mTwoChoiceQuestion) {
            optionA = edtOptA.getText().toString();
            optionB = edtOptB.getText().toString();
            if(optionA.length() == 0 || optionB.length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.post_toast_option_empty), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        }

        ParseObject community = ((MainApplication) getActivity().getApplication()).currentViewingCommunity;

        ParseUtils.postQuestions(question, optionA, optionB, community, mTwoChoiceQuestion, bitmap);
        Utils.hideSoftKeyboard(getActivity());
        return true;
    }
}
