package com.shohei.put_on.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.melnykov.fab.FloatingActionButton;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.service.LayerService;
import com.shohei.put_on.controller.utils.DebugUtil;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.Adapter.MemoAdapter;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private Memo mMemo;
    private MemoAdapter mMemoAdapter;
    private ServiceRunningDetector mServiceRunningDetector;

    private ListView mMemoListView;
    private Toolbar mMainToolbar;

    private int mSelectedMemoCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainToolbar = (Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setElevation(1);
        setNormalToolBar();

        mMemoListView = (ListView) findViewById(R.id.memo_ListView);
        mMemoListView.setEmptyView(findViewById(R.id.listEmpty_TextView));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create_FAB);
        fab.attachToListView(mMemoListView);

        mMemo = new Memo();
        mServiceRunningDetector = new ServiceRunningDetector(this);

        setMemoListView();
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
                if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Position: " + view.getClass().getSimpleName());

                mMemo = mMemoAdapter.getItem(position);
                mMemoAdapter.changeSelect(mMemo);
                changeToolBar();
                return true;
            }
        });
    }

    //Toolbarの色とアイコンの変更
    private void changeToolBar() {
        mSelectedMemoCount = mMemoAdapter.getSelectCount();
        Menu menu = mMainToolbar.getMenu();
        MenuItem menuDelete = menu.getItem(0);
        if (mSelectedMemoCount > 0) {
            menuDelete.setVisible(true);
            setSelectToolBar(menuDelete);
        } else {
            menuDelete.setVisible(false);
            setNormalToolBar();
        }
    }

    private void setNormalToolBar() {
        mMainToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        mMainToolbar.setNavigationIcon(R.mipmap.ic_launcher);
        mMainToolbar.setTitle(R.string.app_name);
        //アイコンをタップでListViewの一番上に
        mMainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMemoListView.setSelection(0);
            }
        });
    }

    private void setSelectToolBar(final MenuItem menuItem) {
        mMainToolbar.setBackgroundColor(getResources().getColor(R.color.accent_red));
        mMainToolbar.setTitle(String.valueOf(mSelectedMemoCount));
        mMainToolbar.setNavigationIcon(R.mipmap.ic_arrow_back);
        mMainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedMemoCount = 0;
                mMemoAdapter.changeSelect(mMemo);
                menuItem.setVisible(false);
                setNormalToolBar();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setMemoListView();
        setNormalToolBar();
    }

    public void createMemo(View v) {
        if (!mServiceRunningDetector.isServiceRunning()) {
            if (DebugUtil.DEBUG)
                Log.d(LOG_TAG, "ServiceRunning" + mServiceRunningDetector.isServiceRunning());
            startService(new Intent(MainActivity.this, LayerService.class));
            finish();
        }
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

        if (id == R.id.delete_menu) {
            mMemoAdapter.deleteAll();
            changeToolBar();
        }
        return super.onOptionsItemSelected(item);
    }
}