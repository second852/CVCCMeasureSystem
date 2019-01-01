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
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.sql.Timestamp;
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
    public static List<String> errorSample;
    public static HashMap<Sample,List<Solution>> dataMap;
    public static List<Sample> samples;
    public static boolean noInitParameter;


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
        FragmentPagerItems pages = new FragmentPagerItems(activity);
        pages.add(FragmentPagerItem.of("Set", IonChannelStep3Set.class));
        pages.add(FragmentPagerItem.of("Chart(V-T)", IonChannelStep3TimeChart.class));
        pages.add(FragmentPagerItem.of("Chart(Ion-T)",IonChannelStep3ConChart.class));
        pages.add(FragmentPagerItem.of("Data", IonChannelStep3Data.class));
        adapterIonStep3 = new FragmentPagerItemAdapter(getFragmentManager(), pages);
        ionViewPager.setAdapter(adapterIonStep3);
        ionViewPager.addOnPageChangeListener(new PageListener());
        ionViewPagerTab.setViewPager(ionViewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(noInitParameter)
        {
            if(dataMap==null||dataMap.size()<=0)
            {
                setSample();
            }
            if(samples==null||samples.size()<=0)
            {
                setSample();
            }
            if(errorSample==null||errorSample.size()<=0)
            {
                setSample();
            }
            if(choiceColor==null||choiceColor.size()<=0)
            {
                setSample();
            }
            return;
        }
        setSample();
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
                IonChannelStep3Set ionChannelStep3Set= (IonChannelStep3Set) fragment;
                ionChannelStep3Set.setImage();
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
                ionViewPager.setCurrentItem(1);
                IonChannelStep3Stop();
                Common.showToast(adapterIonStep3.getPage(currentPage).getActivity(), "Measurement End!");
                return;
            }

            if (msg.what == 4) {
                Common.showToast(adapterIonStep3.getPage(currentPage).getActivity(), "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
                return;
            }


            String result = (String) msg.obj;
            String[] voltages = result.split(",");
            try {
                new Integer(voltages[2]);
            } catch (Exception e) {
                return;
            }


            Solution solution1 = new Solution(sample1.getID());
            Solution solution2 = new Solution(sample2.getID());
            Solution solution3 = new Solution(sample3.getID());
            Solution solution4 = new Solution(sample4.getID());


            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            solution1.setVoltage(new Integer(voltages[1]));
            solution2.setVoltage(new Integer(voltages[2]));
            solution3.setVoltage(new Integer(voltages[3]));
            solution4.setVoltage(new Integer(voltages[4]));


            solution1.setTime(timestamp);
            solution2.setTime(timestamp);
            solution3.setTime(timestamp);
            solution4.setTime(timestamp);


            solution1.setMeasureType("11");
            solution2.setMeasureType("11");
            solution3.setMeasureType("11");
            solution4.setMeasureType("11");

            solution1.setNumber(String.valueOf(measureTimes));
            solution2.setNumber(String.valueOf(measureTimes));
            solution3.setNumber(String.valueOf(measureTimes));
            solution4.setNumber(String.valueOf(measureTimes));

            solution1.setConcentration(Common.calculateCon(sample1,solution1.getVoltage()));
            solution2.setConcentration(Common.calculateCon(sample2,solution2.getVoltage()));
            solution3.setConcentration(Common.calculateCon(sample3,solution3.getVoltage()));
            solution4.setConcentration(Common.calculateCon(sample4,solution4.getVoltage()));

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
                IonChannelStep3Set ionChannelStep3Set= (IonChannelStep3Set) fragment;
                ionChannelStep3Set.setImage();
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
            IonChannelStep3Set ionChannelStep3Set= (IonChannelStep3Set) fragment;
            ionChannelStep3Set.setImage();
        }
    }

    public static void IonChannelStep3Stop()
    {
        indicateColor++;
        startMeasure=false;
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
            IonChannelStep3Set ionChannelStep3Set= (IonChannelStep3Set) fragment;
            ionChannelStep3Set.setImage();
        }
    }

}
