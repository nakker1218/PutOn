package com.nakanakashoshosho1218.memo_sample.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nakanakashoshosho1218.memo_sample.R;
import com.nakanakashoshosho1218.memo_sample.model.Memo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nakayamashohei on 15/08/03.
 */
public class MemoAdapter extends ArrayAdapter<Memo> {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    int resourceId;
    LayoutInflater inflater;
    private Map<Memo, Boolean> mSelectMap;

    public MemoAdapter(Context context, int resource, List<Memo> objects) {
        super(context, resource, objects);

        this.resourceId = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectMap = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_TextView);
            viewHolder.memoTextView = (TextView) convertView.findViewById(R.id.memo_TextView);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.date_TextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Memo memo = getItem(position);
        viewHolder.titleTextView.setText(memo.title);
        viewHolder.memoTextView.setText(memo.memo);
        viewHolder.dateTextView.setText(memo.date);
        if (mSelectMap.containsKey(memo) && mSelectMap.get(memo)) {
            convertView.setBackgroundColor(Color.GRAY);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    public void changeSelect(Memo memo) {
        if (mSelectMap.containsKey(memo)) {
            mSelectMap.put(memo, !mSelectMap.get(memo));
        } else {
            mSelectMap.put(memo, true);
        }
        notifyDataSetChanged();
    }

    public void deleteAll(){
        Set<Memo> memos = mSelectMap.keySet();
        for (Memo memo : memos) {
            if (mSelectMap.get(memo)){
                remove(memo);
                memo.delete();
            }
        }
        mSelectMap.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView titleTextView;
        TextView memoTextView;
        TextView dateTextView;
    }
}
