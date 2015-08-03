package com.nakanakashoshosho1218.memo_sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MemoCreateActivity extends ActionBarActivity {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    private Memo memo;

    EditText titleEditText;
    EditText memoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_create);

        memo = new Memo();

        titleEditText = (EditText) findViewById(R.id.title_EditText);
        memoEditText = (EditText) findViewById(R.id.memo_EditText);
    }

    private void saveMemo() {
        memo.title = titleEditText.getText().toString();
        memo.memo = memoEditText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        memo.date = dateFormat.format(date);
        memo.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_menu) {
            saveMemo();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
