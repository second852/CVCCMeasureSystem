package com.whc.cvccmeasuresystem.Control.Sensitivity;


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
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;
import static com.whc.cvccmeasuresystem.Common.Common.currentPage;


public class SensitivityStep2Main extends Fragment {


    private Activity activity;
    private SharedPreferences sharedPreferences;
    private SmartTabLayout viewPagerTab;
    private DataBase dataBase;

    public static ViewPager priceViewPager;
    public static FragmentPagerItemAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        activity.setTitle("Sensitivity Monitor Step2");
        dataBase = new DataBase(activity);
        if (tcpClient == null) {
            startMeasure = false;
        } else {
            startMeasure = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.batch_step2_main, container, false);
        viewPagerTab = view.findViewById(R.id.viewPagerTab);
        priceViewPager = view.findViewById(R.id.batchViewPager);
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        pages.add(FragmentPagerItem.of("Set", SensitivityStep2Set.class));
        pages.add(FragmentPagerItem.of("Chart(V-T)", SensitivityStep2TimeChart.class));
        pages.add(FragmentPagerItem.of("Chart(mV-Ion)", SensitivityStep2ConChart.class));
        pages.add(FragmentPagerItem.of("Data", SensitivityStep2Data.class));
        adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
        priceViewPager.setAdapter(adapter);
        priceViewPager.addOnPageChangeListener(new PageListener());
        viewPagerTab.setViewPager(priceViewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set Page
        setSample();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataMap = null;
        volCon = null;
    }

    private void setSample() {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        volCon = new HashMap<>();
        choiceColor = new ArrayList<>();

        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);


        //sample 1
        int sampleID = sharedPreferences.getInt(sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, new ArrayList<Solution>());
        volCon.put(sample1, new HashMap<String, List<Solution>>());
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, new ArrayList<Solution>());
        volCon.put(sample2, new HashMap<String, List<Solution>>());
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, new ArrayList<Solution>());
        volCon.put(sample3, new HashMap<String, List<Solution>>());
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, new ArrayList<Solution>());
        volCon.put(sample4, new HashMap<String, List<Solution>>());
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

            if (fragment instanceof SensitivityStep2TimeChart) {
                SensitivityStep2TimeChart sensitivityStep2TimeChart = (SensitivityStep2TimeChart) fragment;
                sensitivityStep2TimeChart.setData();
            } else if (fragment instanceof SensitivityStep2ConChart) {
                SensitivityStep2ConChart sensitivityStep2ConChart = (SensitivityStep2ConChart) fragment;
                sensitivityStep2ConChart.setData();
            } else if (fragment instanceof SensitivityStep2Data) {
                SensitivityStep2Data sensitivityStep2Data = (SensitivityStep2Data) fragment;
                sensitivityStep2Data.setListView();
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
            int currentPage = priceViewPager.getCurrentItem();

            if (msg.what == 1) {
                startMeasure();
                return;
            }

            if (msg.what == 2) {
                endMeasure();
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


            solution1.setMeasureType("2");
            solution2.setMeasureType("2");
            solution3.setMeasureType("2");
            solution4.setMeasureType("2");

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

            dataMap.get(sample1).add(solution1);
            dataMap.get(sample2).add(solution2);
            dataMap.get(sample3).add(solution3);
            dataMap.get(sample4).add(solution4);

            volCon.put(sample1, setMapData(volCon.get(sample1), solution1));
            volCon.put(sample2, setMapData(volCon.get(sample2), solution2));
            volCon.put(sample3, setMapData(volCon.get(sample3), solution3));
            volCon.put(sample4, setMapData(volCon.get(sample4), solution4));


            Fragment fragment = adapter.getPage(currentPage);
            if (fragment instanceof SensitivityStep2TimeChart) {
                SensitivityStep2TimeChart sensitivityStep2TimeChart = (SensitivityStep2TimeChart) fragment;
                sensitivityStep2TimeChart.setData();
            } else if (fragment instanceof SensitivityStep2ConChart) {
                SensitivityStep2ConChart sensitivityStep2ConChart = (SensitivityStep2ConChart) fragment;
                sensitivityStep2ConChart.setData();
            } else if (fragment instanceof SensitivityStep2Data) {
                SensitivityStep2Data sensitivityStep2Data = (SensitivityStep2Data) fragment;
                sensitivityStep2Data.setListView();
            }
        }
    };


    public static void startMeasure() {
        Fragment fragment = SensitivityStep2Main.adapter.getPage(currentPage);
        if (fragment instanceof SensitivityStep2TimeChart) {
            SensitivityStep2TimeChart.message.setText(R.string.measure_start);
            SensitivityStep2TimeChart.message.setTextColor(Color.BLUE);
        } else if (fragment instanceof SensitivityStep2ConChart) {
            SensitivityStep2ConChart.message.setText(R.string.measure_start);
            SensitivityStep2ConChart.message.setTextColor(Color.BLUE);
        }
        SensitivityStep2Main.priceViewPager.setCurrentItem(1);
        Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
    }

    public static void endMeasure() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClient.sendEndMessage();
            }
        }).start();
        indicateColor++;
        tcpClient.mRun = false;
        startMeasure = false;
        try {
            tcpClient.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment = SensitivityStep2Main.adapter.getPage(currentPage);
        if (fragment instanceof SensitivityStep2TimeChart) {
            SensitivityStep2TimeChart.message.setText(R.string.measure_stop);
            SensitivityStep2TimeChart.message.setTextColor(Color.RED);
        } else if (fragment instanceof SensitivityStep2ConChart) {
            SensitivityStep2ConChart.message.setText(R.string.measure_stop);
            SensitivityStep2ConChart.message.setTextColor(Color.RED);
        }
        Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
    }


    public static HashMap<String, List<Solution>> setMapData(HashMap<String, List<Solution>> data, Solution solution) {
        String ion = solution.getConcentration();
        if (data.get(ion) == null) {
            List<Solution> solutions = new ArrayList<>();
            solutions.add(solution);
            data.put(solution.getConcentration(), solutions);
        } else {
            data.get(ion).add(solution);
        }
        return data;
    }


}
