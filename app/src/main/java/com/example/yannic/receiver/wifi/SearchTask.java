package com.example.yannic.receiver.wifi;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.yannic.receiver.MainActivity;

/**
 * Created by Yannic on 10.02.2017.
 */

public class SearchTask extends AsyncTask<Void, Void, String> {


    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private boolean end = false;
    private final String LOG = this.getClass().toString();

    public SearchTask(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        Log.v(LOG, "Start SearchTask");
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Void... params) {
        while (!end) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(mainActivity, "Searching for devices", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(mainActivity, "Failing ti search for devices", Toast.LENGTH_LONG).show();
                }
            });
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Log.v(LOG, "canceled the Search");
            }
        }
        return null;
    }
}
