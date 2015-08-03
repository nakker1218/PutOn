package com.nakanakashoshosho1218.memo_sample;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nakayamashohei on 15/08/03.
 */
public class MemoAdapter extends ArrayAdapter<Memo> {
    private final static String LOG_TAG = MemoAdapter.class.getSimpleName();

    int resourceId;
    LayoutInflater inflater;

    public MemoAdapter(Context context, int resource, List<Memo> objects) {
        super(context, resource, objects);

        this.resourceId = resource;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.title_TextView);
            viewHolder.memoTextView = (TextView)convertView.findViewById(R.id.memo_TextView);
            viewHolder.dateTextView = (TextView)convertView.findViewById(R.id.date_TextView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Memo memo = getItem(position);
        viewHolder.titleTextView.setText(memo.title);
        viewHolder.memoTextView.setText(memo.memo);
        viewHolder.dateTextView.setText(memo.date);

        return convertView;
    }

    private class ViewHolder{
        TextView titleTextView;
        TextView memoTextView;
        TextView dateTextView;
    }
}
