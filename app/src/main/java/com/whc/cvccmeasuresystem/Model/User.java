package com.whc.cvccmeasuresystem.Model;

public class User {

    private int id; //DB id
    private String name;// user name

    public User(String name) {
        this.name = name;
    }

    public User() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
