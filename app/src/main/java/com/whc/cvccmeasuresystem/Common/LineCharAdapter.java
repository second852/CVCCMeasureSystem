package com.whc.cvccmeasuresystem.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

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

public class LineCharAdapter extends BaseAdapter {

    private Context context;
    private List<Sample> samples;
    private HashMap<Sample, List<Solution>> dataMap;


    public LineCharAdapter(Context context, List<Sample> samples, HashMap<Sample, List<Solution>> dataMap) {
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
            itemView = layoutInflater.inflate(R.layout.line_chart_adapter_item, parent, false);
        }
        ListView listView = itemView.findViewById(R.id.listData);
        LineChart lineChart = itemView.findViewById(R.id.chart_line);
        Sample sample=samples.get(position);
        List<Solution> solutions=dataMap.get(sample);
        setLineChart(lineChart,solutions,sample.getName());
        listView.setAdapter(new DataAdapter(context,solutions,sample));
        return itemView;
    }

    private void setLineChart(LineChart lineChart, List<Solution> solutions, String name) {
        List<Entry> entries = new ArrayList<Entry>();
        final int size=solutions.size();
        for (int i=0;i<solutions.size();i++) {
            entries.add(new Entry(0, solutions.get(i).getVoltage()));
        }
        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.parseColor("#dc3545"));
        dataSet.setFillColor(Color.parseColor("#dc3545"));
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
        LineData data = new LineData(dataSet);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    int index = (int) value;
                    return getLabels(size).get(index);
                } catch (Exception e) {
                    return " ";
                }
            }
        });
        lineChart.setData(data);
//        lineChart.setDescription(Common.getDeescription());
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);
        YAxis yAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);
        Legend l = lineChart.getLegend();
        l.setFormSize(18f);
        l.setTextColor(Color.parseColor("#dc3545"));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private List<String> getLabels(int size) {
        List<String> chartLabels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            chartLabels.add( i + "min");
        }
        return chartLabels;
    }
}
