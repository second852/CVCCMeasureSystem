package com.whc.cvccmeasuresystem.Control.Hysteresis;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class HysteresisStep2Main extends Fragment {


    private static Activity activity;
    private SharedPreferences sharedPreferences;
    private SmartTabLayout hisViewPagerTab;

    private DataBase dataBase;

    public static ViewPager hisViewPager;
    public static FragmentPagerItemAdapter adapter;
    public static int loopIndex;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Hysteresis Monitor Step2");
        dataBase = new DataBase(activity);
        if (tcpClient == null) {
            startMeasure = false;
        } else {
            startMeasure = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        dataMap=null;
//        volCon=null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.hysteresis_step2_main, container, false);
        hisViewPagerTab = view.findViewById(R.id.hisViewPagerTab);
        hisViewPager = view.findViewById(R.id.hisViewPager);
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        pages.add(FragmentPagerItem.of("Set", HysteresisStep2Set.class));
        pages.add(FragmentPagerItem.of("Chart", HysteresisStep2Chart.class));
        pages.add(FragmentPagerItem.of("Data", HysteresisStep2Data.class));
        adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
        hisViewPager.setAdapter(adapter);
        hisViewPager.addOnPageChangeListener(new PageListener());
        hisViewPagerTab.setViewPager(hisViewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set Page
        setSample();
    }

    private void setSample() {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        choiceColor=new ArrayList<>();
        indicateColor=0;
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);


        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);


        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, new ArrayList<Solution>());
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, new ArrayList<Solution>());
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, new ArrayList<Solution>());
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, new ArrayList<Solution>());
        samples.add(sample4);
    }


    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            Fragment fragment = adapter.getPage(position);

            if (fragment instanceof HysteresisStep2Chart) {
                HysteresisStep2Chart hysteresisStep2Chart = (HysteresisStep2Chart) fragment;
                hysteresisStep2Chart.setData();
            } else if (fragment instanceof HysteresisStep2Data) {
                HysteresisStep2Data hysteresisStep2Data = (HysteresisStep2Data) fragment;
                hysteresisStep2Data.setListView();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //setList
    public static Handler handlerMessage = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //data 處理
            int currentPage = hisViewPager.getCurrentItem();

            if (msg.what == 1) {
                HysteresisStart();
                HysteresisStep2Main.hisViewPager.setCurrentItem(1);
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
                return;
            }

            if (msg.what == 2) {
                HysteresisStep2Main.hisViewPager.setCurrentItem(1);
                HysteresisEnd();
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
                return;
            }

            if (msg.what == 4) {
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
                return;
            }


            String result = (String) msg.obj;
            String[] voltages = result.split(",");
            try {
                new Integer(voltages[2]);
            } catch (Exception e) {
                return;
            }


            Solution solution1 = new Solution(Common.solution1.getConcentration(), sample1.getID());
            Solution solution2 = new Solution(Common.solution2.getConcentration(), sample2.getID());
            Solution solution3 = new Solution(Common.solution3.getConcentration(), sample3.getID());
            Solution solution4 = new Solution(Common.solution4.getConcentration(), sample4.getID());


            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            solution1.setVoltage(new Integer(voltages[1]));
            solution2.setVoltage(new Integer(voltages[2]));
            solution3.setVoltage(new Integer(voltages[3]));
            solution4.setVoltage(new Integer(voltages[4]));


            solution1.setTime(timestamp);
            solution2.setTime(timestamp);
            solution3.setTime(timestamp);
            solution4.setTime(timestamp);


            solution1.setMeasureType("4");
            solution2.setMeasureType("4");
            solution3.setMeasureType("4");
            solution4.setMeasureType("4");

            solution1.setNumber(String.valueOf(measureTimes));
            solution2.setNumber(String.valueOf(measureTimes));
            solution3.setNumber(String.valueOf(measureTimes));
            solution4.setNumber(String.valueOf(measureTimes));
            measureTimes++;

            int color =Color.parseColor(arrayColor[indicateColor%arrayColor.length]);
            solution1.setColor(color);
            solution2.setColor(color);
            solution3.setColor(color);
            solution4.setColor(color);
            choiceColor.add(color);

            SolutionDB solutionDB=new SolutionDB(new DataBase(activity));
            solutionDB.insert(solution1);
            solutionDB.insert(solution2);
            solutionDB.insert(solution3);
            solutionDB.insert(solution4);


            dataMap.get(sample1).add(solution1);
            dataMap.get(sample2).add(solution2);
            dataMap.get(sample3).add(solution3);
            dataMap.get(sample4).add(solution4);

            Fragment fragment = adapter.getPage(currentPage);
            if (fragment instanceof HysteresisStep2Chart) {
                HysteresisStep2Chart hysteresisStep2Chart = (HysteresisStep2Chart) fragment;
                hysteresisStep2Chart.setData();
            } else if (fragment instanceof HysteresisStep2Data) {
                HysteresisStep2Data hysteresisStep2Data = (HysteresisStep2Data) fragment;
                hysteresisStep2Data.setListView();
            }
        }
    };

    public static  void HysteresisStart()
    {
        Fragment fragment= HysteresisStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof HysteresisStep2Chart){
            HysteresisStep2Chart.message.setText(R.string.measure_start);
            HysteresisStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public static void HysteresisEnd()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClient.sendEndMessage();
            }
        }).start();
        indicateColor++;
        tcpClient.mRun=false;
        startMeasure=false;
        try {
            tcpClient.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment= HysteresisStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof HysteresisStep2Chart){
            HysteresisStep2Chart.message.setText(R.string.measure_stop);
            HysteresisStep2Chart.message.setTextColor(Color.RED);
        }
    }
}
