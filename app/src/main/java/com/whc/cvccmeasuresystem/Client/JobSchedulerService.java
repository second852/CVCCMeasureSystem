package com.whc.cvccmeasuresystem.Client;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;

import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Set;

import java.net.Socket;

import static com.whc.cvccmeasuresystem.Common.Common.tcpClient;

/**
 * Created by 1709008NB01 on 2018/3/29.
 */

public class JobSchedulerService extends JobService {
    private String measureDuration;
    private String measureTime;
    private Handler handlerMessage;
    private Object object;
    public Socket socket;

    public JobSchedulerService(String measureDuration, String measureTime, Handler handlerMessage, Object object, Socket socket) {
        this.measureDuration = measureDuration;
        this.measureTime = measureTime;
        this.handlerMessage = handlerMessage;
        this.object = object;
        this.socket = socket;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        tcpClient=new TCPClient("1", measureTime, DriftStep2Main.handlerMessage,object);
        tcpClient.run();

         return false;

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

}
