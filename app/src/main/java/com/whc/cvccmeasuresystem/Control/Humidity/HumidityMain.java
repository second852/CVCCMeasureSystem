package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Chart;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Data;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.util.Calendar;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class HumidityMain extends Fragment {


    private static Activity activity;
    private BootstrapButton timeEndN,start,hour,minute,second;
    private Long endTime,nowTime;
    private Integer endMin,endHour,showHour,showMin,showSecond;


    private Handler showTimeHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==1)
            {
                showHour=0;
                showMin=0;
                showSecond=0;
            }

            second.setText(String.format("%02d", showSecond));
            minute.setText(String.format("%02d", showMin));
            hour.setText(String.format("%02d", showHour));
        }
    };

    private Runnable calculateTime=new Runnable() {
        @Override
        public void run() {

            while (endTime>nowTime)
            {
                nowTime=System.currentTimeMillis();
                if(nowTime%1000==0) {
                    showSecond=showSecond-1;
                    if(showSecond<0)
                    {
                        showSecond=59;
                        showMin=showMin-1;
                    }
                    if(showMin<0)
                    {
                        showHour=showHour-1;
                        showMin=59;
                    }
                    if(showHour<0)
                    {
                        showHour=0;
                        showMin=0;
                        showSecond=0;
                    }
                    showTimeHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            showTimeHandler.sendEmptyMessage(1);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Humidity Measure");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.humidity_main, container, false);
        findViewById(view);
        timeEndN.setOnClickListener(new showTime());
        start.setOnClickListener(new startAction());
        return view;
    }

    private void findViewById(View view) {
        timeEndN=view.findViewById(R.id.timeEndN);
        hour=view.findViewById(R.id.hour);
        minute=view.findViewById(R.id.minute);
        second=view.findViewById(R.id.second);
        start=view.findViewById(R.id.start);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    private class showTime implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeEndN.setText((hourOfDay + ":" + minute));
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);

                    endTime=calendar.getTimeInMillis();
                    endHour=hourOfDay;
                    endMin=minute;
                }
            }, hour, minute, false).show();
        }
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {


            nowTime=System.currentTimeMillis();
            Calendar nowCalendar=Calendar.getInstance();
            int nowMin=nowCalendar.get(Calendar.MINUTE);
            int nowHour=nowCalendar.get(Calendar.HOUR_OF_DAY);
            showMin=endMin-nowMin;
            showHour=endHour-nowHour;
            showSecond=00;


            if(showMin<0)
            {
                showMin=59;
                showHour=showHour-1;
            }

            if(showHour<0)
            {
                timeEndN.setError(getString(R.string.error_little_time));
                return;
            }

            second.setText(String.format("%02d", showSecond));
            minute.setText(String.format("%02d", showMin));
            hour.setText(String.format("%02d", showHour));

            new Thread(calculateTime).start();
        }
    }
}

