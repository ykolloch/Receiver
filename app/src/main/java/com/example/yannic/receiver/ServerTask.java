package com.example.yannic.receiver;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yannic on 25.10.2016.
 */

public class ServerTask extends AsyncTask<Void, Void, String> {

    ServerSocket serverSocket;

    public ServerTask() {

    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8388);
            Log.v("Server", "Server Socket is open");
            Socket socket = serverSocket.accept();
            Log.v("Server", "client is connected");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            boolean done = false;
            while (!done) {
                byte messageType = dataInputStream.readByte();

                switch (messageType) {
                    case 1:
                        Log.v("SERVER", dataInputStream.readUTF());
                    default:
                        done = true;
                }
            }
            dataInputStream.close();
            socket.close();
            serverSocket.close();
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
}
