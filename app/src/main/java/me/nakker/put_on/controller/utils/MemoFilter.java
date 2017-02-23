package me.nakker.put_on.controller.utils;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import me.nakker.put_on.model.Memo;

/**
 * Created by nakayamashohei on 15/10/09.
 */
public class MemoFilter extends Filter {
    private ArrayAdapter<Memo> mAdapter;
    private SortedSet<Memo> mDataSet;

    public MemoFilter(@NonNull ArrayAdapter<Memo> adapter, Collection<Memo> data) {
        mAdapter = adapter;
        mDataSet = new TreeSet<>();
        mDataSet.addAll(data);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        // クエリーからフィルターして(filterItemsに)データ突っ込む
        FilterResults results = new FilterResults();
        SortedSet<Memo> filteredItems = new TreeSet<>();

        String character = getString(constraint);

        StringBuilder builder = new StringBuilder();
        builder.append(constraint);
        builder.append("|");
        builder.append(character);
        Pattern pattern = Pattern.compile(builder.toString());

        for (Memo memo : mDataSet) {
            if (pattern.matcher(memo.memo).find() || pattern.matcher(memo.tag).find()) {
                filteredItems.add(memo);
            }
        }

        results.values = filteredItems;
        results.count = filteredItems.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Set<Memo> filters = (SortedSet<Memo>) results.values;
        mAdapter.notifyDataSetChanged();
        // 一度データ削除する
        mAdapter.clear();
        // フィルターしたデータを突っ込む
        mAdapter.addAll(filters);
    }

    private String getString(CharSequence charSequence) {
        StringBuffer stringBuffer = new StringBuffer(charSequence);
        for (int i = 0; i < stringBuffer.length(); i++) {
            char c = stringBuffer.charAt(i);
            if (c >= 'ぁ' && c <= 'ん') {
                stringBuffer.setCharAt(i, (char) (c - 'ぁ' + 'ァ'));
            } else if (c >= 'a' && c <= 'z') {
                stringBuffer.setCharAt(i, (char) (c - 'a' + 'A'));
            } else if (c >= 'A' && c <= 'Z') {
                stringBuffer.setCharAt(i, (char) (c - 'A' + 'a'));
            }
        }
        return stringBuffer.toString();
    }
}
