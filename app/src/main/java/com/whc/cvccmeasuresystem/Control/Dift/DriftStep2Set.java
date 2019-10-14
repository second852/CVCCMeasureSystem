package com.whc.cvccmeasuresystem.Control.Dift;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.HomeDialogFragment;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Control.ChoiceFunction;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.sql.Timestamp;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class DriftStep2Set extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton con1, con2, con3, con4, start, stop, finish, step01, step03;
    private BootstrapEditText ion1, ion2, ion3, ion4, measureTime;
    private String mTime;
    private SharedPreferences sharedPreferences;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drift_step2_set, container, false);
        findViewById();
        setIon();
        return view;
    }

    private void setIon() {
        con1.setText(sample1.getIonType());
        con2.setText(sample2.getIonType());
        con3.setText(sample3.getIonType());
        con4.setText(sample4.getIonType());
    }


    @Override
    public void onStart() {
        super.onStart();
        start.setOnClickListener(new startMeasure());
        stop.setOnClickListener(new stopMeasure());
        finish.setOnClickListener(new finishFragment());
        step01.setOnClickListener(new step01OnClick());
        step03.setOnClickListener(new finishFragment());
        measureTime.setText(String.valueOf(720));
        if (pageCon != null) {
            if (pageCon.getCon1() != null) {
                ion1.setText(pageCon.getCon1());
            }
            if (pageCon.getCon2() != null) {
                ion2.setText(pageCon.getCon2());
            }
            if (pageCon.getCon3() != null) {
                ion3.setText(pageCon.getCon3());
            }
            if (pageCon.getCon4() != null) {
                ion4.setText(pageCon.getCon4());
            }
            if (pageCon.getExpTime() != null) {
                measureTime.setText(pageCon.getExpTime());
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        pageCon = new PageCon();
        String ionOne = ion1.getText().toString();
        String ionTwo = ion2.getText().toString();
        String ionThree = ion3.getText().toString();
        String ionFour = ion4.getText().toString();
        String mType = measureTime.getText().toString();
        if (ionOne != null) {
            pageCon.setCon1(ionOne);
        }
        if (ionTwo != null) {
            pageCon.setCon2(ionTwo);
        }
        if (ionTwo != null) {
            pageCon.setCon3(ionThree);
        }
        if (ionTwo != null) {
            pageCon.setCon4(ionFour);
        }
        if (mTime != null) {
            pageCon.setExpTime(mType);
        }
       Common.savePageParameter(sharedPreferences,pageCon);
    }



    private void findViewById() {
        con1 = view.findViewById(R.id.con1);
        con2 = view.findViewById(R.id.con2);
        con3 = view.findViewById(R.id.con3);
        con4 = view.findViewById(R.id.con4);
        ion1 = view.findViewById(R.id.ion1);
        ion2 = view.findViewById(R.id.ion2);
        ion3 = view.findViewById(R.id.ion3);
        ion4 = view.findViewById(R.id.ion4);
        measureTime = view.findViewById(R.id.measureTime);
        start = view.findViewById(R.id.start);
        stop = view.findViewById(R.id.stop);
        finish = view.findViewById(R.id.finish);
        step01 = view.findViewById(R.id.step01);
        step03 = view.findViewById(R.id.step03);
    }




    private class startMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //防止重複量測
            if (startMeasure) {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            String ionOne = ion1.getText().toString();
            String ionTwo = ion2.getText().toString();
            String ionThree = ion3.getText().toString();
            String ionFour = ion4.getText().toString();
            mTime = measureTime.getText().toString();
            //one
            if (Common.checkViewIon(ion1, ionOne)) {

                return;
            }
            ionOne = ionOne.trim();

            //two
            if (Common.checkViewIon(ion2, ionTwo)) {
                return;
            }
            ionTwo = ionTwo.trim();

            //three
            if (Common.checkViewIon(ion3, ionThree)) {
                return;
            }
            ionThree = ionThree.trim();

            //four
            if (Common.checkViewIon(ion4, ionFour)) {
                return;
            }
            ionFour = ionFour.trim();

            //measureTime
            if (mTime == null) {
                measureTime.setError(needInt);
                return;
            }
            mTime = mTime.trim();
            if (mTime.length() <= 0) {
                measureTime.setError(needInt);
                return;
            }
            try {

                new Double(mTime);
            } catch (Exception e) {
                measureTime.setError(needInt);
                return;
            }
            //check Wifi
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssId = wifiInfo.getSSID();
            if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
                Common.showToast(activity, "Please connect BCS_Device");
                return;
            }
            //connection
            solution1 = new Solution(ionOne, sample1.getID());
            solution2 = new Solution(ionTwo, sample2.getID());
            solution3 = new Solution(ionThree, sample3.getID());
            solution4 = new Solution(ionFour, sample4.getID());
            Common.showToast(activity, "Wifi Connecting");


            JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobService.handlerMessage=DriftStep2Main.handlerMessage;
            JobService.measureDuration="5";
            JobService.measureTime=mTime;
            JobService.mRun=true;
            JobService.measureType="0";
            JobService.measureFragment=Common.Drift2Set;


            ComponentName mServiceComponent = new ComponentName(activity, JobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
            tm.cancelAll();
            builder.setMinimumLatency(1);
            builder.setOverrideDeadline(2);
            builder.setRequiresCharging(false);
            builder.setRequiresDeviceIdle(false);
            tm.schedule(builder.build());
        }
    }

    private class stopMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            StopDialogFragment aa = new StopDialogFragment();
            aa.setObject(DriftStep2Set.this);
            aa.show(getFragmentManager(), "show");
        }
    }

    public void finishMeasure() {
        //save file
        DataBase dataBase = new DataBase(activity);
        SaveFileDB saveFileDB=new SaveFileDB(dataBase);
        SaveFile saveFile=saveFileDB.findOldSaveFileById(sample1.getFileID());
        saveFile.setEndTime(new Timestamp(System.currentTimeMillis()));
        saveFileDB.update(saveFile);


        needSet = false;
        pageCon = null;
        sharedPreferences.edit().putBoolean(endModule,true).apply();
        oldFragment.remove(oldFragment.size() - 1);
        switchFragment(new ChoiceFunction(), getFragmentManager());
    }


    private class finishFragment implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            HomeDialogFragment aa = new HomeDialogFragment();
            aa.setObject(DriftStep2Set.this);
            aa.show(getFragmentManager(), "show");
        }
    }

    private class step01OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            Common.switchFragment(new DriftStep1(), getFragmentManager());
        }
    }
}
