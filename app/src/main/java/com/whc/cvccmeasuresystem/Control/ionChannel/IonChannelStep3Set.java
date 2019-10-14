package com.whc.cvccmeasuresystem.Control.ionChannel;

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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.HomeDialogFragment;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Control.ChoiceFunction;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;


import java.sql.Timestamp;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class IonChannelStep3Set extends Fragment {

    private View view;
    private Activity activity;
    private ImageView sample1I,sample2I,sample3I,sample4I;
//    private AwesomeTextView sample1N,sample2N,sample3N,sample4N;
    private BootstrapEditText measureTime;
    private BootstrapButton start,stop,finish,step02,step01,step04;
    private String mType;
    private SharedPreferences sharedPreferences;
    private static String time;



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
        view = inflater.inflate(R.layout.ion_channel_step3_set, container, false);
        findViewById();
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        if(measureTime.getText()!=null)
        {
            time=measureTime.getText().toString();
        }
        sharedPreferences.edit().putInt("solutionTimes",measureTimes).apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        setImage();
    }

    public void setImage()
    {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        List<Solution> sampleSolution=dataMap.get(sample1);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample1.getLimitHighVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample1.getLimitLowVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                errorSample.add(sample1String);
            }
        }

        sampleSolution=dataMap.get(sample2);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample2.getLimitHighVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample2.getLimitLowVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                errorSample.add(sample2String);
            }
        }

        sampleSolution=dataMap.get(sample3);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample3.getLimitHighVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
//                sample3N.setText(sample3.getName());
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample3.getLimitLowVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
//                sample3N.setText(sample3.getName());
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                errorSample.add(sample3String);
            }

        }

        sampleSolution=dataMap.get(sample4);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample4.getLimitHighVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
//                sample4N.setText(sample4.getName());
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample4.getLimitLowVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
//                sample4N.setText(sample4.getName());
                solution.setNoNormalV(true);
            }
            if(solution.isNoNormalV())
            {
                errorSample.add(sample4String);
            }
        }
    }



    private void findViewById() {
        sample1I=view.findViewById(R.id.sample1I);
//        sample1N=view.findViewById(R.id.sample1N);
//        sample1N.setText(sample1.getName());

        sample2I=view.findViewById(R.id.sample2I);
//        sample2N=view.findViewById(R.id.sample2N);
//        sample2N.setText(sample2.getName());

        sample3I=view.findViewById(R.id.sample3I);
//        sample3N=view.findViewById(R.id.sample3N);
//        sample3N.setText(sample3.getName());

        sample4I=view.findViewById(R.id.sample4I);
//        sample4N=view.findViewById(R.id.sample4N);
//        sample4N.setText(sample4.getName());

        measureTime=view.findViewById(R.id.measureTime);


        if(time!=null)
        {
            measureTime.setText(time);
        }

        start=view.findViewById(R.id.start);
        start.setOnClickListener(new startMeasureData());
        stop=view.findViewById(R.id.stop);
        stop.setOnClickListener(new stopMeasure());
        finish=view.findViewById(R.id.finish);
        finish.setOnClickListener(new finishFragment());
        step02=view.findViewById(R.id.step02);
        step02.setOnClickListener(new step02OnClick());
        step01=view.findViewById(R.id.step01);
        step01.setOnClickListener(new step01OnClick());
        step04=view.findViewById(R.id.step04);
        step04.setOnClickListener(new finishFragment());
    }





    private class startMeasureData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //防止重複量測
            if (startMeasure) {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            mType=measureTime.getText().toString();
            mType=checkViewInteger(measureTime,mType);
            if(mType==null)
            {
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

            solution1 = new Solution("0", sample1.getID());
            solution2 = new Solution("0", sample2.getID());
            solution3 = new Solution("0", sample3.getID());
            solution4 = new Solution("0", sample4.getID());

            JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobService.handlerMessage= IonChannelStep3Main.handlerMessage;
            JobService.measureDuration="1";
            JobService.measureTime=mType;
            JobService.mRun=true;
            JobService.measureType="11";
            JobService.measureFragment=Common.IonChannel3Set;


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

            StopDialogFragment aa= new StopDialogFragment();
            aa.setObject(IonChannelStep3Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }

    private class finishFragment implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(startMeasure)
            {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            HomeDialogFragment aa= new HomeDialogFragment();
            aa.setObject(IonChannelStep3Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }

    public void finishMeasure()
    {
        DataBase dataBase=new DataBase(activity);
        SaveFileDB saveFileDB=new SaveFileDB(dataBase);
        SaveFile saveFile=saveFileDB.findOldSaveFileById(sample1.getFileID());
        saveFile.setEndTime(new Timestamp(System.currentTimeMillis()));
        saveFileDB.update(saveFile);


        IonChannelStep2Set.pageCon1=null;
        IonChannelStep2Set.pageCon1=null;

        sharedPreferences.edit().putBoolean(endModule,true).apply();
        oldFragment.remove(oldFragment.size()-1);
        Common.switchFragment(new ChoiceFunction(),getFragmentManager());
        sharedPreferences.edit().putString(Common.finalFragment,CFName).apply();
    }

    private class step02OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            switchFragment(new IonChannelStep2Main(),getFragmentManager());
            oldFragment.remove(oldFragment.size() - 1);
        }
    }

    private class step01OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            needSet=true;
            IonChannelStep1 ionChannelStep1=new IonChannelStep1();
            oldFragment.remove(oldFragment.size() - 1);
            Common.switchFragment(ionChannelStep1, getFragmentManager());
        }
    }
}
