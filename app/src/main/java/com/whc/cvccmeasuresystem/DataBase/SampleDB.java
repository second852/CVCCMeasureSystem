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
            sample.setType(cursor.getString(1));
            sample.setFileID(cursor.getInt(2));
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
            sample.setType(cursor.getString(1));
            sample.setFileID(cursor.getInt(2));
        }
        cursor.close();
        return sample;
    }


    public long insert(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("id", sample.getID());
        values.put("type", sample.getType());
        values.put("fileID", sample.getFileID());
        values.put("NAME", sample.getName());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("id", sample.getID());
        values.put("type", sample.getType());
        values.put("fileID", sample.getFileID());
        values.put("NAME", sample.getName());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(sample.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
