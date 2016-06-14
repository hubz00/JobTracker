package com.example.iot;

/**
 * Created by Rafal on 2016-05-26.
 */
public class DeviceData {
    private int deviceId;
    private String locationBefore;
    private String locationAfter;
    private int timeSpentKitchen = 0;
    private int timeSpentWork = 0;
    private int timeSpentOutside = 0;
    private int kitchen = 0;
    private int left = 0;
    private int work = 0;

    public DeviceData() {
    }

    public DeviceData(int deviceId, String locationBefore, String locationAfter, int time_spent) {
        this.deviceId = deviceId;
        this.locationBefore = locationBefore;
        this.locationAfter = locationAfter;
        switch (locationBefore) {
            case "kitchen": timeSpentKitchen = time_spent;
            case "outofbuilding": timeSpentWork = time_spent;
            case "work": timeSpentOutside = time_spent;
        }
        switch (locationAfter) {
            case "kitchen": tickKitchen();
            case "outofbuilding": tickLeft();
            case "work": tickWork();
        }
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocationBefore() {
        return locationBefore;
    }

    public void setLocationBefore(String locationBefore) {
        this.locationBefore = locationBefore;
    }

    public String getLocationAfter() {
        return locationAfter;
    }

    public void setLocationAfter(String locationAfter) {
        this.locationAfter = locationAfter;
    }

    public int getKitchen() {
        return kitchen;
    }

    public int getLeft() {
        return left;
    }

    public int getWork() {
        return work;
    }

    public void tickKitchen() {
        kitchen = 1;
        work = 0;
        left = 0;
    }

    public void tickLeft() {
        kitchen = 0;
        work = 0;
        left = 1;
    }

    public void tickWork() {
        kitchen = 0;
        work = 1;
        left = 0;
    }

    public int getTimeSpentOutside() {
        return timeSpentOutside;
    }

    public int getTimeSpentWork() {
        return timeSpentWork;
    }

    public int getTimeSpentKitchen() {
        return timeSpentKitchen;
    }
}
