package com.whc.cvccmeasuresystem.Common;


import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.Model.HumidityVO;


public class TimeRunnable implements Runnable {

    private int what;
    private HumidityVO humidityVO;
    public static boolean timeRun;
    private long nowTime,differ,second,min,hour;
    private String setTime;
    private SharedPreferences sharedPreferences;
    private String nameThread;


    public TimeRunnable(int what, HumidityVO humidityVO, SharedPreferences sharedPreferences, String nameThread) {
        this.what = what;
        this.humidityVO = humidityVO;
        this.sharedPreferences = sharedPreferences;
        this.nameThread = nameThread;
    }

    @Override
    public void run() {
        while (timeRun)
        {

            nowTime=System.currentTimeMillis();
            differ=nowTime-humidityVO.getBeginTime().getTime();
            if(differ%1000==0) {

                second=second+1;

                if(second>=60)
                {
                    second=0;
                    min=min+1;
                }

                if(min>=60)
                {
                    min=0;
                    hour=hour+1;
                }

                setTime = String.format("%02d", hour) + ":" + String.format("%02d", min) ;
                setTime=String.valueOf(setTime);
                sharedPreferences.edit().putString(nameThread,setTime).apply();
                Log.d("Sample Job",humidityVO.getName()+" : " +setTime);
                Message message=new Message();
                message.obj=setTime;
                message.what=what;
                HumidityMain.overHandler.sendMessage(message);



                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }


    public boolean isTimeRun() {
        return timeRun;
    }

    public void setTimeRun(boolean timeRun) {
        this.timeRun = timeRun;
    }
}
