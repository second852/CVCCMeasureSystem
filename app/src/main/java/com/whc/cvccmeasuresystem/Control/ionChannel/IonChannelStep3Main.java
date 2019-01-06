package com.whc.cvccmeasuresystem.Control.ionChannel;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class IonChannelStep3Main extends Fragment {


    private static  Activity activity;
    private SharedPreferences sharedPreferences;
    private SmartTabLayout ionViewPagerTab;
    private DataBase dataBase;


    public static ViewPager ionViewPager;
    public static FragmentPagerItemAdapter adapterIonStep3;






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Ion Channel Monitor Step3");
        dataBase = new DataBase(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.ion_step3_main, container, false);
        ionViewPagerTab = view.findViewById(R.id.ionViewPagerTab);
        ionViewPager = view.findViewById(R.id.ionViewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapterIonStep3==null)
        {
            FragmentPagerItems pages = new FragmentPagerItems(activity);
            pages.add(FragmentPagerItem.of("Set", IonChannelStep3Set.class));
            pages.add(FragmentPagerItem.of("Chart(V-T)", IonChannelStep3TimeChart.class));
            pages.add(FragmentPagerItem.of("Chart(Ion-T)",IonChannelStep3ConChart.class));
            pages.add(FragmentPagerItem.of("Data", IonChannelStep3Data.class));
            adapterIonStep3 = new FragmentPagerItemAdapter(getFragmentManager(), pages);
            ionViewPager.setAdapter(adapterIonStep3);
            ionViewPager.addOnPageChangeListener(new PageListener());
            ionViewPagerTab.setViewPager(ionViewPager);
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        boolean endMeasure = sharedPreferences.getBoolean(Common.endMeasure, true);
        boolean endModule=sharedPreferences.getBoolean(Common.endModule, true);
        startMeasure = (!endMeasure);
        errorSample=new ArrayList<>();
        if (endModule) {
            Common.setSample(sharedPreferences, activity, dataBase);
        } else {
            Common.setMeasureSample(sharedPreferences, activity, dataBase);
            ionViewPager.setCurrentItem(1);
            JobService.handlerMessage = IonChannelStep3Main.handlerMessage;
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
        adapterIonStep3 = null;
    }

    private void setSample() {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        errorSample=new ArrayList<>();
        indicateColor=0;
        choiceColor=new ArrayList<>();
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);


        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, new ArrayList<Solution>());
        samples.add(sample1);
//        Log.d("XXXXX",sample1.getStandardDeviation()+" "+sample1.getR());
//        Log.d("XXXXX",sample1.getDifferenceX()+" "+sample1.getDifferenceY());
//        Log.d("XXXXX",sample1.getIntercept()+" "+sample1.getSlope());
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
            Fragment fragment = adapterIonStep3.getPage(position);
            if(fragment instanceof IonChannelStep3TimeChart)
            {
                IonChannelStep3TimeChart ionChannelStep3TimeChart= (IonChannelStep3TimeChart) fragment;
                ionChannelStep3TimeChart.setData();
            }else if(fragment instanceof  IonChannelStep3ConChart)
            {
                IonChannelStep3ConChart ionChannelStep3ConChart= (IonChannelStep3ConChart) fragment;
                ionChannelStep3ConChart.setData();
            }else if(fragment instanceof IonChannelStep3Data)
            {
                IonChannelStep3Data ionChannelStep3Data= (IonChannelStep3Data) fragment;
                ionChannelStep3Data.setListView();
            }else if(fragment instanceof IonChannelStep3Set)
            {
                IonChannelStep3Set IonChannelStep3Set = (IonChannelStep3Set) fragment;
                IonChannelStep3Set.setImage();
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
            int currentPage = ionViewPager.getCurrentItem();

            if (msg.what == 1) {
                IonChannelStep3Start();
                ionViewPager.setCurrentItem(1);
                Common.showToast(adapterIonStep3.getPage(currentPage).getActivity(), "Measurement Start!");
                return;
            }

            if (msg.what == 2) {

                indicateColor++;
                JobService.mRun = false;
                startMeasure = false;
                if (JobService.socket != null) {
                    try {
                        JobService.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                SharedPreferences sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Common.endMeasure, true).apply();
                IonChannelStep3Stop();
                return;
            }

            if (msg.what == 4) {
                Common.showToast(adapterIonStep3.getPage(currentPage).getActivity(), "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
                return;
            }

            solution1.setConcentration(Common.calculateCon(sample1,solution1.getVoltage()));
            solution2.setConcentration(Common.calculateCon(sample2,solution2.getVoltage()));
            solution3.setConcentration(Common.calculateCon(sample3,solution3.getVoltage()));
            solution4.setConcentration(Common.calculateCon(sample4,solution4.getVoltage()));


            SolutionDB solutionDB=new SolutionDB(new DataBase(activity));

            solution1.setID(solutionDB.getOneSolutionByTimeId(solution1.getSampleID(),solution1.getTime().getTime()));
            solution2.setID(solutionDB.getOneSolutionByTimeId(solution2.getSampleID(),solution2.getTime().getTime()));
            solution3.setID(solutionDB.getOneSolutionByTimeId(solution3.getSampleID(),solution3.getTime().getTime()));
            solution4.setID(solutionDB.getOneSolutionByTimeId(solution4.getSampleID(),solution4.getTime().getTime()));


            solutionDB.update(solution1);
            solutionDB.update(solution2);
            solutionDB.update(solution3);
            solutionDB.update(solution4);

            choiceColor.add(oneColor);
            dataMap.get(sample1).add(solution1);
            dataMap.get(sample2).add(solution2);
            dataMap.get(sample3).add(solution3);
            dataMap.get(sample4).add(solution4);

            if(adapterIonStep3!=null)
            {
                Fragment fragment = adapterIonStep3.getPage(currentPage);
                if(fragment instanceof IonChannelStep3TimeChart)
                {
                    IonChannelStep3TimeChart ionChannelStep3TimeChart= (IonChannelStep3TimeChart) fragment;
                    ionChannelStep3TimeChart.setData();
                }else if(fragment instanceof  IonChannelStep3ConChart)
                {
                    IonChannelStep3ConChart ionChannelStep3ConChart= (IonChannelStep3ConChart) fragment;
                    ionChannelStep3ConChart.setData();
                }else if(fragment instanceof IonChannelStep3Data)
                {
                    IonChannelStep3Data ionChannelStep3Data= (IonChannelStep3Data) fragment;
                    ionChannelStep3Data.setListView();
                }else if(fragment instanceof IonChannelStep3Set)
                {
                    IonChannelStep3Set IonChannelStep3Set = (IonChannelStep3Set) fragment;
                    IonChannelStep3Set.setImage();
                }
            }
        }
    };


    public static void IonChannelStep3Start()
    {

        Fragment fragment = adapterIonStep3.getPage(currentPage);
        if(fragment instanceof IonChannelStep3TimeChart)
        {
            IonChannelStep3TimeChart ionChannelStep3TimeChart= (IonChannelStep3TimeChart) fragment;
            ionChannelStep3TimeChart.setData();
        }else if(fragment instanceof  IonChannelStep3ConChart)
        {
            IonChannelStep3ConChart ionChannelStep3ConChart= (IonChannelStep3ConChart) fragment;
            ionChannelStep3ConChart.setData();
        }else if(fragment instanceof IonChannelStep3Data)
        {
            IonChannelStep3Data ionChannelStep3Data= (IonChannelStep3Data) fragment;
            ionChannelStep3Data.setListView();
        }else if(fragment instanceof IonChannelStep3Set)
        {
            IonChannelStep3Set IonChannelStep3Set = (IonChannelStep3Set) fragment;
            IonChannelStep3Set.setImage();
        }
    }

    public static void IonChannelStep3Stop()
    {

        if(adapterIonStep3!=null)
        {
            Fragment fragment = adapterIonStep3.getPage(currentPage);
            if(fragment instanceof IonChannelStep3TimeChart)
            {
                IonChannelStep3TimeChart ionChannelStep3TimeChart= (IonChannelStep3TimeChart) fragment;
                ionChannelStep3TimeChart.setData();
            }else if(fragment instanceof  IonChannelStep3ConChart)
            {
                IonChannelStep3ConChart ionChannelStep3ConChart= (IonChannelStep3ConChart) fragment;
                ionChannelStep3ConChart.setData();
            }else if(fragment instanceof IonChannelStep3Data)
            {
                IonChannelStep3Data ionChannelStep3Data= (IonChannelStep3Data) fragment;
                ionChannelStep3Data.setListView();
            }else if(fragment instanceof IonChannelStep3Set)
            {
                IonChannelStep3Set IonChannelStep3Set = (IonChannelStep3Set) fragment;
                IonChannelStep3Set.setImage();
            }
            ionViewPager.setCurrentItem(1);
            Common.showToast(adapterIonStep3.getPage(currentPage).getActivity(), "Measurement End!");
        }

    }

}
