package com.nakanakashoshosho1218.memo_sample;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by nakayamashohei on 15/08/03.
 */
@Table(name = "memo_table")
public class Memo extends Model{
    @Column(name = "title")
    public String title;

    @Column(name = "memo")
    public String memo;

    @Column(name = "date")
    public String date;

    @Override
    public String toString(){
        return title;
    }
}
