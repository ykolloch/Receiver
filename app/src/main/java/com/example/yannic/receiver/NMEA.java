package com.example.yannic.receiver;

import java.io.Serializable;

/**
 * Created by Yannic on 25.10.2016.
 */

public class NMEA implements Serializable {

    private String lat;
    private String log;

    public NMEA(String lat, String log) {
        this.lat = lat;
        this.log = log;
    }

    public String getLat() {
        return lat;
    }

    public String getLog() {
        return log;
    }
}
