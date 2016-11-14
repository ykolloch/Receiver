package com.example.yannic.receiver;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yannic on 25.10.2016.
 */

public class ServerTask extends AsyncTask<Void, Void, String> {

    ServerSocket serverSocket;
    Handler handler;

    public ServerTask(Handler handler) {
        this.handler = handler;
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
                        Log.v("SERVER", dataInputStream.readUTF());
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
            handler.sendMessage(message);

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
