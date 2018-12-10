package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.FinishDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import static com.whc.cvccmeasuresystem.Common.Common.dataMap;
import static com.whc.cvccmeasuresystem.Common.Common.finishToSave;
import static com.whc.cvccmeasuresystem.Common.Common.measureStartNotExist;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.needInt;
import static com.whc.cvccmeasuresystem.Common.Common.needSet;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.pageCon;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.solution1;
import static com.whc.cvccmeasuresystem.Common.Common.solution2;
import static com.whc.cvccmeasuresystem.Common.Common.solution3;
import static com.whc.cvccmeasuresystem.Common.Common.solution4;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;
import static com.whc.cvccmeasuresystem.Common.Common.tcpClient;


public class IonChannelStep2Limit extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapEditText low1, low2, low3, low4;
    private BootstrapEditText upper1, upper2, upper3, upper4;
    private BootstrapButton save;






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ion_channel_step2_limit, container, false);
        findViewById();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        save.setOnClickListener(new saveLimit());
        setLimit();
    }

    private void setLimit() {
        if(sample1.getLimitLowVoltage()!=null)
        {
            low1.setText(sample1.getLimitLowVoltage());
        }
        if(sample1.getLimitHighVoltage()!=null)
        {
            upper1.setText(sample1.getLimitHighVoltage());
        }

        if(sample2.getLimitLowVoltage()!=null)
        {
            low2.setText(sample2.getLimitLowVoltage());
        }
        if(sample2.getLimitHighVoltage()!=null)
        {
            upper2.setText(sample2.getLimitHighVoltage());
        }

        if(sample3.getLimitLowVoltage()!=null)
        {
            low3.setText(sample3.getLimitLowVoltage());
        }
        if(sample3.getLimitHighVoltage()!=null)
        {
            upper3.setText(sample3.getLimitHighVoltage());
        }

        if(sample4.getLimitLowVoltage()!=null)
        {
            low4.setText(sample4.getLimitLowVoltage());
        }
        if(sample4.getLimitHighVoltage()!=null)
        {
            upper4.setText(sample4.getLimitHighVoltage());
        }

    }


    private void findViewById() {
        low1=view.findViewById(R.id.low1);
        low2=view.findViewById(R.id.low2);
        low3=view.findViewById(R.id.low3);
        low4=view.findViewById(R.id.low4);
        upper1=view.findViewById(R.id.upper1);
        upper2=view.findViewById(R.id.upper2);
        upper3=view.findViewById(R.id.upper3);
        upper4=view.findViewById(R.id.upper4);
        save=view.findViewById(R.id.save);
    }

    private class saveLimit implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String lowV1=low1.getText().toString();
            String lowV2=low2.getText().toString();
            String lowV3=low3.getText().toString();
            String lowV4=low4.getText().toString();

            String upperV1=upper1.getText().toString();
            String upperV2=upper2.getText().toString();
            String upperV3=upper3.getText().toString();
            String upperV4=upper4.getText().toString();

            lowV1=Common.checkViewInteger(low1, lowV1);
            if (lowV1==null) {
                return;
            }
            lowV2=Common.checkViewInteger(low2, lowV2);
            if (lowV2==null) {
                return;
            }
            lowV3=Common.checkViewInteger(low3, lowV3);
            if (lowV3==null) {
                return;
            }
            lowV4=Common.checkViewInteger(low4, lowV4);
            if (lowV4==null) {
                return;
            }

            upperV1=Common.checkViewInteger(upper1, upperV1);
            if (upperV1==null) {
                return;
            }
            upperV2=Common.checkViewInteger(upper2, upperV2);
            if (upperV2==null) {
                return;
            }
            upperV3=Common.checkViewInteger(upper3, upperV3);
            if (upperV3==null) {
                return;
            }
            upperV4=Common.checkViewInteger(upper4, upperV4);
            if (upperV4==null) {
                return;
            }

            sample1.setLimitLowVoltage(lowV1);
            sample1.setLimitHighVoltage(upperV1);

            sample2.setLimitLowVoltage(lowV2);
            sample2.setLimitHighVoltage(upperV2);

            sample3.setLimitLowVoltage(lowV3);
            sample3.setLimitHighVoltage(upperV3);

            sample4.setLimitLowVoltage(lowV4);
            sample4.setLimitHighVoltage(upperV4);


            DataBase dataBase=new DataBase(activity);
            SampleDB sampleDB=new SampleDB(dataBase.getReadableDatabase());
            sampleDB.update(sample1);
            sampleDB.update(sample2);
            sampleDB.update(sample3);
            sampleDB.update(sample4);
            Common.switchFragment(new IonChannelStep2Main(),getFragmentManager());
        }
    }
}
