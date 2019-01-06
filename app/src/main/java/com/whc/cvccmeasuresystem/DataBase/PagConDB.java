package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Solution;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class PagConDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "PageCon";


    public PagConDB(SQLiteOpenHelper sq) {
        this.db = sq.getWritableDatabase();
    }

    public PagConDB(SQLiteDatabase sq) {
        this.db = sq;
    }


    public List<PageCon> getAll() {
        String sql = "SELECT * FROM PageCon order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PageCon> pageCons = new ArrayList<>();
        PageCon pageCon;
        while (cursor.moveToNext()) {
           pageCon=new PageCon();
           pageCon.setId(cursor.getInt(0));
           pageCon.setCon1(cursor.getString(1));
           pageCon.setCon2(cursor.getString(2));
           pageCon.setCon3(cursor.getString(3));
           pageCon.setCon4(cursor.getString(4));
           pageCon.setExpTime(cursor.getString(5));
           pageCon.setStep(cursor.getString(6));
           pageCon.setFileId(cursor.getInt(7));
        }
        cursor.close();
        return pageCons;
    }


    public List<PageCon> getStepPagCon(String step,Integer fileId) {
        String sql = "SELECT * FROM PageCon where step = '"+step+"' and fileId = '"+fileId+"' order by id ;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<PageCon> pageCons = new ArrayList<>();
        PageCon pageCon;
        while (cursor.moveToNext()) {
            pageCon=new PageCon();
            pageCon.setId(cursor.getInt(0));
            pageCon.setCon1(cursor.getString(1));
            pageCon.setCon2(cursor.getString(2));
            pageCon.setCon3(cursor.getString(3));
            pageCon.setCon4(cursor.getString(4));
            pageCon.setExpTime(cursor.getString(5));
            pageCon.setStep(cursor.getString(6));
            pageCon.setFileId(cursor.getInt(7));
        }
        cursor.close();
        return pageCons;
    }






    public long insert(PageCon pageCon) {
        ContentValues values = new ContentValues();
        values.put("con1", pageCon.getCon1());
        values.put("con2", pageCon.getCon2());
        values.put("con3", pageCon.getCon3());
        values.put("con4", pageCon.getCon4());
        values.put("expTime", pageCon.getExpTime());
        values.put("step", pageCon.getStep());
        values.put("fileId", pageCon.getFileId());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(PageCon pageCon) {
        ContentValues values = new ContentValues();
        values.put("id", pageCon.getId());
        values.put("con1", pageCon.getCon1());
        values.put("con2", pageCon.getCon2());
        values.put("con3", pageCon.getCon3());
        values.put("con4", pageCon.getCon4());
        values.put("expTime", pageCon.getExpTime());
        values.put("step", pageCon.getStep());
        values.put("fileId", pageCon.getFileId());
        String whereClause = "step = ? and fileId = ?;";
        String[] whereArgs = {pageCon.getStep(),Integer.toString(pageCon.getFileId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
