package com.example.yannic.receiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.yannic.receiver.gnss.Positionsabgleich;
import com.example.yannic.receiver.util.CustomAdapter;
import com.example.yannic.receiver.wifi.SearchTask;
import com.example.yannic.receiver.wifi.ServerTask;
import com.example.yannic.receiver.wifi.WifiDirectBroadcastReceiver;

import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener, OnSeekBarChangeListener {

    private boolean isWifiEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private TextView tfConStatus;
    private ArrayList<WifiP2pDevice> incDataList = new ArrayList<WifiP2pDevice>();
    private ListView listViewIncData;
    private ArrayAdapter<WifiP2pDevice> adapter;
    private Button btnDisco;
    private Button btnStart;
    private static final String[] GPS_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int PERM_CODE = 1337;
    private SearchTask searchTask;
    private WifiP2pDeviceList p2pDeviceList;
    private static ImageView imageGPS;
    private SeekBar seekBar;
    private TextView tfRange;
    public static int MAX_RANGE = 20;
    private  int x = 0;
    private NotificationCompat.Builder builder;
    private static final int unuqieID = 231232;

    private Vibrator vibrator;

    private static ArrayList<String> rangeDiffernce = new ArrayList<>();
    private int[] rangeArray = {5, 20, 40, 60};


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(GPS_PERMS, PERM_CODE);

        tfConStatus = (TextView) findViewById(R.id.tfConStatus);
        listViewIncData = (ListView) findViewById(R.id.listViewIncData);
        btnDisco = (Button) findViewById(R.id.btnDisco);
        btnStart = (Button) findViewById(R.id.btnStart);
        imageGPS = (ImageView) findViewById(R.id.imageGPS);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tfRange = (TextView) findViewById(R.id.tfRange);

        imageGPS.setImageResource(R.drawable.ic_location_disabled_black_24dp);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(0);
        seekBar.setMax(3);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        adapter = new CustomAdapter(this, incDataList);
        listViewIncData.setAdapter(adapter);

        btnDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        ServerTask serverTask = new ServerTask(this);
        serverTask.execute();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTask != null) {
                    toggleStartButton(false);
                    CustomAdapter.startStarted();
                    searchTask.cancel(true);
                }
            }
        });

        builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                reset();
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /**
     * reset all data after disconnect.
     */
    private void reset() {
        this.toggleStartButton(true);
        CustomAdapter.reset();
        rangeDiffernce.clear();
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
            new ServerTask(this).execute();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.v("peers", peers.toString());
        this.incDataList.clear();
        this.incDataList.addAll(peers.getDeviceList());
        this.p2pDeviceList = peers;
        adapter.notifyDataSetChanged();
    }

    public void setTfConStatus(String tfConStatus) {
        this.tfConStatus.setText(tfConStatus);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_CODE:
                if (canAccessLocation()) {
                    new Positionsabgleich(this, this);
                }
                break;
            default:
                break;
        }
    }

    /**
     * changes the icon top right if own gps position can be tract.
     */
    public static void locationWorking() {
        imageGPS.setImageResource(R.drawable.ic_location_searching_black_24dp);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessLocation() {
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * starts the search async task to search for other wifi direct devices.
     * used from the broadcastreceiver.
     */
    public void startSearchTask() {
        searchTask = new SearchTask(manager, channel, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            searchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            searchTask.execute();
    }

    /**
     * enables the start button if needed and disables when position tracking started.
     *
     * @param b
     */
    private void toggleStartButton(Boolean b) {
        btnStart.setEnabled(b);
        adapter.notifyDataSetChanged();
    }

    /**
     * @param s
     */
    public void rangeDiffernce(int device, String s, final boolean b) {
        rangeDiffernce.add(device, s);
        if(!b)
            vibrator.vibrate(100);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                if(!b)
                    CustomAdapter.outOfRangeTrigger(true);
                else
                    CustomAdapter.outOfRangeTrigger(false);
            }
        });
    }

    /**
     *
     * @return
     */
    public static ArrayList<String> getRangeDiff() {
        return (rangeDiffernce.size() > 0) ? rangeDiffernce : null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int range = rangeArray[progress];
        tfRange.setText("Range: " + String.valueOf(range));
        MAX_RANGE = range;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * @TODO Not Implemented
     * @param device_name
     */
    private void makeNotification(String device_name) {
        builder.setSmallIcon(R.drawable.ic_alarm_black_24dp);
        builder.setTicker("");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(device_name);
        builder.setContentText("is out of range!");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
