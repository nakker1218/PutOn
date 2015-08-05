package com.nakanakashoshosho1218.memo_sample.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.nakanakashoshosho1218.memo_sample.R;
import com.nakanakashoshosho1218.memo_sample.controller.MemoAdapter;
import com.nakanakashoshosho1218.memo_sample.model.Memo;

import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    ListView mMemoListView;
    private MemoAdapter mMemoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMemoListView = (ListView)findViewById(R.id.memo_ListView);
    }

    private void setMemoListView() {
        List<Memo> memoList = new Select().from(Memo.class).execute();
        Collections.sort(memoList, new Memo.DateTimeComparator());
        mMemoAdapter = new MemoAdapter(this, R.layout.memo_adapter, memoList);
        mMemoListView.setAdapter(mMemoAdapter);
        mMemoListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mMemoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Position: " + view.getClass().getSimpleName());

                Memo memo = mMemoAdapter.getItem(position);
                mMemoAdapter.changeSelect(memo);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setMemoListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_menu) {
            Intent intent = new Intent(MainActivity.this, MemoCreateActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.delete_menu) {
            mMemoAdapter.deleteAll();
        }

        return super.onOptionsItemSelected(item);
    }
}
