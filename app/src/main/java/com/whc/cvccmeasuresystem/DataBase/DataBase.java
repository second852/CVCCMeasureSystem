package com.whc.cvccmeasuresystem.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "CVCCMeasureSystem";
    private static final int DB_VERSION = 1;


    private static final String Table_User =
            "CREATE TABLE User ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL) ;";

    private static final String Table_SaveFile =
            "CREATE TABLE SaveFile ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL," +
                    "measureType TEXT, startTime DATETIME ,endTime DATETIME, userId INTEGER ) ;";

    private static final String Table_Sample =
            "CREATE TABLE Sample ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL," +
                    "ionType TEXT, fileID INTEGER , limitHighVoltage Text ,limitLowVoltage Text,slope TEXT,unit Text,intercept Text,standardDeviation TEXT,differenceX TEXT,differenceY TEXT,R TEXT);";

    private static final String Table_Solution =
            "CREATE TABLE Solution ( id INTEGER PRIMARY KEY AUTOINCREMENT, concentration TEXT NOT NULL," +
                    "number text,time DATETIME, voltage INTEGER, sampleID INTEGER) ;";


    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Table_User);
        db.execSQL(Table_SaveFile);
        db.execSQL(Table_Sample);
        db.execSQL(Table_Solution);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS SaveFile");
        db.execSQL("DROP TABLE IF EXISTS Sample");
        db.execSQL("DROP TABLE IF EXISTS Solution");
        onCreate(db);
    }

}
