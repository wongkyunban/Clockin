package com.wong.clockin.util;

public class DataBean {

    private int id;
    private int amount;
    private String clockInTime;
    private String title;
    //0未签到 1已签到
    private int isClockIn;

    public DataBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockinTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public int getIsClockIn() {
        return isClockIn;
    }

    public void setIsClockIn(int isClockIn) {
        this.isClockIn = isClockIn;
    }



}
