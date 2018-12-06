package com.whc.cvccmeasuresystem.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;


import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleAdapter extends BaseAdapter {

    private Context context;
    private List<Sample> samples;
    private HashMap<Sample, List<Solution>> dataMap;


    public SampleAdapter(Context context, List<Sample> samples, HashMap<Sample, List<Solution>> dataMap) {
        this.context = context;
        this.samples = samples;
        this.dataMap = dataMap;
    }

    @Override
    public int getCount() {
        return samples.size();
    }

    @Override
    public Object getItem(int i) {
        return samples.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View itemView, final ViewGroup parent) {
        if (itemView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.data_adapter_item1, parent, false);
        }
        TextView textView=itemView.findViewById(R.id.tittle);
        ListView listView = itemView.findViewById(R.id.listData);
        Sample sample=samples.get(position);
        List<Solution> solutions=dataMap.get(sample);
        textView.setText("Sample"+(position+1));
        listView.setAdapter(new SolutionAdapter(context,solutions,sample));
        return itemView;
    }

}
