package com.whc.cvccmeasuresystem.Control.Batch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.whc.cvccmeasuresystem.Common.SolutionAdapter;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;


import static com.whc.cvccmeasuresystem.Common.Common.*;

public class BatchStep2Data extends Fragment{
    private View view;
    private Activity activity;
    private ListView listData;


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
        view = inflater.inflate(R.layout.batch_step2_data, container, false);
        listData=view.findViewById(R.id.listData);
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
        List<Solution> solutions=new ArrayList<>();
        solutions.add(new Solution());
        if(samples!=null)
        {
            for(Sample sample:samples)
            {
                solutions.addAll(dataMap.get(sample));
            }
        }
        listData.setAdapter(new SolutionAdapter(activity,solutions));
    }

}
