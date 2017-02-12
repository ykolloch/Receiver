package com.example.yannic.receiver.gnss;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Yannic on 25.10.2016.
 */

public class NMEA implements Serializable {

    private String message_type;
    private double latitude;
    private double longitude;
    private ArrayList<String> strings = new ArrayList<>();
    private final String LOG_TAG = this.getClass().toString();

    public NMEA(String string) {
        String[] parts = string.split(Pattern.quote(","));
        for (int i = 0; i < parts.length; i++) {
            strings.add(i, parts[i]);
        }
    }

    public double getLongitude() {
        if(strings != null) {
            return makeCordNormal(Double.parseDouble(strings.get(4)));
        }
        return 0;
    }

    public double getLatitude() {
        return makeCordNormal(Double.parseDouble(strings.get(2)));
    }


    /**
     * NMEA format coordinates back to normal format.
     *
     * @param v
     * @return
     */
    private Double makeCordNormal(double v) {
        String s = String.valueOf(v);
        String[] parts = s.split(Pattern.quote("."));
        String firstPart = parts[0];
        String secondPart = parts[1];


        secondPart = firstPart.substring(firstPart.length() - 2) + secondPart;
        firstPart = firstPart.substring(0, firstPart.length() - 2);
        return Double.valueOf(firstPart + "." + secondPart);
    }
}
