package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class SampleDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Sample";


    public SampleDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<Sample> getAll() {
        String sql = "SELECT * FROM Sample order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<Sample> samples = new ArrayList<>();
        Sample sample;
        while (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
            samples.add(sample);
        }
        cursor.close();
        return samples;
    }


    public Sample findOldSample(String name,Integer fileID) {
        String sql = "SELECT * FROM Sample where name = '"+name+"' and  fileID = '"+fileID+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Sample sample=null;
        if (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
        }
        cursor.close();
        return sample;
    }

    public Sample findOldSample(Integer ID) {
        String sql = "SELECT * FROM Sample where id = '"+ID+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Sample sample=null;
        if (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
        }
        cursor.close();
        return sample;
    }


    public long insert(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("name", sample.getName());
        values.put("ionType", sample.getIonType());
        values.put("fileID", sample.getFileID());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("id", sample.getID());
        values.put("name", sample.getName());
        values.put("ionType", sample.getIonType());
        values.put("fileID", sample.getFileID());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(sample.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
