package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Model.HumidityVoltage;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.Model.User;

import java.util.ArrayList;
import java.util.List;


public class HumidityVoltageDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="HumidityVoltage";


    public HumidityVoltageDB(SQLiteOpenHelper sq)
    {
        this.db=sq.getWritableDatabase();
    }

    public List<HumidityVoltage> getAll() {
        String sql = "SELECT * FROM HumidityVoltage order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<HumidityVoltage> humidityVoltages = new ArrayList<>();
        HumidityVoltage humidityVoltage;
        while (cursor.moveToNext()) {
            humidityVoltage=new HumidityVoltage();
            humidityVoltage.setId(cursor.getLong(0));
            humidityVoltage.setVoltage1(cursor.getInt(1));
            humidityVoltage.setVoltage2(cursor.getInt(2));
            humidityVoltage.setVoltage3(cursor.getInt(3));
            humidityVoltage.setVoltage4(cursor.getInt(4));
            humidityVoltage.setFileId(cursor.getInt(5));
            humidityVoltages.add(humidityVoltage);
        }
        cursor.close();
        return humidityVoltages;
    }



    public  List<HumidityVoltage> findUserByHuId(int  fileID) {
        String sql = "SELECT * FROM HumidityVoltage where fileID = '"+fileID+"' order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<HumidityVoltage> humidityVoltages = new ArrayList<>();
        HumidityVoltage humidityVoltage;
        while (cursor.moveToNext()) {
            humidityVoltage=new HumidityVoltage();
            humidityVoltage.setId(cursor.getLong(0));
            humidityVoltage.setVoltage1(cursor.getInt(1));
            humidityVoltage.setVoltage2(cursor.getInt(2));
            humidityVoltage.setVoltage3(cursor.getInt(3));
            humidityVoltage.setVoltage4(cursor.getInt(4));
            humidityVoltage.setFileId(cursor.getInt(5));
            humidityVoltages.add(humidityVoltage);
        }
        cursor.close();
        return humidityVoltages;
    }

    public long insert(HumidityVoltage humidityVoltage) {
        ContentValues values = new ContentValues();
        values.put("Voltage1", humidityVoltage.getVoltage1());
        values.put("Voltage2", humidityVoltage.getVoltage2());
        values.put("Voltage3", humidityVoltage.getVoltage3());
        values.put("Voltage4", humidityVoltage.getVoltage4());
        values.put("fileID", humidityVoltage.getFileId());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(HumidityVoltage humidityVoltage) {
        ContentValues values = new ContentValues();
        values.put("Voltage1", humidityVoltage.getVoltage1());
        values.put("Voltage2", humidityVoltage.getVoltage2());
        values.put("Voltage3", humidityVoltage.getVoltage3());
        values.put("Voltage4", humidityVoltage.getVoltage4());
        values.put("fileID", humidityVoltage.getFileId());
        String whereClause = "id = ?;";
        String[] whereArgs = {Long.toString(humidityVoltage.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
