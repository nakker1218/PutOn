package com.shohei.put_on.view.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.activeandroid.query.Select;
import com.melnykov.fab.FloatingActionButton;
import com.shohei.put_on.R;
import com.shohei.put_on.controller.service.LayerService;
import com.shohei.put_on.controller.utils.Logger;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.adapter.MemoAdapter;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private Memo mMemo;
    private MemoAdapter mMemoAdapter;
    private ServiceRunningDetector mServiceRunningDetector;

    private Toolbar mMainToolbar;
    private ListView mMemoListView;
    private SearchView mSearchView;

    private int mSelectedMemoCount = 0;
    private int mCurrentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainToolbar = (Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setElevation(1);

        mCurrentColor = getResources().getColor(R.color.primary);
        changeToolBarColorNormal();

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
                if (mSelectedMemoCount > 0) {
                    mMemoAdapter.changeSelect(mMemo);
                    changeToolBar();
                } else {
                    Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
                    intent.putExtra("date", mMemo.date);
                    startActivity(intent);
                }
            }
        });
        mMemoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.d(LOG_TAG, "Position: " + view.getClass().getSimpleName());

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
        if (mSelectedMemoCount > 0) {
            setToolbarIcon(true);
            changeToolBarColorSelect();
        } else {
            setToolbarIcon(false);
            changeToolBarColorNormal();
        }
    }

    private void changeToolBarColorNormal() {
        setToolbar(getResources().getString(R.string.app_name),
                R.mipmap.ic_launcher,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMemoListView.setSelection(0);
                    }
                }
        );
        changeViewColor(mMainToolbar,
                mCurrentColor,
                mCurrentColor = getResources().getColor(R.color.primary));
    }

    private void changeToolBarColorSelect() {
        setToolbar(String.valueOf(mSelectedMemoCount),
                R.mipmap.ic_arrow_back,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedMemoCount = 0;
                        mMemoAdapter.changeSelect(mMemo);
                        setToolbarIcon(false);
                        changeToolBarColorNormal();
                    }
                }
        );
        changeViewColor(mMainToolbar,
                mCurrentColor,
                mCurrentColor = getResources().getColor(R.color.accent_red));
    }

    private void setToolbarIcon(boolean isSelect) {
        Menu menu = mMainToolbar.getMenu();
        MenuItem menuDelete = menu.getItem(0);
        MenuItem menuSearch = menu.getItem(1);
        menuDelete.setVisible(isSelect);
        menuSearch.setVisible(!isSelect);
    }

    private void setToolbar(String title, int iconId, View.OnClickListener listener) {
        mMainToolbar.setTitle(title);
        mMainToolbar.setNavigationIcon(iconId);
        mMainToolbar.setNavigationOnClickListener(listener);
    }

    private void changeViewColor(final View view, final int initialColor, final int finalColor) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                view.setBackgroundColor(blended);
            }
        });
        anim.setDuration(250).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onStart() {
        super.onStart();
        setMemoListView();
        changeToolBarColorNormal();
    }

    public void createMemo(View v) {
        if (!mServiceRunningDetector.isServiceRunning()) {
            Logger.d(LOG_TAG, "ServiceRunning" + mServiceRunningDetector.isServiceRunning());
            startService(new Intent(MainActivity.this, LayerService.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.search_menu).getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Search");

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        Logger.d(LOG_TAG, "onQueryTextSubmit" + query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Logger.d(LOG_TAG, "onQueryTextChange" + newText);
        mMemoAdapter.getFilter().filter(newText);
        return true;
    }
}