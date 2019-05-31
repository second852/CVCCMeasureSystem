package com.whc.cvccmeasuresystem.Common;

import android.os.Message;

import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.Model.HumidityVO;

public class TimeRunnable implements Runnable {

    private int what;
    private HumidityVO humidityVO;
    private boolean timeRun;
    private long nowTime,differ,second,min,hour;
    private long[] time;


    public TimeRunnable(int what, HumidityVO humidityVO) {
        this.what = what;
        this.humidityVO = humidityVO;
        timeRun=true;
        time=new long[3];

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

                time[0]=second;
                time[1]=min;
                time[2]=hour;

                Message message=new Message();
                message.obj=time;
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
