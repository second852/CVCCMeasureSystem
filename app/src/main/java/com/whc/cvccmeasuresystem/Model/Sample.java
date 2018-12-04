package com.whc.cvccmeasuresystem.Model;

/**
 * Created by Wang on 2018/12/4.
 */

public class Sample {

    private int ID;
    private String name;
    private String type;// solution type Common-SolutionType
    private int fileID;// file measure id


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
