package com.whc.cvccmeasuresystem.Client;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import static com.whc.cvccmeasuresystem.Common.Common.arrayColor;
import static com.whc.cvccmeasuresystem.Common.Common.endMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.endModule;
import static com.whc.cvccmeasuresystem.Common.Common.finalFragment;
import static com.whc.cvccmeasuresystem.Common.Common.indicateColor;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.oneColor;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;

/**
 * Created by Wang on 2018/12/22.
 */

public class TimeService extends android.app.job.JobService{


    private SharedPreferences sharedPreferences;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


}
