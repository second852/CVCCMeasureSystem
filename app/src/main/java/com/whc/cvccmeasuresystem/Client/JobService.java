package com.whc.cvccmeasuresystem.Client;

import android.app.job.JobParameters;

import com.whc.cvccmeasuresystem.Model.SaveFile;

/**
 * Created by Wang on 2018/12/22.
 */

public class JobService extends android.app.job.JobService{



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               new SaveFile();
            }
        }).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
