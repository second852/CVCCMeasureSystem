package com.whc.cvccmeasuresystem.Control.Sensitivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


public class SensitivityStep2Data extends Fragment{
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
        for(Sample sample:samples)
        {

            List<Solution> s=dataMap.get(sample);
            int length=s.size()-1;
            for(int i=length;i>=0;i--)
            {
                solutions.add(s.get(i));
            }
        }
        listData.setAdapter(new SolutionAdapter(activity,solutions));
    }

}
