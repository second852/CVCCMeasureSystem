package com.whc.cvccmeasuresystem.Control;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;



import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.userShare;


public class BatchStep2Main extends Fragment {


    private Activity activity;
    private SharedPreferences sharedPreferences;
    private  SmartTabLayout viewPagerTab;

    private static DataBase dataBase;
    private static SolutionDB solutionDB;

    public static ViewPager priceViewPager;
    public static Sample sample1, sample2, sample3, sample4;
    public static HashMap<Sample,List<Solution>> dataMap;
    public static boolean startMeasure;
    public static Solution solution1,solution2,solution3,solution4;
    public static FragmentPagerItemAdapter adapter;
    public static int currentPage;
    public static List<Sample> samples;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            activity=(Activity) context;
        }else{
            activity=getActivity();
        }

        //init
        ((AppCompatActivity)activity).getSupportActionBar().hide();
        dataBase=new DataBase(activity);
        solutionDB=new SolutionDB(dataBase.getReadableDatabase());
        startMeasure=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.batch_step2_main, container, false);
        viewPagerTab =view.findViewById(R.id.viewPagerTab);
        priceViewPager =  view.findViewById(R.id.batchViewPager);
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        pages.add(FragmentPagerItem.of("Set", BatchStep2Set.class));
        pages.add(FragmentPagerItem.of("Chart", BatchStep2Chart.class));
        pages.add(FragmentPagerItem.of("Data", BatchStep2Data.class));
        adapter = new FragmentPagerItemAdapter(getFragmentManager(),pages);
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

    private void setSample() {
        dataMap=new HashMap<>();
        samples=new ArrayList<>();
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);

        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase.getReadableDatabase());


        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1,new ArrayList<Solution>());
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2,new ArrayList<Solution>());
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3,new ArrayList<Solution>());
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4,new ArrayList<Solution>());
        samples.add(sample4);
    }


    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage=position;
            switch (position)
            {
                case 1:
                    BatchStep2Chart batchStep2Chart= (BatchStep2Chart) adapter.getPage(1);
                    batchStep2Chart.setData();
                    break;
                case 2:
                    BatchStep2Data batchStep2Data= (BatchStep2Data) adapter.getPage(2);
                    batchStep2Data.setListView();
                    break;
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
            int currentPage=priceViewPager.getCurrentItem();
            String result= (String) msg.obj;
            String[] voltages=result.split(",");
            try {
                new Integer(voltages[2]);
            }catch (Exception e)
            {
                return;
            }

            Timestamp timestamp=new Timestamp(System.currentTimeMillis());

            solution1.setVoltage(new Integer(voltages[1]));
            solution2.setVoltage(new Integer(voltages[2]));
            solution3.setVoltage(new Integer(voltages[3]));
            solution4.setVoltage(new Integer(voltages[4]));

            solution1.setTime(timestamp);
            solution2.setTime(timestamp);
            solution3.setTime(timestamp);
            solution4.setTime(timestamp);

            solutionDB.insert(solution1);
            solutionDB.insert(solution2);
            solutionDB.insert(solution3);
            solutionDB.insert(solution4);

            BatchStep2Main.dataMap.get(sample1).add(solution1);
            BatchStep2Main.dataMap.get(sample2).add(solution2);
            BatchStep2Main.dataMap.get(sample3).add(solution3);
            BatchStep2Main.dataMap.get(sample4).add(solution4);


            switch (currentPage)
            {
                case 1:
                    BatchStep2Chart batchStep2Chart= (BatchStep2Chart) adapter.getPage(1);
                    batchStep2Chart.setData();
                    break;
                case 2:
                    break;
            }
        }
    };
}
