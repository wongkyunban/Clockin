package com.wong.clockin.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {
    private static final String DATABASE_NAME = "datastorage";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "clockin";
    private static final String[] COLUMNS = { "_id", "amount", "clockInTime", "title","isClockIn" };
    private DBOpenHelper helper;
    private SQLiteDatabase db;


    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " + COLUMNS[0] + " integer primary key autoincrement, " + COLUMNS[1]
                + " integer, " + COLUMNS[2] + " text, " + COLUMNS[3] + " text,"+COLUMNS[4]+" integer);";

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }

    public DBHelper(Context context) {
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insert(DataBean data) {
        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], data.getAmount());
        values.put(COLUMNS[2], data.getClockInTime());
        values.put(COLUMNS[3], data.getTitle());
        values.put(COLUMNS[4],data.getIsClockIn());
        long rs = db.insert(TABLE_NAME, null, values);
        return rs;
    }
    public void update(DataBean data){
        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], data.getAmount());
        values.put(COLUMNS[2], data.getClockInTime());
        values.put(COLUMNS[3], data.getTitle());
        values.put(COLUMNS[4],data.getIsClockIn());
        String arg[] = new String[1];
        arg[0] = String.valueOf(data.getId());
        db.update(TABLE_NAME,values,"_id=?",arg);
    }
    public void delete(DataBean data){

        String arg[] = new String[1];
        arg[0] = String.valueOf(data.getId());
        db.delete(TABLE_NAME,"_id=",arg);
    }
    public List<DataBean> queryAll() {
        List<DataBean> result = new ArrayList<DataBean>();
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        while (cursor.moveToNext()) {
            DataBean databean = new DataBean();
            databean.setId(cursor.getInt(0));
            databean.setAmount(cursor.getInt(1));
            databean.setClockinTime(cursor.getString(2));
            databean.setTitle(cursor.getString(3));
            databean.setIsClockIn(cursor.getInt(4));
            result.add(databean);

        }
        return result;
    }

}
