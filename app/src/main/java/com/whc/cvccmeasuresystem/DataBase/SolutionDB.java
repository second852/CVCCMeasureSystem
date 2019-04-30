package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Model.Solution;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class SolutionDB {
    private SQLiteDatabase db;
    private String TABLE_NAME = "Solution";


    public SolutionDB(SQLiteOpenHelper sq) {
        this.db = sq.getWritableDatabase();
    }

    public SolutionDB(SQLiteDatabase sq) {
        this.db = sq;
    }


    public List<Solution> getAll() {
        String sql = "SELECT * FROM Solution order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<Solution> solutions = new ArrayList<>();
        Solution solution;
        while (cursor.moveToNext()) {
            solution = new Solution();
            solution.setID(cursor.getInt(0));
            solution.setConcentration(cursor.getString(1));
            solution.setNumber(cursor.getString(2));
            solution.setTime(new Timestamp(cursor.getLong(3)));
            solution.setVoltage(cursor.getInt(4));
            solution.setSampleID(cursor.getInt(5));
            solution.setMeasureType(cursor.getString(6));
            solution.setColor(cursor.getInt(7));
            solutions.add(solution);
        }
        cursor.close();
        return solutions;
    }


    public List<Solution> getSampleAll(int id) {
        String sql = "SELECT * FROM Solution where sampleID = '" + id + "' order by time;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<Solution> solutions = new ArrayList<>();
        Common.choiceColor=new ArrayList<>();
        Solution solution;
        while (cursor.moveToNext()) {
            solution = new Solution();
            solution.setID(cursor.getInt(0));
            solution.setConcentration(cursor.getString(1));
            solution.setNumber(cursor.getString(2));
            solution.setTime(new Timestamp(cursor.getLong(3)));
            solution.setVoltage(cursor.getInt(4));
            solution.setSampleID(cursor.getInt(5));
            solution.setMeasureType(cursor.getString(6));
            solution.setColor(cursor.getInt(7));
            Common.choiceColor.add(solution.getColor());
            solutions.add(solution);
        }
        cursor.close();
        return solutions;
    }

    public Integer getOneSolutionByTimeId(int id,long time) {
        String sql = "SELECT id FROM Solution where sampleID = '" + id + "' and time = '"+time+"' order by time;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        int result=0;
        if (cursor.moveToNext()) {
            result=cursor.getInt(0);
        }
        cursor.close();
        return result;
    }


    public List<Solution> getSampleAByIdByMeasureType(int id,String measureType) {
        String sql = "SELECT * FROM Solution where sampleID = '" + id + "' and measureType = '"+measureType+"' order by id;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<Solution> solutions = new ArrayList<>();
        Solution solution;
        while (cursor.moveToNext()) {
            solution = new Solution();
            solution.setID(cursor.getInt(0));
            solution.setConcentration(cursor.getString(1));
            solution.setNumber(cursor.getString(2));
            solution.setTime(new Timestamp(cursor.getLong(3)));
            solution.setVoltage(cursor.getInt(4));
            solution.setSampleID(cursor.getInt(5));
            solution.setMeasureType(cursor.getString(6));
            solution.setColor(cursor.getInt(7));
            solutions.add(solution);
        }
        cursor.close();
        return solutions;
    }


    public void deleteBySampleId(int id) {
        String sql = "delete FROM Solution where sampleID = '" + id + "';";
        db.execSQL(sql);
    }

    public long insert(Solution solution) {
        ContentValues values = new ContentValues();
        values.put("concentration", solution.getConcentration());
        values.put("number", solution.getNumber());
        values.put("time", solution.getTime().getTime());
        values.put("voltage", solution.getVoltage());
        values.put("sampleID", solution.getSampleID());
        values.put("measureType", solution.getMeasureType());
        values.put("color", solution.getColor());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Solution solution) {
        ContentValues values = new ContentValues();
        values.put("id", solution.getID());
        values.put("concentration", solution.getConcentration());
        values.put("number", solution.getNumber());
        values.put("time", solution.getTime().getTime());
        values.put("voltage", solution.getVoltage());
        values.put("sampleID", solution.getSampleID());
        values.put("measureType", solution.getMeasureType());
        values.put("color", solution.getColor());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(solution.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
