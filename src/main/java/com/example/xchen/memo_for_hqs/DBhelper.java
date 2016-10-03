package com.example.xchen.memo_for_hqs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xchen on 2016/6/10.
 */
public class DBhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "hqs_memo.db"; // 数据库名称
    private static final int version = 1; // 数据库版本

    public DBhelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists memo(name char(30), id char(30), password char(30));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

}