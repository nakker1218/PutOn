package com.nakanakashoshosho1218.memo_sample.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.activeandroid.query.Select;
import com.melnykov.fab.FloatingActionButton;
import com.nakanakashoshosho1218.memo_sample.R;
import com.nakanakashoshosho1218.memo_sample.controller.MemoAdapter;
import com.nakanakashoshosho1218.memo_sample.model.Memo;

import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    private ListView mMemoListView;
    private Toolbar mainToolbar;

    private Button searchButton;
    private Button deleteButton;

    private MemoAdapter mMemoAdapter;
    private Memo mMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(mainToolbar);

        mMemoListView = (ListView) findViewById(R.id.memo_ListView);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        floatingActionButton.attachToListView(mMemoListView);
        searchButton = (Button) findViewById(R.id.search_menu);
        deleteButton = (Button) findViewById(R.id.delete_menu);

    }

    private void setMemoListView() {
        List<Memo> memoList = new Select().from(Memo.class).execute();
        Collections.sort(memoList, new Memo.DateTimeComparator());
        mMemoAdapter = new MemoAdapter(this, R.layout.memo_adapter, memoList);
        mMemoListView.setAdapter(mMemoAdapter);
        mMemoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMemo = mMemoAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
                intent.putExtra("date", mMemo.date);
                startActivity(intent);
            }
        });
        mMemoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Position: " + view.getClass().getSimpleName());

                mMemo = mMemoAdapter.getItem(position);
                mMemoAdapter.changeSelect(mMemo);
                setToolbar();
                return true;
            }
        });
    }

    private void setToolbar(){
        int count = mMemoAdapter.getSelectCount();
        mainToolbar.setBackgroundColor(
                getResources()
                        .getColor(
                                count > 0 ?
                                        R.color.toolbar_pressed : R.color.primary
                        ));
        Menu menu = mainToolbar.getMenu();
        MenuItem menuDelete = menu.getItem(0);
        MenuItem menuSearch = menu.getItem(1);
        if (count > 0) {
            menuDelete.setVisible(true);
            menuSearch.setVisible(false);
        } else {
            menuDelete.setVisible(false);
            menuSearch.setVisible(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setMemoListView();
    }

    public void addMemo(View v) {
        Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
        startActivity(intent);
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
        if (id == R.id.search_menu) {
            Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.delete_menu) {
            mMemoAdapter.deleteAll();
            setToolbar();
        }

        return super.onOptionsItemSelected(item);
    }
}
