package com.whc.cvccmeasuresystem.Control.ionChannel;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Chart;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Data;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.arrayColor;
import static com.whc.cvccmeasuresystem.Common.Common.choiceColor;
import static com.whc.cvccmeasuresystem.Common.Common.currentPage;
import static com.whc.cvccmeasuresystem.Common.Common.dataMap;
import static com.whc.cvccmeasuresystem.Common.Common.indicateColor;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.needSet;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.samples;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.tcpClient;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;
import static com.whc.cvccmeasuresystem.Common.Common.volCon;


public class IonChannelStep2Main extends Fragment {


    private Activity activity;
    private SharedPreferences sharedPreferences;
    private SmartTabLayout viewPagerTab;
    private DataBase dataBase;


    public static ViewPager priceViewPager;
    public static FragmentPagerItemAdapter adapter;
    public static List<String> errorSample;
    public static boolean initParameter;
    public static boolean needOldData;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Ion Channel Monitor Step2");
        dataBase = new DataBase(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.batch_step2_main, container, false);
        viewPagerTab = view.findViewById(R.id.viewPagerTab);
        priceViewPager = view.findViewById(R.id.batchViewPager);
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        pages.add(FragmentPagerItem.of("Set", IonChannelStep2Set.class));
        pages.add(FragmentPagerItem.of("Data", IonChannelStep2Data.class));
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
        if(initParameter)
        {
            setSample();
        }else{
            if(dataMap==null||dataMap.size()<=0)
            {
                setSample();
            }
        }
        if(needOldData)
        {
            setOldSample();
        }
    }

    private void setOldSample() {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        errorSample=new ArrayList<>();
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);
        SolutionDB solutionDB=new SolutionDB(dataBase);
        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1,solutionDB.getSampleAll(sample1.getID()));
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, solutionDB.getSampleAll(sample2.getID()));
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, solutionDB.getSampleAll(sample3.getID()));
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, solutionDB.getSampleAll(sample4.getID()));
        samples.add(sample4);

    }

    private void setSample() {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        errorSample=new ArrayList<>();
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
            if (fragment instanceof BatchStep2Chart) {
                BatchStep2Chart batchStep2Chart = (BatchStep2Chart) fragment;
                batchStep2Chart.setData();
            } else if (fragment instanceof BatchStep2Data) {
                BatchStep2Data batchStep2Data = (BatchStep2Data) fragment;
                batchStep2Data.setListView();
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
                IonChannelStep2Start();
                IonChannelStep2Main.priceViewPager.setCurrentItem(1);
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
                return;
            }

            if (msg.what == 2) {
                IonChannelStep2Stop();
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


            solution1.setNumber(String.valueOf(measureTimes));
            solution2.setNumber(String.valueOf(measureTimes));
            solution3.setNumber(String.valueOf(measureTimes));
            solution4.setNumber(String.valueOf(measureTimes));


            dataMap.get(sample1).add(solution1);
            dataMap.get(sample2).add(solution2);
            dataMap.get(sample3).add(solution3);
            dataMap.get(sample4).add(solution4);


            Fragment fragment = adapter.getPage(1);

            if (fragment instanceof IonChannelStep2Data) {
                IonChannelStep2Data ionChannelStep2Data = (IonChannelStep2Data) fragment;
                ionChannelStep2Data.setListView();
            }
        }
    };


    public static void IonChannelStep2Start()
    {
        Fragment fragment= IonChannelStep2Main.adapter.getPage(1);
        if(fragment instanceof IonChannelStep2Data)
        {
            IonChannelStep2Data ionChannelStep2Data= (IonChannelStep2Data) fragment;
            ionChannelStep2Data.senMessage.setTextColor(Color.BLUE);
            ionChannelStep2Data.senMessage.setText(R.string.measure_start);
        }
    }

    public static void IonChannelStep2Stop()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClient.sendEndMessage();
            }
        }).start();
        tcpClient.mRun=false;
        startMeasure=false;
        Fragment fragment= IonChannelStep2Main.adapter.getPage(1);
        if(fragment instanceof IonChannelStep2Data)
        {
            IonChannelStep2Data ionChannelStep2Data= (IonChannelStep2Data) fragment;
            ionChannelStep2Data.senMessage.setTextColor(Color.RED);
            ionChannelStep2Data.senMessage.setText(R.string.measure_stop);
        }
    }
}
