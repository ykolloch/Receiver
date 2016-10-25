package com.example.yannic.receiver;

import java.io.Serializable;

/**
 * Created by Yannic on 25.10.2016.
 */

public class NMEA implements Serializable {

    private String name;

    public NMEA(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
