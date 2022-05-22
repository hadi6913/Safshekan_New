package com.safshekan.parkban.persistence.models;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.safshekan.parkban.BR;

public class User extends BaseObservable {

    private String userName , password ;

    public User() {
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
}
