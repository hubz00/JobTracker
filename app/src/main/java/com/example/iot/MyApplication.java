package com.example.iot;

import android.app.Application;

import java.util.Date;

public class MyApplication extends Application {
    private int id = -1;
    private Date lastCheckout = new Date();
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLastCheckout() {
        return lastCheckout;
    }

    public void setLastCheckout(Date lastCheckout) {
        this.lastCheckout = lastCheckout;
    }
}
