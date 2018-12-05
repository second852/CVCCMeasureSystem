package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.whc.cvccmeasuresystem.Model.Solution;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class SolutionDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="Solution";


    public SolutionDB(SQLiteDatabase db)
    {
        this.db=db;
    }

    public List<Solution> getAll() {
        String sql = "SELECT * FROM Solution order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<Solution> solutions = new ArrayList<>();
        Solution solution;
        while (cursor.moveToNext()) {
            solution=new Solution();
            solution.setID(cursor.getInt(0));
            solution.setConcentration(cursor.getString(1));
            solution.setTime(new Timestamp(cursor.getLong(2)));
            solution.setVoltage(cursor.getInt(3));
            solution.setSampleID(cursor.getInt(4));
            solutions.add(solution);
        }
        cursor.close();
        return solutions;
    }

    public long insert(Solution solution) {
        ContentValues values = new ContentValues();
        values.put("concentration", solution.getConcentration());
        values.put("time", solution.getTime().getTime());
        values.put("voltage", solution.getVoltage());
        values.put("sampleID", solution.getSampleID());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Solution solution) {
        ContentValues values = new ContentValues();
        values.put("id", solution.getID());
        values.put("concentration", solution.getConcentration());
        values.put("time", solution.getTime().getTime());
        values.put("voltage", solution.getVoltage());
        values.put("sampleID", solution.getSampleID());
        String whereClause = "id = ?;";
        String[] whereArgs = {Integer.toString(solution.getID())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}
