package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.samples;


public class SampleDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Sample";


    public SampleDB(SQLiteOpenHelper sq)
    {
        this.db=sq.getWritableDatabase();
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
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
            samples.add(sample);
        }
        cursor.close();
        return samples;
    }


    public List<Sample> getFileSamepleAll(int fileID) {
        String sql = "SELECT * FROM Sample where fileID = '"+fileID+"' order by id desc;";
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
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
            samples.add(sample);
        }
        cursor.close();
        return samples;
    }

    public Sample findOldSample(String name,Integer fileID,String location) {
        String sql = "SELECT * FROM Sample where name = '"+name+"' and  fileID = '"+fileID+"' and location = '"+location+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Sample sample=null;
        if (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
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
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
        }
        cursor.close();
        return sample;
    }


    public HashMap<Sample, List<Solution>> setMapSampeSolution(Integer fileID) {
        SolutionDB solutionDB=new SolutionDB(db);
        HashMap<Sample, List<Solution>> dataMap=new HashMap<>();
        String sql = "SELECT * FROM Sample where fileID = '"+fileID+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Sample sample;
        List<Sample> samples=new ArrayList<>();
        Common.samples=new ArrayList<>();
        while (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
            List<Solution> solutions=solutionDB.getSampleAll(sample.getID());
            dataMap.put(sample,solutions);
            samples.add(sample);
            Common.samples.add(sample);
        }
        Common.sample1=samples.get(0);
        Common.sample2=samples.get(1);
        Common.sample3=samples.get(2);
        Common.sample4=samples.get(3);
        cursor.close();
        return dataMap;
    }


    public HashMap<Sample, List<Solution>> setMapSampeSolutionToIC(Integer fileID,String measureType) {
        SolutionDB solutionDB=new SolutionDB(db);
        HashMap<Sample, List<Solution>> dataMap=new HashMap<>();
        String sql = "SELECT * FROM Sample where fileID = '"+fileID+"';";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        Sample sample;
        List<Sample> samples=new ArrayList<>();
        while (cursor.moveToNext()) {
            sample=new Sample();
            sample.setID(cursor.getInt(0));
            sample.setName(cursor.getString(1));
            sample.setIonType(cursor.getString(2));
            sample.setFileID(cursor.getInt(3));
            sample.setLocation(cursor.getString(4));
            sample.setLimitHighVoltage(cursor.getString(5));
            sample.setLimitLowVoltage(cursor.getString(6));
            sample.setSlope(cursor.getString(7));
            sample.setUnit(cursor.getString(8));
            sample.setIntercept(cursor.getString(9));
            sample.setStandardDeviation(cursor.getString(10));
            sample.setDifferenceX(cursor.getString(11));
            sample.setDifferenceY(cursor.getString(12));
            sample.setR(cursor.getString(13));
            List<Solution> solutions=solutionDB.getSampleAByIdByMeasureType(sample.getID(),measureType);
            dataMap.put(sample,solutions);
            samples.add(sample);
        }
        Common.sample1=samples.get(0);
        Common.sample2=samples.get(1);
        Common.sample3=samples.get(2);
        Common.sample4=samples.get(3);
        cursor.close();
        return dataMap;
    }





    public long insert(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("name", sample.getName());
        values.put("ionType", sample.getIonType());
        values.put("fileID", sample.getFileID());
        values.put("location", sample.getLocation());
        values.put("limitHighVoltage", sample.getLimitHighVoltage());
        values.put("limitLowVoltage", sample.getLimitLowVoltage());
        values.put("slope", sample.getSlope());
        values.put("unit", sample.getUnit());
        values.put("intercept", sample.getIntercept());
        values.put("standardDeviation", sample.getStandardDeviation());
        values.put("differenceX", sample.getDifferenceX());
        values.put("differenceY", sample.getDifferenceY());
        values.put("R", sample.getR());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("id", sample.getID());
        values.put("name", sample.getName());
        values.put("ionType", sample.getIonType());
        values.put("fileID", sample.getFileID());
        values.put("location", sample.getLocation());
        values.put("limitHighVoltage", sample.getLimitHighVoltage());
        values.put("limitLowVoltage", sample.getLimitLowVoltage());
        values.put("slope", sample.getSlope());
        values.put("unit", sample.getUnit());
        values.put("intercept", sample.getIntercept());
        values.put("standardDeviation", sample.getStandardDeviation());
        values.put("differenceX", sample.getDifferenceX());
        values.put("differenceY", sample.getDifferenceY());
        values.put("R", sample.getR());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(sample.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
