package com.whc.cvccmeasuresystem.DataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whc.cvccmeasuresystem.Model.User;

import java.util.ArrayList;
import java.util.List;


public class UserDB {
    private SQLiteDatabase db;
    private String TABLE_NAME="User";


    public UserDB(SQLiteOpenHelper sq)
    {
        this.db=sq.getWritableDatabase();
    }

    public List<User> getAll() {
        String sql = "SELECT * FROM User order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<User> users = new ArrayList<>();
        User user;
        while (cursor.moveToNext()) {
            user=new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public List<String> getAllUserName() {
        String sql = "SELECT name FROM User order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        List<String> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            users.add(cursor.getString(0));
        }
        cursor.close();
        return users;
    }

    public String findUserName(String name) {
        String sql = "SELECT name FROM User where name = '"+name+"' order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        String result=null;
        if (cursor.moveToNext()) {
            result=cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    public User findUserByName(String name) {
        String sql = "SELECT * FROM User where name = '"+name+"' order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        User user=null;
        if (cursor.moveToNext()) {
            user=new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
        }
        cursor.close();
        return user;
    }

    public User findUserById(int  id) {
        String sql = "SELECT * FROM User where id = '"+id+"' order by id desc;";
        String[] args = {};
        Cursor cursor = db.rawQuery(sql, args);
        User user=null;
        if (cursor.moveToNext()) {
            user=new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
        }
        cursor.close();
        return user;
    }

    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        return db.insert(TABLE_NAME, null, values);
    }

}
