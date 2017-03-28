package com.example.yannic.receiver;

import android.location.Location;

import com.example.yannic.receiver.gnss.NMEA;
import com.example.yannic.receiver.gnss.Positionsabgleich;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Yannic on 26.02.2017.
 */

public class Positionsmodul {

    private String name;
    private Socket socket;
    private DataInputStream dataInputStream;
    private Boolean active = false;
    private NMEA nmea;
    private MainActivity mainActivity;

    public Positionsmodul(final String name, final MainActivity mainActivity) {
        this.name = name;
        this.mainActivity = mainActivity;
        this.nmea = new NMEA();
    }

    /**
     * processes the NMEA Data
     * @param string
     * @param realData
     */
    public void processData(final int device, final String string, final boolean realData) {
        if(!realData) {
            return;
        }
        nmea.setData(string);
        Double d = Positionsabgleich.getReference().getRangeDiffernce(nmea);
        mainActivity.rangeDiffernce(device, d.toString(), Positionsabgleich.getReference().inRange(d));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        active = true;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
