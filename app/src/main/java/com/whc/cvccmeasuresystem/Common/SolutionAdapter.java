package com.whc.cvccmeasuresystem.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.Date;
import java.util.List;

public class SolutionAdapter extends BaseAdapter {

    private Context context;
    private List<Solution> solutions;
    private Sample sample;

    public SolutionAdapter(Context context, List<Solution> solutions, Sample sample) {
        this.context = context;
        this.solutions = solutions;
        this.sample = sample;
    }

    @Override
    public int getCount() {
        return solutions.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return solutions.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View itemView, final ViewGroup parent) {
        if (itemView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.data_adapter_item2, parent, false);
        }
        TextView times=itemView.findViewById(R.id.times);
        TextView sampleIon1=itemView.findViewById(R.id.sampleIon1);
        TextView sampleIVol1=itemView.findViewById(R.id.sampleIVol1);
        TextView dateTime=itemView.findViewById(R.id.dateTime);
        if(position==0)
        {
            times.setText("Time");
            sampleIon1.setText(sample.getIonType());
            sampleIVol1.setText("S 1(mV)");
            dateTime.setText("dateTime");
        }else {
            Solution solution=solutions.get(position-1);
            times.setText(String.valueOf(position-1));
            sampleIon1.setText(solution.getConcentration());
            sampleIVol1.setText(solution.getVoltage());
            dateTime.setText(Common.timeToString.format(new Date(solution.getTime().getTime())));
        }
        return itemView;
    }
}
