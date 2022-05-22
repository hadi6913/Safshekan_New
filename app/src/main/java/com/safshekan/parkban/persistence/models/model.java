package com.safshekan.parkban.persistence.models;

public class model {

    private long id ;
    private String msg , msg2;
    private boolean dataBase ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg2() {
        return msg2;
    }

    public void setMsg2(String msg2) {
        this.msg2 = msg2;
    }

    public boolean isDataBase() {
        return dataBase;
    }

    public void setDataBase(boolean dataBase) {
        this.dataBase = dataBase;
    }
}
