package com.whc.cvccmeasuresystem.Control.Batch;


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
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;


import static com.whc.cvccmeasuresystem.Common.Common.*;


public class BatchStep2Main extends Fragment {


    private static Activity activity;
    private SmartTabLayout viewPagerTab;

    private DataBase dataBase;
    public static ViewPager batchViewPager;
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
        activity.setTitle("Batch Monitor Step2");
        dataBase = new DataBase(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.batch_step2_main, container, false);
        viewPagerTab = view.findViewById(R.id.viewPagerTab);
        batchViewPager = view.findViewById(R.id.batchViewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set Page
        if (adapter == null) {
            FragmentPagerItems pages = new FragmentPagerItems(activity);
            pages.add(FragmentPagerItem.of("Set", BatchStep2Set.class));
            pages.add(FragmentPagerItem.of("Chart", BatchStep2Chart.class));
            pages.add(FragmentPagerItem.of("Data", BatchStep2Data.class));
            adapter = new FragmentPagerItemAdapter(getFragmentManager(), pages);
            batchViewPager.setAdapter(adapter);
            batchViewPager.addOnPageChangeListener(new PageListener());
            viewPagerTab.setViewPager(batchViewPager);
        }
        //set Page
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        boolean endMeasure = sharedPreferences.getBoolean(Common.endMeasure, true);
        boolean endModule=sharedPreferences.getBoolean(Common.endModule, true);
        startMeasure = (!endMeasure);
        if (endModule) {
            Common.setSample(sharedPreferences, activity, dataBase);
        } else {
            Common.setMeasureSample(sharedPreferences, activity, dataBase);
            batchViewPager.setCurrentItem(1);
            JobService.handlerMessage = BatchStep2Main.handlerMessage;
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
            int currentPage = batchViewPager.getCurrentItem();

            if (msg.what == 1) {
                BatchStep2Main.batchViewPager.setCurrentItem(1);
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement Start!");
                BatchStart();
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
                BatchStep2Main.batchViewPager.setCurrentItem(1);
                BatchStop();
                Common.showToast(adapter.getPage(currentPage).getActivity(), "Measurement End!");
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
                if (fragment instanceof BatchStep2Chart) {
                    BatchStep2Chart batchStep2Chart = (BatchStep2Chart) fragment;
                    batchStep2Chart.setData();
                } else if (fragment instanceof BatchStep2Data) {
                    BatchStep2Data batchStep2Data = (BatchStep2Data) fragment;
                    batchStep2Data.setListView();
                }
            }

        }
    };

    public static void BatchStart() {
        Fragment fragment = BatchStep2Main.adapter.getPage(currentPage);
        if (fragment instanceof BatchStep2Chart) {
            BatchStep2Chart.message.setText(R.string.measure_start);
            BatchStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public static void BatchStop() {

        if (BatchStep2Main.adapter != null) {
            Fragment fragment = BatchStep2Main.adapter.getPage(currentPage);
            if (fragment instanceof BatchStep2Chart) {
                BatchStep2Chart.message.setText(R.string.measure_stop);
                BatchStep2Chart.message.setTextColor(Color.RED);
            }
        }

    }


}
