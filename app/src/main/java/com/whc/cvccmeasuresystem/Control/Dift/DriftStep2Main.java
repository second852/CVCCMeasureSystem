package com.whc.cvccmeasuresystem.Control.Dift;


import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.R;


import java.io.IOException;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class DriftStep2Main extends Fragment {


    private static Activity activity;
    private SmartTabLayout driftViewPagerTab;
    private DataBase dataBase;


    public static ViewPager driftViewPager;
    public static FragmentPagerItemAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Drift Monitor Step2");
        dataBase = new DataBase(activity);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.driff_step2_main, container, false);
        driftViewPagerTab = view.findViewById(R.id.driftViewPagerTab);
        driftViewPager = view.findViewById(R.id.driftViewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter==null)
        {
            FragmentPagerItems pages = new FragmentPagerItems(activity);
            pages.add(FragmentPagerItem.of("Set", DriftStep2Set.class));
            pages.add(FragmentPagerItem.of("Chart", DriftStep2Chart.class));
            pages.add(FragmentPagerItem.of("Data", DriftStep2Data.class));
            adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
            driftViewPager.setAdapter(adapter);
            driftViewPager.addOnPageChangeListener(new PageListener());
            driftViewPagerTab.setViewPager(driftViewPager);
        }


        //set Page
        SharedPreferences sharedPreferences=activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        boolean endMeasure=sharedPreferences.getBoolean(Common.endMeasure,true);
        boolean endModule=sharedPreferences.getBoolean(Common.endModule, true);
        startMeasure=(!endMeasure);
        if(endModule)
        {
            Common.setSample(sharedPreferences,activity,dataBase);
            measureTimes=0;
        }else{
            Common.setMeasureSample(sharedPreferences,activity,dataBase);
            driftViewPager.setCurrentItem(1);
            JobService.handlerMessage=DriftStep2Main.handlerMessage;
            Common.setMeasureTimes();
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
        adapter=null;
    }




    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            Fragment fragment = adapter.getPage(position);

            if (fragment instanceof DriftStep2Chart) {
                DriftStep2Chart driftStep2Chart = (DriftStep2Chart) fragment;
                driftStep2Chart.setData();
            } else if (fragment instanceof DriftStep2Data) {
                DriftStep2Data driftStep2Data = (DriftStep2Data) fragment;
                driftStep2Data.setListView();
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
            int currentPage = driftViewPager.getCurrentItem();

            if (msg.what == 1) {
                DriftStep2Main.driftViewPager.setCurrentItem(1);
                DriftStart();
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
                return;
            }

            if (msg.what == 2) {

                JobService.mRun=false;
                startMeasure=false;
                if(JobService.socket!=null)
                {
                    try {
                        JobService.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                activity.getSharedPreferences(userShare, Context.MODE_PRIVATE).edit().putBoolean(Common.endMeasure,true).apply();

                indicateColor++;
                DriftEnd();

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

            if(adapter!=null)
            {
                Fragment fragment = adapter.getPage(currentPage);
                if (fragment instanceof DriftStep2Chart) {
                    DriftStep2Chart driftStep2Chart = (DriftStep2Chart) fragment;
                    driftStep2Chart.setData();
                } else if (fragment instanceof DriftStep2Data) {
                    DriftStep2Data driftStep2Data = (DriftStep2Data) fragment;
                    driftStep2Data.setListView();
                }
            }

        }
    };

    public static void DriftStart()
    {
        Fragment fragment= DriftStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof DriftStep2Chart)
        {
            DriftStep2Chart.message.setText(R.string.measure_start);
            DriftStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public static void DriftEnd()
    {
        if(adapter!=null)
        {
            Fragment fragment= DriftStep2Main.adapter.getPage(currentPage);
            if(fragment instanceof DriftStep2Chart)
            {
                DriftStep2Chart.message.setText(R.string.measure_stop);
                DriftStep2Chart.message.setTextColor(Color.RED);
            }
            DriftStep2Main.driftViewPager.setCurrentItem(1);
            Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
        }
    }
}
