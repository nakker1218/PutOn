package com.shohei.put_on.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.MemoAdapter;
import com.shohei.put_on.model.Memo;

import java.util.Date;
import java.util.List;


public class MemoDetailActivity extends ActionBarActivity implements TextWatcher {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    private Memo memo;

    private boolean mIsCreatedMode;

    EditText titleEditText;
    EditText memoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.memoDetail_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        memo = new Memo();

        titleEditText = (EditText) findViewById(R.id.title_EditText);
        memoEditText = (EditText) findViewById(R.id.memo_EditText);

        titleEditText.addTextChangedListener(this);
        memoEditText.addTextChangedListener(this);

        setMemo();
    }

    private void saveMemo() {
        final String title = titleEditText.getText().toString();
        final String memo = memoEditText.getText().toString();

        if (TextUtils.isEmpty(memo)) return;
        this.memo.title = TextUtils.isEmpty(title) ? "無題のタイトル" : title;
        this.memo.memo = memo;
        Date date = new Date(System.currentTimeMillis());
        this.memo.date = Memo.DATE_FORMAT.format(date);
        this.memo.save();

    }

    public void setMemo() {
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        if (!TextUtils.isEmpty(date)) {
            List<Memo> lists = new Select().from(Memo.class).where("date = ?", intent.getStringExtra("date")).execute();
            memo = lists.get(0);
            titleEditText.setText(memo.title);
            memoEditText.setText(memo.memo);
        }
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
//        if (id == R.id.save_menu) {
//            saveMemo();
//            finish();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    int currentLength = 0;

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
