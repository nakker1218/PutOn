package me.nakker.put_on.model;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by nakayamashohei on 15/08/03.
 */
@Table(name = "memo_table")
public class Memo extends Model implements Comparable<Memo> {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Column(name = "memo")
    public String memo;

    @Column(name = "tag")
    public String tag;

    @Column(name = "date")
    public String date;

    @Override
    public String toString() {
        return tag;
    }

    public Date getUpdateTime() {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveMemo(String memo, String tag) {
        if (TextUtils.isEmpty(memo)) return;
        this.tag = TextUtils.isEmpty(tag) ? "" : tag;
        this.memo = memo;
        Date date = new Date(System.currentTimeMillis());
        this.date = Memo.DATE_FORMAT.format(date);
        this.save();
    }

    public List<Memo> searchMemo(){
        return new Select().from(Memo.class).execute();
    }

    //Listのソート
    public static class DateTimeComparator implements Comparator<Memo> {
        @Override
        public int compare(Memo memo1, Memo memo2) {
            return (int) (memo2.getUpdateTime().getTime() - memo1.getUpdateTime().getTime());
        }

    }

    @Override
    public int compareTo(Memo another) {
        if (this.getUpdateTime().after(another.getUpdateTime())) {
            return 1;
        } else if (this.getUpdateTime().before(another.getUpdateTime())) {
            return -1;
        } else {
            return 0;
        }
    }

}