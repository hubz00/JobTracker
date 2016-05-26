package com.example.iot;

/**
 * Created by Rafal on 2016-05-26.
 */
public class DeviceData {
    private int deviceId;
    private String location;
    private int time_spent;

    public DeviceData() {
    }

    public DeviceData(int deviceId, String location, int time_spent) {

        this.deviceId = deviceId;
        this.location = location;
        this.time_spent = time_spent;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTime_spent() {
        return time_spent;
    }

    public void setTime_spent(int time_spent) {
        this.time_spent = time_spent;
    }
}
