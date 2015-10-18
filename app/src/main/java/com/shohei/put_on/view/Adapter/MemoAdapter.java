package com.shohei.put_on.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.shohei.put_on.R;
import com.shohei.put_on.controller.utils.Logger;
import com.shohei.put_on.controller.utils.MemoFilter;
import com.shohei.put_on.model.Memo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nakayamashohei on 15/08/03.
 */
public class MemoAdapter extends ArrayAdapter<Memo> {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    private int mSelectedColor;
    private Filter mFilter;
    private LayoutInflater mLayoutInflater;
    private Set<Memo> mSelectedSet;
    private int mResourceId;


    public MemoAdapter(Context context, int resource, List<Memo> objects) {
        super(context, resource, objects);

        mResourceId = resource;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedSet = new HashSet<>();
        mFilter = new MemoFilter(this, objects);
        mSelectedColor = getContext().getResources().getColor(R.color.list_pressed);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tagTextView = (TextView) convertView.findViewById(R.id.tag_TextView);
            viewHolder.memoTextView = (TextView) convertView.findViewById(R.id.memo_TextView);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.date_TextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Memo memo = getItem(position);
        if (memo.tag != null) {
            viewHolder.tagTextView.setText(memo.tag);
        } else {
            viewHolder.tagTextView.setVisibility(View.GONE);
        }
        viewHolder.memoTextView.setText(memo.memo);
        viewHolder.dateTextView.setText(memo.date);
        if (mSelectedSet.contains(memo)) {
            convertView.setBackgroundColor(mSelectedColor);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    public int getSelectCount() {
        return mSelectedSet.size();
    }

    public void changeSelect(Memo memo) {
        Logger.d(LOG_TAG, "containsKey: " + mSelectedSet.contains(memo));

        if (mSelectedSet.contains(memo)) {
            mSelectedSet.remove(memo);
        } else {
            mSelectedSet.add(memo);
        }
        notifyDataSetChanged();
    }

    public void deleteAll() {
        for (Memo memo : mSelectedSet) {
            remove(memo);
        }
        mSelectedSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ViewHolder {
        TextView memoTextView;
        TextView tagTextView;
        TextView dateTextView;
    }
}