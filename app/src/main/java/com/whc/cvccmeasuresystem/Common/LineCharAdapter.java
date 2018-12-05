package com.whc.cvccmeasuresystem.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.HashMap;
import java.util.List;

public class LineCharAdapter extends BaseAdapter {

    private List<Sample> samples;
    private Context context;
    private HashMap<Sample,List<Solution>> dataMap;

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HashMap<Sample, List<Solution>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<Sample, List<Solution>> dataMap) {
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
            itemView = layoutInflater.inflate(R.layout.data_adapter_item, parent, false);
        }
        ListView listView=itemView.findViewById(R.id.listData);
        LineChart lineChart=itemView.findViewById(R.id.chart_line);



        return itemView;
    }
}
