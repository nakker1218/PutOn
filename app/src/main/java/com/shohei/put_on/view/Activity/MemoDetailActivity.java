package com.shohei.put_on.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.DebugUtil;
import com.shohei.put_on.controller.utils.LocationUtil;
import com.shohei.put_on.model.Memo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MemoDetailActivity extends ActionBarActivity implements TextWatcher {
    private final static String LOG_TAG = MemoDetailActivity.class.getSimpleName();

    private Memo mMemo;
    private LocationUtil mLocationUtil;

    EditText mTagEditText;
    EditText mMemoEditText;
//    CheckBox mLocationCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.memoDetail_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMemo = new Memo();

        mTagEditText = (EditText) findViewById(R.id.title_EditText);
        mMemoEditText = (EditText) findViewById(R.id.memo_EditText);

        mTagEditText.addTextChangedListener(this);
        mMemoEditText.addTextChangedListener(this);

//        mLocationCheckBox = (CheckBox) findViewById(R.id.location_CheckBox);

        mLocationUtil = new LocationUtil(this);

        setMemo();
    }

    private void saveMemo() {
        final String tag = mTagEditText.getText().toString();
        final String memo = mMemoEditText.getText().toString();

        if (TextUtils.isEmpty(memo)) return;
        final String[] tagResult = tag.split("\\s+");
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, tagResult.length + "");
        this.mMemo.tag = new ArrayList<>();
        for (String string : tagResult) {
            this.mMemo.tag.add(string);
        }

        this.mMemo.memo = memo;
        Date date = new Date(System.currentTimeMillis());
        this.mMemo.date = Memo.DATE_FORMAT.format(date);

        this.mMemo.save();
    }

    public void setMemo() {
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        if (!TextUtils.isEmpty(date)) {
            List<Memo> lists = new Select().from(Memo.class).where("date = ?", date).execute();
            mMemo = lists.get(0);
            String tag = mMemo.buildTextTag(mMemo.tag);
            mTagEditText.setText(tag.isEmpty() ? null : tag);
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
            saveMemo();
        }
    }
}
