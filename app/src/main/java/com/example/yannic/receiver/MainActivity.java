package com.example.yannic.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {

    private boolean isWifiEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private TextView tfConStatus;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ArrayList<String> incDataList = new ArrayList<>();

    private ListView listViewIncData;
    private ArrayAdapter<String> adapter;
    private Button btnDisco;

    private static final String[] GPS_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int PERM_CODE = 1337;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(GPS_PERMS, PERM_CODE);

        tfConStatus = (TextView) findViewById(R.id.tfConStatus);
        listViewIncData = (ListView) findViewById(R.id.listViewIncData);
        btnDisco = (Button) findViewById(R.id.btnDisco);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiEnabled()) {
                    //@TODO
                    return;
                }
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "Failed Searching", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        adapter = new CustomAdapter(this, incDataList);
        listViewIncData.setAdapter(adapter);

        btnDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //@TODO close all and reset;
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public boolean isWifiEnabled() {
        return isWifiEnabled;
    }

    public void setWifiEnabled(boolean wifiEnabled) {
        isWifiEnabled = wifiEnabled;
    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.groupFormed && info.isGroupOwner) {
            new ServerTask(handler, this).execute();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }

    public String getTfConStatus() {
        return (String) tfConStatus.getText();
    }

    public void setTfConStatus(String tfConStatus) {
        this.tfConStatus.setText(tfConStatus);
    }

    public void addIncData(String s) {
        incDataList.add(s);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_CODE:
                if(canAccessLocation()) {
                    new Positionsabgleich(this, this);
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessLocation() {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
