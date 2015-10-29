package com.shohei.put_on.view.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.shohei.put_on.R;
import com.shohei.put_on.controller.service.LayerService;
import com.shohei.put_on.controller.utils.Logger;
import com.shohei.put_on.controller.utils.ServiceRunningDetector;
import com.shohei.put_on.model.Memo;
import com.shohei.put_on.view.adapter.MemoAdapter;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Memo mMemo;
    private MemoAdapter mMemoAdapter;
    private ServiceRunningDetector mServiceRunningDetector;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mMainToolbar;
    private FloatingActionButton mFab;
    private ListView mMemoListView;
    private Snackbar mSnackBar;
    private SearchView mSearchView;

    private int mSelectedMemoCount = 0;
    private int mCurrentColor;

    private boolean mIsSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_Layout);

        mMainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mMainToolbar);

        mCurrentColor = ContextCompat.getColor(this, R.color.primary);
        changeStateNormal();

        mMemoListView = (ListView) findViewById(R.id.memo_ListView);
        mMemoListView.setEmptyView(findViewById(R.id.listEmpty_TextView));

        mSharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick();
            }
        });

        mMemo = new Memo();
        mServiceRunningDetector = new ServiceRunningDetector(this);

        setMemoListView();

        if (mSharedPreferences.getBoolean("first", false) == false) {
            mMemo.saveMemo(getString(R.string.hint_memo), getString(R.string.hint_tag));

            mEditor.putBoolean("first", true);
            mEditor.commit();
        }
    }

    private void fabClick() {
        if (!mServiceRunningDetector.isServiceRunning()) {
            Logger.d(this.getClass(), "ServiceRunning" + mServiceRunningDetector.isServiceRunning());
            startService(new Intent(MainActivity.this, LayerService.class));
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.cancel(LayerService.NOTIFICATION_ID);
            finish();

        }
    }

    public void setMemoListView() {
        List<Memo> memoList = mMemo.searchMemo();
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
                Logger.d(this.getClass(), "Position: " + view.getClass().getSimpleName());

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
            changeStateSelect();
        } else {
            setToolbarIcon(false);
            changeStateNormal();
            setMemoListView();
        }
    }

    private void changeStateNormal() {
        setToolbar(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           mMemoListView.setSelection(0);
                       }
                   }
        );
        changeViewColor(mMainToolbar,
                mCurrentColor,
                mCurrentColor = ContextCompat.getColor(this, R.color.primary));
        if (mIsSelected) {
            mSnackBar.dismiss();
            mIsSelected = !mIsSelected;
        }
    }

    private void changeStateSelect() {
        setToolbar(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           mSelectedMemoCount = 0;
                           mMemoAdapter.changeSelect(mMemo);
                           setToolbarIcon(false);
                           changeStateNormal();
                           setMemoListView();
                       }
                   }
        );
        changeViewColor(mMainToolbar,
                mCurrentColor,
                mCurrentColor = ContextCompat.getColor(this, R.color.accent_red));
        setSnackBar();
    }

    private void setToolbarIcon(boolean isSelect) {
        Menu menu = mMainToolbar.getMenu();
        MenuItem menuDelete = menu.getItem(0);
        MenuItem menuSearch = menu.getItem(1);
        menuDelete.setVisible(isSelect);
        menuSearch.setVisible(!isSelect);
    }

    private void setToolbar(View.OnClickListener listener) {
        mMainToolbar.setTitle(getResources().getString(R.string.app_name));
        mMainToolbar.setNavigationIcon(R.mipmap.ic_puton);
        mMainToolbar.setNavigationOnClickListener(listener);
    }

    private void setSnackBar() {
        if (mIsSelected) {
            mSnackBar.setText(String.valueOf(mSelectedMemoCount) + "SELECT");
        } else {
            mSnackBar = Snackbar.make(mCoordinatorLayout,
                    String.valueOf(mSelectedMemoCount) + "SELECT",
                    Snackbar.LENGTH_INDEFINITE);
            mSnackBar.show();
            mIsSelected = !mIsSelected;
        }
        mSnackBar.setAction("CLEAR", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackBar.dismiss();
                mSelectedMemoCount = 0;
                mMemoAdapter.changeSelect(mMemo);
                setToolbarIcon(false);
                changeStateNormal();
                setMemoListView();
            }
        });
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
        changeStateNormal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.search_menu).getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getResources().getString(R.string.title_menu_main_search));

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
        Logger.d(this.getClass(), "onQueryTextSubmit" + query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Logger.d(this.getClass(), "onQueryTextChange" + newText);
        mMemoAdapter.getFilter().filter(newText);
        return true;
    }
}