package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Model.HumidityVO;
import com.whc.cvccmeasuresystem.Model.Solution;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class HumidityDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "Humidity";


    public HumidityDB(SQLiteOpenHelper sq) {
        this.db = sq.getWritableDatabase();
    }




    public List<HumidityVO> getAll() {
        String sql = "SELECT * FROM Humidity order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<HumidityVO> humidityVOS = new ArrayList<>();
        HumidityVO humidityVO;
        while (cursor.moveToNext()) {
            humidityVO = new HumidityVO();
            humidityVO.setId(cursor.getInt(0));
            humidityVO.setName(cursor.getString(1));
            humidityVO.setBeginTime(new Timestamp(cursor.getLong(2)));
            humidityVO.setBaseVoltage(cursor.getInt(3));
            humidityVO.setOverVoltage(cursor.getInt(4));
            humidityVO.setLight(Boolean.valueOf(cursor.getString(5)));
            humidityVO.setFileID(cursor.getInt(6));
            humidityVOS.add(humidityVO);
        }
        cursor.close();
        return humidityVOS;
    }


    public HumidityVO getById(int id) {
        String sql = "SELECT * FROM Humidity where id  = '"+id+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        HumidityVO humidityVO=null;
        if (cursor.moveToNext()) {
            humidityVO = new HumidityVO();
            humidityVO.setId(cursor.getInt(0));
            humidityVO.setName(cursor.getString(1));
            humidityVO.setBeginTime(new Timestamp(cursor.getLong(2)));
            humidityVO.setBaseVoltage(cursor.getInt(3));
            humidityVO.setOverVoltage(cursor.getInt(4));
            humidityVO.setLight(Boolean.valueOf(cursor.getString(5)));
            humidityVO.setFileID(cursor.getInt(6));
        }
        cursor.close();
        return humidityVO;
    }


    public long insert(HumidityVO humidityVO) {
        ContentValues values = new ContentValues();
        values.put("name", humidityVO.getName());
        values.put("beginTime", humidityVO.getBeginTime().getTime());
        values.put("baseVoltage", humidityVO.getBaseVoltage());
        values.put("overVoltage", humidityVO.getOverVoltage());
        values.put("isLight", String.valueOf(humidityVO.isLight()));
        values.put("fileID", humidityVO.getFileID());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(HumidityVO humidityVO) {
        ContentValues values = new ContentValues();
        values.put("name", humidityVO.getName());
        values.put("beginTime", humidityVO.getBeginTime().getTime());
        values.put("baseVoltage", humidityVO.getBaseVoltage());
        values.put("overVoltage", humidityVO.getOverVoltage());
        values.put("isLight", String.valueOf(humidityVO.isLight()));
        values.put("fileID", humidityVO.getFileID());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(humidityVO.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
