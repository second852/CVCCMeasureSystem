package com.whc.cvccmeasuresystem.Control.Hysteresis;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
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


public class HysteresisStep2Main extends Fragment {



    private SmartTabLayout hisViewPagerTab;
    private DataBase dataBase;

    private static Activity activity;
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.hysteresis_step2_main, container, false);
        hisViewPagerTab = view.findViewById(R.id.hisViewPagerTab);
        hisViewPager = view.findViewById(R.id.hisViewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter == null) {
            FragmentPagerItems pages = new FragmentPagerItems(activity);
            pages.add(FragmentPagerItem.of("Set", HysteresisStep2Set.class));
            pages.add(FragmentPagerItem.of("Chart", HysteresisStep2Chart.class));
            pages.add(FragmentPagerItem.of("Data", HysteresisStep2Data.class));
            adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
            hisViewPager.setAdapter(adapter);
            hisViewPager.addOnPageChangeListener(new PageListener());
            hisViewPagerTab.setViewPager(hisViewPager);
        }


        //set Page
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        boolean endMeasure = sharedPreferences.getBoolean(Common.endMeasure, true);
        boolean endModule=sharedPreferences.getBoolean(Common.endModule, true);
        startMeasure = (!endMeasure);
        if (endModule) {
            Common.setSample(sharedPreferences, activity, dataBase);
            measureTimes=0;
        } else {
            Common.setMeasureSample(sharedPreferences, activity, dataBase);
            hisViewPager.setCurrentItem(1);
            JobService.handlerMessage = HysteresisStep2Main.handlerMessage;
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
        adapter = null;
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
                HysteresisEnd();
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


            if (adapter != null) {
                Fragment fragment = adapter.getPage(currentPage);
                if (fragment instanceof HysteresisStep2Chart) {
                    HysteresisStep2Chart hysteresisStep2Chart = (HysteresisStep2Chart) fragment;
                    hysteresisStep2Chart.setData();
                } else if (fragment instanceof HysteresisStep2Data) {
                    HysteresisStep2Data hysteresisStep2Data = (HysteresisStep2Data) fragment;
                    hysteresisStep2Data.setListView();
                }
            }
        }
    };

    public static void HysteresisStart() {
        Fragment fragment = HysteresisStep2Main.adapter.getPage(currentPage);
        if (fragment instanceof HysteresisStep2Chart) {
            HysteresisStep2Chart.message.setText(R.string.measure_start);
            HysteresisStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public static void HysteresisEnd() {
        if (adapter != null) {
            Fragment fragment = HysteresisStep2Main.adapter.getPage(currentPage);
            if (fragment instanceof HysteresisStep2Chart) {
                HysteresisStep2Chart.message.setText(R.string.measure_stop);
                HysteresisStep2Chart.message.setTextColor(Color.RED);
            }

            HysteresisStep2Main.hisViewPager.setCurrentItem(1);
            Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
        }
    }
}
