package com.whc.cvccmeasuresystem.Control.History;


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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Chart;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Data;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Chart;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Data;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Chart;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Data;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Set;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2ConChart;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Data;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2TimeChart;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Data;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3ConChart;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Data;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3TimeChart;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;



import static com.whc.cvccmeasuresystem.Common.Common.volCon;
import static com.whc.cvccmeasuresystem.Control.History.HistoryMain.showFileDate;


public class HistoryShowMain extends Fragment {


    private Activity activity;
    private SmartTabLayout historyMainTag;
    private DataBase dataBase;
    public  ViewPager historyMainViewPager;
    public  FragmentPagerItemAdapter adapter;
    private BootstrapButton backP;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        //init
        activity.setTitle("History");
        dataBase = new DataBase(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.history_show_main, container, false);
        historyMainTag = view.findViewById(R.id.historyMainTag);
        historyMainViewPager = view.findViewById(R.id.historyMainViewPager);
        backP=view.findViewById(R.id.backP);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToSearch();
            }
        });
        setShowViewPager();
        return view;
    }



    private void setShowViewPager() {
        String measureType=showFileDate.getMeasureType();
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        DataBase dataBase=new DataBase(activity);
        SampleDB sampleDB=new SampleDB(dataBase);

        switch (measureType)
        {
            case "0":
                Common.dataMap=sampleDB.setMapSampeSolution(showFileDate.getID());
                pages.add(FragmentPagerItem.of("Chart", BatchStep2Chart.class));
                pages.add(FragmentPagerItem.of("Data", BatchStep2Data.class));
                break;
            case "3":
                Common.dataMap=sampleDB.setMapSampeSolution(showFileDate.getID());
                pages.add(FragmentPagerItem.of("Chart", DriftStep2Chart.class));
                pages.add(FragmentPagerItem.of("Data", DriftStep2Data.class));
                break;
            case "4":
                Common.dataMap=sampleDB.setMapSampeSolution(showFileDate.getID());
                pages.add(FragmentPagerItem.of("Chart", HysteresisStep2Chart.class));
                pages.add(FragmentPagerItem.of("Data", HysteresisStep2Data.class));
                break;
            case "1":
                Common.dataMap=sampleDB.setMapSampeSolutionToIC(showFileDate.getID(),"1");
                pages.add(FragmentPagerItem.of("Data", IonChannelStep2Data.class));
                choiceColor=new ArrayList<>();
                for (Solution solution:dataMap.get(Common.sample1))
                {
                    choiceColor.add(solution.getColor());
                }
                pages.add(FragmentPagerItem.of("Chart(V-T)", IonChannelStep3TimeChart.class));
                pages.add(FragmentPagerItem.of("Chart(Ion-T)",IonChannelStep3ConChart.class));
                pages.add(FragmentPagerItem.of("Data", IonChannelStep3Data.class));
                break;
            case "2":
                Common.dataMap=sampleDB.setMapSampeSolution(showFileDate.getID());
                volCon=new HashMap<>();
                volCon.put(sample1,new HashMap<String, List<Solution>>());
                volCon.put(sample2,new HashMap<String, List<Solution>>());
                volCon.put(sample3,new HashMap<String, List<Solution>>());
                volCon.put(sample4,new HashMap<String, List<Solution>>());
                for (Sample sample:dataMap.keySet())
                {
                    for(Solution solution:dataMap.get(sample))
                    {
                        volCon.put(sample, SensitivityStep2Main.setMapData(volCon.get(sample), solution));
                    }
                }
                pages.add(FragmentPagerItem.of("Chart(V-T)", SensitivityStep2TimeChart.class));
                pages.add(FragmentPagerItem.of("Chart(mV-Ion)", SensitivityStep2ConChart.class));
                pages.add(FragmentPagerItem.of("Data", SensitivityStep2Data.class));
                break;
        }
        adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
        historyMainViewPager.setAdapter(adapter);
        historyMainTag.setViewPager(historyMainViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        //set Page
        if(showFileDate==null)
        {
            backToSearch();
            return ;
        }

    }


    public void backToSearch()
    {
        switchFragment(new HistoryMain(),getFragmentManager());
        oldFragment.remove(oldFragment.size() - 1);
    }




}
