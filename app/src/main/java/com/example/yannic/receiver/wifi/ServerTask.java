package com.example.yannic.receiver.wifi;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yannic.receiver.MainActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yannic on 25.10.2016.
 */

public class ServerTask extends AsyncTask<Void, Void, String> {

    private ServerSocket serverSocket;
    //private final Handler handler;
    private final MainActivity mainActivity;
    private final String LOG_TAG = this.getClass().toString();

    public ServerTask(MainActivity mainActivity) {
        //this.handler = handler;
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8288);
            Log.v("Server", "Server Socket is open");
            Socket socket = serverSocket.accept();
            Log.v("Server", "client is connected");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            boolean done = false;
            while (!done) {
                byte messageType = dataInputStream.readByte();

                switch (messageType) {
                    default:
                        String s = dataInputStream.readUTF();
                        //Log.v("SERVER", s);
                        //NMEA nmea = new NMEA(s);
                        //Double d = Positionsabgleich.getReference().getRangeDiffernce(nmea);
                        Log.v(LOG_TAG, String.valueOf(s));
                        //mainActivity.addIncData(s);
                        break;
                    case -1:
                        done = true;
                        break;
                }
            }
            dataInputStream.close();
            socket.close();
            serverSocket.close();
            Message message = new Message();
            //handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                Log.v("Server", "serversocket closed");
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
}
