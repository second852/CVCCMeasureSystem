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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
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


    private static Activity activity;
    private SharedPreferences sharedPreferences;
    private SmartTabLayout senViewPagerTab;
    private DataBase dataBase;
    public static ViewPager senViewPager;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.sen_step2_main, container, false);
        senViewPagerTab = view.findViewById(R.id.senViewPagerTab);
        senViewPager = view.findViewById(R.id.senViewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter == null) {
            FragmentPagerItems pages = new FragmentPagerItems(activity);
            pages.add(FragmentPagerItem.of("Set", SensitivityStep2Set.class));
            pages.add(FragmentPagerItem.of("Chart(V-T)", SensitivityStep2TimeChart.class));
            pages.add(FragmentPagerItem.of("Chart(mV-Ion)", SensitivityStep2ConChart.class));
            pages.add(FragmentPagerItem.of("Data", SensitivityStep2Data.class));
            adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
            senViewPager.setAdapter(adapter);
            senViewPager.addOnPageChangeListener(new PageListener());
            senViewPagerTab.setViewPager(senViewPager);
        }
        //set Page
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        boolean endMeasure = sharedPreferences.getBoolean(Common.endMeasure, true);
        boolean endModule=sharedPreferences.getBoolean(Common.endModule, true);
        startMeasure = (!endMeasure);
        volCon = new HashMap<>();
        if (endModule) {
            Common.setSample(sharedPreferences, activity, dataBase);
            volCon.put(sample1, new HashMap<String, List<Solution>>());
            volCon.put(sample2, new HashMap<String, List<Solution>>());
            volCon.put(sample3, new HashMap<String, List<Solution>>());
            volCon.put(sample4, new HashMap<String, List<Solution>>());
        } else {
            setMeasureSample(sharedPreferences, activity, dataBase);
            senViewPager.setCurrentItem(1);
            JobService.handlerMessage = SensitivityStep2Main.handlerMessage;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment f : getFragmentManager().getFragments()) {
            fragmentTransaction.remove(f);
        }
        fragmentTransaction.commit();
        adapter = null;
    }

    public void setMeasureSample(SharedPreferences sharedPreferences, Activity activity, DataBase dataBase) {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        choiceColor = new ArrayList<>();
        volCon = new HashMap<>();
        List<Solution> solutions;

        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);
        SolutionDB solutionDB = new SolutionDB(dataBase);
        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        solutions = solutionDB.getSampleAll(sampleID);
        dataMap.put(sample1, solutions);
        samples.add(sample1);
        volCon.put(sample1, new HashMap<String, List<Solution>>());
        for (Solution solution : solutions) {
            volCon.put(sample1, setMapData(volCon.get(sample1), solution));
        }
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        solutions = solutionDB.getSampleAll(sampleID);
        dataMap.put(sample2, solutions);
        samples.add(sample2);
        volCon.put(sample2, new HashMap<String, List<Solution>>());
        for (Solution solution : solutions) {
            volCon.put(sample2, setMapData(volCon.get(sample2), solution));
        }
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        solutions = solutionDB.getSampleAll(sampleID);
        dataMap.put(sample3, solutions);
        samples.add(sample3);
        volCon.put(sample3, new HashMap<String, List<Solution>>());
        for (Solution solution : solutions) {
            volCon.put(sample3, setMapData(volCon.get(sample3), solution));
        }
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        solutions = solutionDB.getSampleAll(sampleID);
        dataMap.put(sample4, solutions);
        samples.add(sample4);
        volCon.put(sample4, new HashMap<String, List<Solution>>());
        for (Solution solution : solutions) {
            volCon.put(sample4, setMapData(volCon.get(sample4), solution));
        }

        for (Solution solution : dataMap.get(sample1)) {
            choiceColor.add(solution.getColor());
        }
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
            int currentPage = senViewPager.getCurrentItem();

            if (msg.what == 1) {
                SensitivityStep2Main.senViewPager.setCurrentItem(1);
                startMeasure();
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
                return;
            }

            if (msg.what == 2) {

                JobService.mRun = false;
                startMeasure = false;
                if (JobService.socket != null) {
                    try {
                        JobService.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                indicateColor++;
                SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Common.endMeasure, true).apply();
                SensitivityStep2Main.senViewPager.setCurrentItem(1);
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
                endMeasure();
                return;
            }

            if (msg.what == 4) {
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
                return;
            }


            choiceColor.add(oneColor);

            dataMap.get(sample1).add(solution1);
            dataMap.get(sample2).add(solution2);
            dataMap.get(sample3).add(solution3);
            dataMap.get(sample4).add(solution4);

            volCon.put(sample1, setMapData(volCon.get(sample1), solution1));
            volCon.put(sample2, setMapData(volCon.get(sample2), solution2));
            volCon.put(sample3, setMapData(volCon.get(sample3), solution3));
            volCon.put(sample4, setMapData(volCon.get(sample4), solution4));


            if (adapter != null) {
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
    }

    public static void endMeasure() {

        if (adapter != null) {
            Fragment fragment = SensitivityStep2Main.adapter.getPage(currentPage);
            if (fragment instanceof SensitivityStep2TimeChart) {
                SensitivityStep2TimeChart.message.setText(R.string.measure_stop);
                SensitivityStep2TimeChart.message.setTextColor(Color.RED);
            } else if (fragment instanceof SensitivityStep2ConChart) {
                SensitivityStep2ConChart.message.setText(R.string.measure_stop);
                SensitivityStep2ConChart.message.setTextColor(Color.RED);
            }
        }
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
