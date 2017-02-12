package com.example.yannic.receiver.util;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yannic.receiver.MainActivity;
import com.example.yannic.receiver.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Yannic on 17.11.2016.
 */

public class CustomAdapter extends ArrayAdapter<WifiP2pDevice> {


    private static Boolean started = false;

    public CustomAdapter(Context context, List<WifiP2pDevice> wifiP2pDeviceList) {
        super(context, R.layout.row_data, wifiP2pDeviceList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.row_data, parent, false);

        String deviceName = getItem(position).deviceName;
        String distance = "";

        if(MainActivity.getRangeDiff() != null) {
            distance = MainActivity.getRangeDiff().get(position).substring(0, 2) + " Meter";
        }

        TextView tfdeviceName = (TextView) view.findViewById(R.id.tfDeviceName);
        TextView tfDistance = (TextView) view.findViewById(R.id.tfDistance);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        tfdeviceName.setText(deviceName);
        tfDistance.setText(distance);

        if (getItem(position).status == 0) {
            if (started) {
                imageView.setImageResource(R.drawable.ic_room_black_24dp);
            } else {
                imageView.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_visibility_black_24dp);
        }
        return view;
    }

    public static void startStarted() {
        started = true;
    }

    public static void reset() {
        started = false;
    }
}
