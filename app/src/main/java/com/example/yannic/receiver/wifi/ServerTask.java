package com.example.yannic.receiver.wifi;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yannic.receiver.MainActivity;
import com.example.yannic.receiver.Positionsmodul;
import com.example.yannic.receiver.gnss.NMEA;
import com.example.yannic.receiver.gnss.Positionsabgleich;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Yannic on 25.10.2016.
 */

public class ServerTask extends AsyncTask<Void, Void, String> {

    private ServerSocket serverSocket;
    private final MainActivity mainActivity;
    private final String LOG_TAG = this.getClass().toString();
    private int devices = 1;
    private ArrayList<Positionsmodul> positionsmoduls = new ArrayList<>();
    private boolean message_end = false;
    private String rec_message = "";

    public ServerTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8288);
            Log.v(LOG_TAG, "Server Socket is open");

            /**
             * create new Positionsmodul obj for each connected Devices.
             */
            for (int i = 0; i < devices; i++) {
                Positionsmodul positionsmodul = new Positionsmodul(String.valueOf(i), mainActivity);
                positionsmodul.setSocket(serverSocket.accept());
                positionsmoduls.add(positionsmodul);
                Log.v(LOG_TAG, "Client is connected");
            }

            boolean done = false;
            while (!done) {
                for (int i = 0; i < positionsmoduls.size(); i++) {
                    Positionsmodul pos = positionsmoduls.get(i);
                    byte messageType = pos.getDataInputStream().readByte();
                    switch (messageType) {
                        default:
                            while (!message_end) {
                                byte[] b = new byte[1];
                                pos.getDataInputStream().read(b);
                                String cha = new String(b);
                                if (cha.equals("$")) {
                                    if (rec_message.length() > 33) {
                                        if (rec_message.substring(33, 34).equals("*") || !rec_message.substring(0, 5).equals("GPGGA")) {
                                            Log.v(LOG_TAG, "no GPS Data");
                                            pos.processData(devices - 1, rec_message, false);
                                        } else {
                                            Log.v(LOG_TAG, rec_message);
                                            rec_message = rec_message.substring(0, 63);
                                            pos.processData(devices - 1, rec_message, true);
                                        }
                                        rec_message = "";
                                    }
                                } else {
                                    if (!cha.equals("\n")) {
                                        rec_message = rec_message.concat(cha);
                                    }
                                }
                            }
                            //pos.processData(pos.getDataInputStream().readUTF());
                            message_end = false;
                            break;
                        case -1:
                            pos.setActive(false);
                            break;
                    }
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                Log.v(LOG_TAG, "Server Socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public void setDevices(final int devices) {
        this.devices = devices;
    }
}
