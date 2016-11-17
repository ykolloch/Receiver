package com.example.yannic.receiver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yannic on 17.11.2016.
 */

public class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(Context context, ArrayList<String> strings) {
        super(context, R.layout.row_data, strings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.row_data, parent, false);

        String s1 = getItem(position);

        TextView t2 = (TextView) view.findViewById(R.id.secondLine);

        t2.setText(s1);

        return view;
    }
}
