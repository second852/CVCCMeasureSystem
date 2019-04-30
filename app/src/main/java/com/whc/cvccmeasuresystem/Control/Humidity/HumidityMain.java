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

import static com.whc.cvccmeasuresystem.Common.Common.choiceColor;
import static com.whc.cvccmeasuresystem.Common.Common.currentPage;
import static com.whc.cvccmeasuresystem.Common.Common.dataMap;
import static com.whc.cvccmeasuresystem.Common.Common.indicateColor;
import static com.whc.cvccmeasuresystem.Common.Common.oneColor;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.solution1;
import static com.whc.cvccmeasuresystem.Common.Common.solution2;
import static com.whc.cvccmeasuresystem.Common.Common.solution3;
import static com.whc.cvccmeasuresystem.Common.Common.solution4;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;


public class HumidityMain extends Fragment {


    private static Activity activity;
    private BootstrapButton timeEndN;



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
        timeEndN=view.findViewById(R.id.timeEndN);
        timeEndN.setOnClickListener(new showTime());
        return view;
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
                    timeEndN.setText(hourOfDay + ":" + minute);
                }
            }, hour, minute, false).show();
        }
        }
    }

