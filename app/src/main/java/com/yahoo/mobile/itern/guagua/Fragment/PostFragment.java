package com.yahoo.mobile.itern.guagua.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.yahoo.mobile.itern.guagua.R;
import com.yahoo.mobile.itern.guagua.Util.ParseUtils;

public class PostFragment extends Fragment {

    View mView;
    EditText edtQuestion;
    EditText edtOptA;
    EditText edtOptB;
    Button btnPost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_post, container, false);
        edtQuestion = (EditText) mView.findViewById(R.id.edt_question);
        edtOptA = (EditText) mView.findViewById(R.id.edt_optA);
        edtOptB = (EditText) mView.findViewById(R.id.edt_optB);
        btnPost = (Button) mView.findViewById(R.id.btn_post_question);
        btnPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String question = edtQuestion.getText().toString();
                final String optionA = edtOptA.getText().toString();
                final String optionB = edtOptB.getText().toString();
                ParseUtils.postQuestions(question, optionA, optionB);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new MainActivityFragment())
                        .commit();

            }
        });
        return mView;
    }

}
