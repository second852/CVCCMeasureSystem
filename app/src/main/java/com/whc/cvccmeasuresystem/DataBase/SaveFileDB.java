package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Model.SaveFile;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class SaveFileDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="SaveFile";


    public SaveFileDB(SQLiteOpenHelper sq)
    {
        this.db=sq.getReadableDatabase();
    }

    public List<SaveFile> getAll() {
        String sql = "SELECT * FROM SaveFile order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<SaveFile> saveFiles = new ArrayList<>();
        SaveFile saveFile;
        while (cursor.moveToNext()) {
            saveFile=new SaveFile();
            saveFile.setID(cursor.getInt(0));
            saveFile.setName(cursor.getString(1));
            saveFile.setMeasureType(cursor.getString(2));
            saveFile.setStatTime(new Timestamp(cursor.getLong(3)));
            saveFile.setEndTime(new Timestamp(cursor.getLong(4)));
            saveFile.setUserId(cursor.getInt(5));
            saveFiles.add(saveFile);
        }
        cursor.close();
        return saveFiles;
    }

    public SaveFile findOldSaveFile(String name) {
        String sql = "SELECT * FROM SaveFile where name = '"+name+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        SaveFile saveFile=null;
        if (cursor.moveToNext()) {
            saveFile=new SaveFile();
            saveFile.setID(cursor.getInt(0));
            saveFile.setName(cursor.getString(1));
            saveFile.setMeasureType(cursor.getString(2));
            saveFile.setStatTime(new Timestamp(cursor.getLong(3)));
            saveFile.setEndTime(new Timestamp(cursor.getLong(4)));
            saveFile.setUserId(cursor.getInt(5));
        }
        cursor.close();
        return saveFile;
    }

    public long insert(SaveFile saveFile) {
        ContentValues values = new ContentValues();
        values.put("name", saveFile.getName());
        values.put("measureType", saveFile.getMeasureType());
        values.put("startTime", saveFile.getStatTime().getTime());
        values.put("endTime", saveFile.getEndTime().getTime());
        values.put("userId", saveFile.getUserId());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(SaveFile saveFile) {
        ContentValues values = new ContentValues();
        values.put("id", saveFile.getID());
        values.put("name", saveFile.getName());
        values.put("measureType", saveFile.getMeasureType());
        values.put("startTime", saveFile.getStatTime().getTime());
        values.put("endTime", saveFile.getEndTime().getTime());
        values.put("userId", saveFile.getUserId());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(saveFile.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }


}
