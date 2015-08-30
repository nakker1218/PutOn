package com.shohei.put_on.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.LocationUtil;
import com.shohei.put_on.model.Memo;

import java.util.List;

public class MemoDetailActivity extends ActionBarActivity implements TextWatcher {
    private final static String LOG_TAG = MemoDetailActivity.class.getSimpleName();

    private Memo mMemo;
//    private LocationUtil mLocationUtil;

    EditText mTagEditText;
    EditText mMemoEditText;
//    CheckBox mLocationCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.memoDetail_Toolbar);
        toolbar.setTitle(R.string.title_toolbar_detail_activity);
        toolbar.setNavigationIcon(R.mipmap.ic_done);
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

//        mLocationCheckBox = (CheckBox) findViewById(R.id.location_CheckBox);

//        mLocationUtil = new LocationUtil(this);

        setMemo();
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

//    public void locationCheckBox(View v){
//        final boolean isChecked = ((CheckBox) v).isChecked();
//        if (isChecked){
//            mLocationUtil.requestLocation();
//            mLocationCheckBox.setText(mMemo.address);
//            if (DebugUtil.DEBUG) Log.d(LOG_TAG, mMemo.address);
//        } else {
//
//        }
//    }

    private String[] getText() {
        String[] texts = new String[2];
        texts[0] = mMemoEditText.getText().toString();
        texts[1] = mTagEditText.getText().toString();
        return texts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_ditail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private int currentLength = 0;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        currentLength = s.toString().length();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() < currentLength) {
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
