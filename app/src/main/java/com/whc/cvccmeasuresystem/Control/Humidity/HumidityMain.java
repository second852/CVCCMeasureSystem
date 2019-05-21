package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.whc.cvccmeasuresystem.Client.JobHumidity;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.FloatWindow.FloatWindowManager;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.R;


import java.io.IOException;
import java.util.Calendar;

import static com.whc.cvccmeasuresystem.Client.JobHumidity.measureFragment;
import static com.whc.cvccmeasuresystem.Common.Common.*;



public class HumidityMain extends Fragment {


    private static Activity activity;
    private static BootstrapButton timeEndN,start,hour,minute,second;
    private static ImageView sensor;
    private static SharedPreferences sharedPreferences;
    private static AwesomeTextView remain;

    private BootstrapButton con,stop,minimize;
    private String setTime;



    public static Handler showLightHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            switch (msg.what)
            {
                case 0:
                    sensor.setImageResource(R.drawable.lighte);
                    sensor.clearAnimation();
                    break;
                case 1:
                    sensor.setImageResource(R.drawable.lighto);
                    sensor.startAnimation(animation);
                    break;
                case 2:
                    JobHumidity.showHour=0;
                    JobHumidity.showMin=0;
                    JobHumidity.showSecond=0;
                    second.setText(String.format("%02d", JobHumidity.showSecond));
                    minute.setText(String.format("%02d", JobHumidity.showMin));
                    hour.setText(String.format("%02d", JobHumidity.showHour));
                   break;
                case 3:
                    second.setText(String.format("%02d", JobHumidity.showSecond));
                    minute.setText(String.format("%02d", JobHumidity.showMin));
                    hour.setText(String.format("%02d", JobHumidity.showHour));
                    break;
            }

            Common.pageCon = new PageCon();
            pageCon.setCon1(timeEndN.getText().toString());
            pageCon.setCon2(hour.getText().toString());
            pageCon.setCon3(minute.getText().toString());
            pageCon.setCon4(second.getText().toString());
            Common.savePageParameter(sharedPreferences,pageCon);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Humidity Measure");
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(endModule,false).apply();
        sharedPreferences.edit().putString(finalFragment,HumidityMain).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.humidity_main, container, false);
        findViewById(view);
        timeEndN.setOnClickListener(new showTime());
        start.setOnClickListener(new startAction());
        con.setOnClickListener(new continueAction());
        stop.setOnClickListener(new stopAction());
        return view;
    }

    private void findViewById(View view) {
        timeEndN=view.findViewById(R.id.timeEndN);
        hour=view.findViewById(R.id.hour);
        minute=view.findViewById(R.id.minute);
        second=view.findViewById(R.id.second);
        start=view.findViewById(R.id.start);
        con=view.findViewById(R.id.con);
        stop=view.findViewById(R.id.stop);
        sensor=view.findViewById(R.id.sensor);
        remain=view.findViewById(R.id.remain);
        minimize=view.findViewById(R.id.minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(activity);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        JobHumidity.onPause=false;
        boolean endModule=sharedPreferences.getBoolean(Common.endModule,true);
        if(!endModule&&pageCon!=null)
        {
            timeEndN.setText(pageCon.getCon1());
            hour.setText(pageCon.getCon2());
            minute.setText(pageCon.getCon3());
            second.setText(pageCon.getCon4());
        }else{
            timeEndN.setText("00:00:00");
            hour.setText("00");
            minute.setText("00");
            second.setText("00");
        }

        if(startMeasure)
        {
            remain.setText(getString(R.string.measure_start));
            remain.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        }else{
            remain.setText(getString(R.string.measure_stop));
            remain.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        JobHumidity.onPause=true;
    }


    private class showTime implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeEndN.setError(null);
                    setTime=String.format("%02d", hourOfDay)+" : "+String.format("%02d", minute);
                    timeEndN.setText(setTime);

                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);

                    JobHumidity.endTime=calendar.getTimeInMillis();
                    JobHumidity.endHour=hourOfDay;
                    JobHumidity.endMin=minute;
                }
            }, hour, minute, false).show();
        }
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(startMeasure)
            {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            JobHumidity.nowTime=System.currentTimeMillis();
            Calendar nowCalendar=Calendar.getInstance();
            int nowMin=nowCalendar.get(Calendar.MINUTE);
            int nowHour=nowCalendar.get(Calendar.HOUR_OF_DAY);
            JobHumidity.showMin= JobHumidity.endMin-nowMin;
            JobHumidity.showHour= JobHumidity.endHour-nowHour;
            JobHumidity.showSecond=00;

            if ((JobHumidity.endTime-JobHumidity.nowTime)<0)
            {
                timeEndN.setError(" ");
                Common.showToast(activity,getString(R.string.error_little_time));
                return;
            }


            if(JobHumidity.showMin<0)
            {
                JobHumidity.showMin=JobHumidity.showMin+60;
                JobHumidity.showHour=JobHumidity.showHour-1;
            }
            //設定時間
            showLightHandler.sendEmptyMessage(3);
            //重置圖片
            showLightHandler.sendEmptyMessage(0);
            startMeasure();
        }
    }

    private class continueAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(!startMeasure)
            {
                return;
            }

            if(JobHumidity.mRun)
            {
                con.setText("continue");
                con.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                stopMeasure();
                JobHumidity.mRun=false;
            }else{
                con.setText("break");
                con.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                startMeasure();
                JobHumidity.mRun=true;
            }
        }
    }

    private class stopAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            StopDialogFragment aa= new StopDialogFragment();
            aa.setObject(HumidityMain.this);
            aa.show(getFragmentManager(),"show");
            stopMeasure();

        }
    }

    public void stopMeasure()
    {
        sharedPreferences.edit().putBoolean(endMeasure,true).apply();
        JobHumidity.mRun=false;
        if(JobHumidity.socket!=null)
        {
            try {
                JobHumidity.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(0);
        remain.setText(getString(R.string.measure_stop));
        remain.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
        Common.showToast(activity, getString(R.string.measure_stop));
    }



    private void startMeasure()
    {
        //check Wifi
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssId = wifiInfo.getSSID();
        if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
            Common.showToast(activity, "Please connect BCS_Device");
            return;
        }
        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobHumidity.handlerMessage= HumidityMain.this.showLightHandler;
        JobHumidity.measureDuration="1";
        JobHumidity.mRun=true;
        JobHumidity.measureType="5";
        measureFragment=Common.HumidityMain;
        JobHumidity.measureTime=String.valueOf(JobHumidity.showHour*60+JobHumidity.showMin+1);

        ComponentName mServiceComponent = new ComponentName(activity, JobHumidity.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        tm.cancelAll();

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

        remain.setText(getString(R.string.measure_start));
        remain.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        Common.showToast(activity, getString(R.string.measure_start));
    }

}

