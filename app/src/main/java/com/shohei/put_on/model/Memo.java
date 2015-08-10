package com.shohei.put_on.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by nakayamashohei on 15/08/03.
 */
@Table(name = "memo_table")
public class Memo extends Model {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Column(name = "title")
    public String title;

    @Column(name = "memo")
    public String memo;

    @Column(name = "date")
    public String date;

    @Override
    public String toString() {
        return title;
    }

    public Date getUpdateTime() {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Listのソート
    public static class DateTimeComparator implements Comparator<Memo> {

        @Override
        public int compare(Memo memo1, Memo memo2) {
            return (int) (memo2.getUpdateTime().getTime() - memo1.getUpdateTime().getTime());
        }
    }
}
