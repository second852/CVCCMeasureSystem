package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.whc.cvccmeasuresystem.Common.SolutionAdapter;
import com.whc.cvccmeasuresystem.Common.SolutionAdapterIonChannel;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;


import static com.whc.cvccmeasuresystem.Common.Common.*;


public class IonChannelStep2Data extends Fragment{
    private View view;
    private Activity activity;
    private ListView listData;
    private ImageView sample1I,sample2I,sample3I,sample4I;
    private AwesomeTextView sample1N,sample2N,sample3N,sample4N;


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
        listData=view.findViewById(R.id.listData);

        sample1I=view.findViewById(R.id.sample1I);
        sample1N=view.findViewById(R.id.sample1N);
        sample1I.setImageResource(R.drawable.lighte);
        sample1N.setText(sample1.getName());

        sample2I=view.findViewById(R.id.sample2I);
        sample2N=view.findViewById(R.id.sample2N);
        sample2I.setImageResource(R.drawable.lighte);
        sample2N.setText(sample2.getName());

        sample3I=view.findViewById(R.id.sample3I);
        sample3N=view.findViewById(R.id.sample3N);
        sample3I.setImageResource(R.drawable.lighte);
        sample3N.setText(sample3.getName());

        sample4I=view.findViewById(R.id.sample4I);
        sample4N=view.findViewById(R.id.sample4N);
        sample4I.setImageResource(R.drawable.lighte);
        sample4N.setText(sample4.getName());

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

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        List<Solution> solutions=new ArrayList<>();
        solutions.add(new Solution());
        List<Solution> sampleSolution=dataMap.get(sample1);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample1.getLimitHighVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                sample1N.setText(sample1.getName()+"\n Too High");
            }
            if(solution.getVoltage()<Integer.valueOf(sample1.getLimitLowVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                sample1N.setText(sample1.getName()+"\n Too Low");
            }
            solutions.add(solution);
        }

        sampleSolution=dataMap.get(sample2);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample2.getLimitHighVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                sample2N.setText(sample2.getName()+"\nToo High");
            }
            if(solution.getVoltage()<Integer.valueOf(sample2.getLimitLowVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                sample2N.setText(sample2.getName()+"\nToo Lower");
            }
            solutions.add(solution);
        }

        sampleSolution=dataMap.get(sample3);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample3.getLimitHighVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
                sample3N.setText(sample3.getName()+"\nToo High");
            }
            if(solution.getVoltage()<Integer.valueOf(sample3.getLimitLowVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
                sample3N.setText(sample3.getName()+"\nToo Low");
            }
            solutions.add(solution);
        }

        sampleSolution=dataMap.get(sample4);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample4.getLimitHighVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
                sample4N.setText(sample4.getName()+"\nToo High");
            }
            if(solution.getVoltage()<Integer.valueOf(sample4.getLimitLowVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
                sample4N.setText(sample4.getName()+"\nToo Low");
            }
            solutions.add(solution);
        }
        listData.setAdapter(new SolutionAdapterIonChannel(activity,solutions));
    }

}
