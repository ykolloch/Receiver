package com.example.yannic.receiver;

import android.location.Location;

/**
 * Created by Yannic on 26.02.2017.
 */

public class Positionsmodul {

    private String name;
    private Location location;

    public Positionsmodul(final String name) {
        this.name = name;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
