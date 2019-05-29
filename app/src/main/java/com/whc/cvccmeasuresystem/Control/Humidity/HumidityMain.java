package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.whc.cvccmeasuresystem.Client.JobHumidity;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.HumidityDB;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.FloatWindow.FloatWindowManager;
import com.whc.cvccmeasuresystem.Model.HumidityVO;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.R;




import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;


import static com.whc.cvccmeasuresystem.Client.JobHumidity.measureFragment;
import static com.whc.cvccmeasuresystem.Common.Common.*;


public class HumidityMain extends Fragment {


    private static Activity activity;
    private static ImageView[] imageViews;
    private static SharedPreferences sharedPreferences;
    public static TextView showTime,message;
    public static long startTime;
    public static ProgressBar progress;
    public static long hour,min,second;
    private static String setTime;
    public static HumidityVO[] humidityVOS;




    public BootstrapButton con, stop, minimize, start;




    public static Handler showLightHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    for (ImageView imageView : imageViews) {
                        Common.clearImageAnimation(imageView);
                    }
                    break;
                case 1:
                    HumidityMain.message.setText(R.string.measure_start);
                    HumidityMain.message.setTextColor(Color.BLUE);
                    setTime = "Duration\n" + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", second);
                    showTime.setText(setTime);
                    break;
                case 2:
                    Common.setImageAnimation(imageViews[msg.arg1]);
                    break;
                case 3:
                    Common.showToast(activity, activity.getString(R.string.measure_start));
                    break;
            }
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
        sharedPreferences.edit().putBoolean(endModule, false).apply();
        sharedPreferences.edit().putString(finalFragment, HumidityMainString).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.humidity_main, container, false);
        humidityVOS=new HumidityVO[4];
        findViewById(view);
        showTime.setText("Duration\n 00:00:00");
        start.setOnClickListener(new startAction());
        con.setOnClickListener(new continueAction());
        stop.setOnClickListener(new stopAction());

        setMeasureNowData();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startMeasure=!(sharedPreferences.getBoolean(Common.endMeasure,true));
                if(startMeasure)
                {
                    if (HumidityMain.progress.getAnimation() == null || HumidityMain.progress.getAnimation().hasEnded())
                    {
                        Animation a=new RotateAnimation(0,720,HumidityMain.progress.getPivotX(),HumidityMain.progress.getPivotY());
                        a.setRepeatCount(Animation.INFINITE);
                        a.setDuration(2000);
                        a.setInterpolator(new LinearInterpolator());
                        HumidityMain.progress.startAnimation(a);
                    }
                    showLightHandler.sendEmptyMessage(1);
                    setMeasureNowData();
                }else{

                    HumidityMain.progress.clearAnimation();
                    message.setText(getString(R.string.measure_stop));
                    message.setTextColor(Color.RED);
                    if( Common.pageCon!=null)
                    {
                        if(pageCon.getCon1().equals("continue"))
                        {
                            con.setText("continue");
                            con.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                            showTime.setText(pageCon.getCon2());
                            setMeasureNowData();
                        }
                    }


                }

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return view;
    }

    private void findViewById(View view) {
        imageViews = new ImageView[4];
        imageViews[0] = view.findViewById(R.id.sample1I);
        imageViews[1] = view.findViewById(R.id.sample2I);
        imageViews[2] = view.findViewById(R.id.sample3I);
        imageViews[3] = view.findViewById(R.id.sample4I);

        progress=view.findViewById(R.id.progress);
        message=view.findViewById(R.id.message);
        message.setText(R.string.measure_stop);
        message.setTextColor(Color.RED);

        showTime = view.findViewById(R.id.showTime);
        start = view.findViewById(R.id.start);
        con = view.findViewById(R.id.con);
        stop = view.findViewById(R.id.stop);
        minimize = view.findViewById(R.id.minimize);
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

    }

    @Override
    public void onPause() {
        super.onPause();
        JobHumidity.onPause=true;
        PageCon pageCon=new PageCon();
        pageCon.setCon1(con.getText().toString());
        pageCon.setCon2(showTime.getText().toString());
        Common.savePageParameter(sharedPreferences,pageCon);
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (startMeasure||con.getText().equals("continue")) {
                Common.showToast(activity, "Measuring Now! Please Press Stop");
                return;
            }
            //重置圖片
            showLightHandler.sendEmptyMessage(0);
            hour=0;
            min=0;
            second=0;
            JobHumidity.setTime=true;
            startMeasure();


            DataBase dataBase=new DataBase(activity);
            SaveFileDB saveFileDB=new SaveFileDB(dataBase);
            int useId=sharedPreferences.getInt(Common.userId,0);
            SaveFile saveFile=new SaveFile();
            saveFile.setMeasureType("0");
            saveFile.setName(Common.timeToString.format(new Date(System.currentTimeMillis())));
            saveFile.setStatTime(new Timestamp(System.currentTimeMillis()));
            saveFile.setEndTime(new Timestamp(0));
            saveFile.setUserId(useId);
            saveFileDB.insert(saveFile);
            SaveFile nowFile=saveFileDB.findOldSaveFile(saveFile.getName());

            int i=1;
            HumidityDB humidityDB=new HumidityDB(dataBase);
            for (HumidityVO humidityVO:humidityVOS)
            {
                humidityVO=new HumidityVO();
                humidityVO.setFileID(nowFile.getID());
                humidityVO.setBeginTime(new Timestamp(0));
                humidityVO.setName("Sample"+i);
                humidityVO.setBaseVoltage(0);
                humidityVO.setOverVoltage(0);
                humidityVO.setLight(false);
//                humidityVO.setId((int) humidityDB.insert(humidityVO));
//                Log.d("XXXXXX"," sharedPreferences: "+ humidityVO.getId());
                sharedPreferences.edit().putInt("HumidityVO"+i,humidityVO.getId()).apply();
                i++;
            }
        }
    }

    private class continueAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (!startMeasure) {
                return;
            }

            if (JobHumidity.mRun) {
                con.setText("continue");
                con.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                stopMeasure();
                JobHumidity.mRun = false;
            } else {
                con.setText("break");
                con.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                startMeasure();
                JobHumidity.mRun = true;
            }
        }
    }

    private class stopAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            StopDialogFragment aa = new StopDialogFragment();
            aa.setObject(HumidityMain.this);
            aa.show(getFragmentManager(), "show");
        }
    }

    public void stopMeasure() {

        HumidityMain.message.setText(R.string.measure_stop);
        HumidityMain.message.setTextColor(Color.RED);
        progress.clearAnimation();
        sharedPreferences.edit().putBoolean(endMeasure, true).apply();
        JobHumidity.mRun = false;
        if (JobHumidity.socket != null) {
            try {
                JobHumidity.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(0);
        Common.showToast(activity, getString(R.string.measure_stop));
    }


    private void startMeasure() {
        //check Wifi
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssId = wifiInfo.getSSID();
        if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
            Common.showToast(activity, "Please connect BCS_Device");
            return;
        }

        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobHumidity.handlerMessage = HumidityMain.this.showLightHandler;
        JobHumidity.mRun = true;
        JobHumidity.measureType = "5";
        measureFragment = Common.HumidityMainString;

        ComponentName mServiceComponent = new ComponentName(activity, JobHumidity.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        tm.cancelAll();

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

    }

    public void setMeasureNowData(){
        HumidityDB humidityDB=new HumidityDB(new DataBase(activity));
        for(HumidityVO humidityVO:humidityDB.getAll())
        {
            Log.d("XXXXXX",humidityVO.getId()+" : "+humidityVO.isLight());
        }


        for(int i=0;i<4;i++)
        {
            int id=sharedPreferences.getInt("HumidityVO"+i,0);
             Log.d("XXXXXX"," sharedPreferences: "+ id);
//            humidityVOS[i]=humidityDB.getById(id);
//            if(humidityVOS[i].isLight())
//            {
//                Common.setImageAnimation(imageViews[i]);
//            }else{
//                Common.clearImageAnimation(imageViews[i]);
//            }
        }
    };


}

