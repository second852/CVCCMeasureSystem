package com.whc.cvccmeasuresystem.Control.History;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.TextView;

import com.whc.cvccmeasuresystem.Common.SolutionAdapterIonChannel;
import com.whc.cvccmeasuresystem.Common.SolutionAdapterSlope;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.errorSample;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample1String;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample2String;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample3String;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.sample4String;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Control.History.HistoryMain.showFileDate;


public class HisIonChannelStep2Data extends Fragment{
    private View view;
    private Activity activity;
    private ListView listData,listSlope;
    private ImageView sample1I,sample2I,sample3I,sample4I;
//    private AwesomeTextView sample1N,sample2N,sample3N,sample4N;
    public TextView senMessage;
    public SampleDB sampleDB;
    public Integer[] index={0,0,1};
    public HashMap<Sample, List<Solution>> ionMap;


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
        view = inflater.inflate(R.layout.ion_channel_step2_data, container, false);

        SampleDB sampleDB=new SampleDB(new DataBase(activity));
        ionMap= sampleDB.setMapSampleSolutionToIC(showFileDate.getID(),"1");

        listData=view.findViewById(R.id.listData);
        listSlope=view.findViewById(R.id.listSlope);

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


        senMessage=view.findViewById(R.id.senMessage);
        listSlope.setDividerHeight(0);
        listData.setDividerHeight(0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setListView();
    }

    public void setListView()
    {
        if(startMeasure)
        {
            senMessage.setTextColor(Color.BLUE);
            senMessage.setText(R.string.measure_start);
        }else {
            senMessage.setTextColor(Color.RED);
            senMessage.setText(R.string.measure_stop);
        }

        sample1I.setImageResource(R.drawable.lighte);
        if(sample1I.getAnimation()!=null)
        {
            sample1I.getAnimation().cancel();
        }
        sample2I.setImageResource(R.drawable.lighte);
        if(sample2I.getAnimation()!=null)
        {
            sample2I.getAnimation().cancel();
        }
        sample3I.setImageResource(R.drawable.lighte);
        if(sample3I.getAnimation()!=null)
        {
            sample3I.getAnimation().cancel();
        }
        sample4I.setImageResource(R.drawable.lighte);
        if(sample4I.getAnimation()!=null)
        {
            sample4I.getAnimation().cancel();
        }

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        List<Solution> solutions=new ArrayList<>();
        solutions.add(new Solution());

        ///////////////////////////////////////
//        Solution solution1=new Solution();
//        solution1.setVoltage(2000);
//        solution1.setConcentration("4");
//        Solution solution2=new Solution();
//        solution2.setVoltage(2000);
//        solution2.setConcentration("4");
        ///////////////
        List<Solution> sampleSolution=ionMap.get(sample1);


        for(Solution solution:sampleSolution)
        {
            if(sample1.getLimitHighVoltage()==null||sample1.getLimitLowVoltage()==null)
            {
                return;
            }

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

            solutions.add(solution);
        }

        sampleSolution=ionMap.get(sample2);
        for(Solution solution:sampleSolution)
        {
            if(sample2.getLimitHighVoltage()==null||sample2.getLimitLowVoltage()==null)
            {
                return;
            }
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
            solutions.add(solution);
        }

        sampleSolution=ionMap.get(sample3);
        for(Solution solution:sampleSolution)
        {
            if(sample3.getLimitHighVoltage()==null||sample3.getLimitLowVoltage()==null)
            {
                return;
            }
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
            solutions.add(solution);
        }

        sampleSolution=ionMap.get(sample4);
        for(Solution solution:sampleSolution)
        {
            if(sample4.getLimitHighVoltage()==null||sample4.getLimitLowVoltage()==null)
            {
                return;
            }
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
            solutions.add(solution);
        }
        listSlope.setAdapter(new SolutionAdapterSlope(activity,index));
        listData.setAdapter(new SolutionAdapterIonChannel(activity,solutions));
    }

}
