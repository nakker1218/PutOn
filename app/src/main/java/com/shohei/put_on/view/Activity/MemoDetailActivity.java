package com.shohei.put_on.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.Logger;
import com.shohei.put_on.model.Memo;

import java.util.List;

public class MemoDetailActivity extends AppCompatActivity implements TextWatcher {
    private final static String LOG_TAG = MemoDetailActivity.class.getSimpleName();

    private Memo mMemo;

    private EditText mTagEditText;
    private EditText mMemoEditText;

    private int mCurrentLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_toolbar_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] texts = getText();
                mMemo.saveMemo(texts[0], texts[1]);
                finish();
            }
        });

        mMemo = new Memo();

        mTagEditText = (EditText) findViewById(R.id.tag_EditText_Detail);
        mMemoEditText = (EditText) findViewById(R.id.memo_EditText_Detail);

        mTagEditText.addTextChangedListener(this);
        mMemoEditText.addTextChangedListener(this);

        setMemo();
        mMemoEditText.setSelection(mMemo.memo.length());
    }

    public void setMemo() {
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        if (!TextUtils.isEmpty(date)) {
            List<Memo> lists = new Select().from(Memo.class).where("date = ?", date).execute();
            mMemo = lists.get(0);
            mTagEditText.setText(mMemo.tag);
            mMemoEditText.setText(mMemo.memo);
        }
    }

    private String[] getText() {
        String[] texts = new String[2];
        texts[0] = mMemoEditText.getText().toString();
        texts[1] = mTagEditText.getText().toString();
        return texts;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCurrentLength = s.toString().length();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() < mCurrentLength) {
            return;
        }
        boolean unfixed = false;
        Object[] spanned = s.getSpans(0, s.length(), Object.class);
        if (spanned != null) {
            for (Object obj : spanned) {
                if (obj instanceof android.text.style.UnderlineSpan) {
                    unfixed = true;
                }
            }
        }
        //EditTextの入力が確定したら保存
        if (!unfixed) {
            final String[] texts = getText();
            mMemo.saveMemo(texts[0], texts[1]);
        }
    }
}