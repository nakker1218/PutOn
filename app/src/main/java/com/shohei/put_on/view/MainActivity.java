package com.shohei.put_on.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.activeandroid.query.Select;
import com.melnykov.fab.FloatingActionButton;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.MemoAdapter;
import com.shohei.put_on.model.Memo;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    private ListView mMemoListView;
    private Toolbar mMainToolbar;
    private SearchView mSearchView;

    private MemoAdapter mMemoAdapter;
    private Memo mMemo;

    private enum DayOfTheWeek{
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainToolbar = (Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setElevation(8);

        //ToolBarにアイコンを表示、アイコンをタップでListViewの一番上に
//        mMainToolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        mMainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMemoListView.setSelection(0);
//            }
//        });

        mMemoListView = (ListView) findViewById(R.id.memo_ListView);
        mMemoListView.setEmptyView(findViewById(R.id.listEmpty_TextView));

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.create_FloatingButton);
        floatingActionButton.attachToListView(mMemoListView);
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

    //Toolbarの色とアイコンの変更
    private void setToolbar(){
        int count = mMemoAdapter.getSelectCount();
        mMainToolbar.setBackgroundColor(
                getResources()
                        .getColor(
                                count > 0 ?
                                        R.color.toolbar_pressed : R.color.primary
                        ));
        Menu menu = mMainToolbar.getMenu();
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

    //曜日を取得
    private static int getDayOfTheWeek(){
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY: return DayOfTheWeek.SUNDAY.ordinal();
            case Calendar.MONDAY: return DayOfTheWeek.MONDAY.ordinal();
            case Calendar.TUESDAY: return DayOfTheWeek.TUESDAY.ordinal();
            case Calendar.WEDNESDAY: return DayOfTheWeek.WEDNESDAY.ordinal();
            case Calendar.THURSDAY: return DayOfTheWeek.THURSDAY.ordinal();
            case Calendar.FRIDAY: return DayOfTheWeek.FRIDAY.ordinal();
            case Calendar.SATURDAY: return DayOfTheWeek.SATURDAY.ordinal();
        }
        throw new IllegalStateException();
    }

    @Override
    public void onResume() {
        super.onResume();
        setMemoListView();
    }

    //FloatingActionButtonが押された時の処理
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
            mSearchView = (SearchView) MenuItemCompat.getActionView(item);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (id == R.id.delete_menu) {
            mMemoAdapter.deleteAll();
            setToolbar();
        }

        return super.onOptionsItemSelected(item);
    }
}
