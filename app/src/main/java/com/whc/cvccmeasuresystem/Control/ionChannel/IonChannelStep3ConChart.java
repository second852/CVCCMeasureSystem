package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class IonChannelStep3ConChart extends Fragment{
    private View view;
    private Activity activity;
    private LineChart[] lineCharts;
    private int size;
    public TextView message;
    public SampleDB sampleDB;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        DataBase dataBase=new DataBase(activity);
        sampleDB=new SampleDB(dataBase);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sensitivity_step2_chart_con, container, false);
        lineCharts=new LineChart[4];
        lineCharts[0]=view.findViewById(R.id.senCharLine1);
        lineCharts[1]=view.findViewById(R.id.senCharLine2);
        lineCharts[2]=view.findViewById(R.id.senCharLine3);
        lineCharts[3]=view.findViewById(R.id.senCharLine4);
        message=view.findViewById(R.id.senMessage);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int i=1;
        for(LineChart lineChart:lineCharts)
        {
            lineChart.clear();
            lineChart.setData(null);
            Paint p = lineChart.getPaint(Chart.PAINT_INFO);
            p.setTextSize(60);
            lineChart.setNoDataText("Sample"+i+" don't have any data!");
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            i++;
        }
        setData();
    }


    public void setData()
    {
        if(startMeasure)
        {
            message.setText(R.string.measure_start);
            message.setTextColor(Color.BLUE);
        }else{
            message.setText(R.string.measure_stop);
            message.setTextColor(Color.RED);
        }





        setLineChart(lineCharts[0],dataMap.get(sample1),"sample1",sample1);


        setLineChart(lineCharts[1],dataMap.get(sample2),"sample2",sample2);
//
//
        setLineChart(lineCharts[2],dataMap.get(sample3),"sample3",sample3);
//
//
        setLineChart(lineCharts[3],dataMap.get(sample4),"sample4",sample4);
    }





    private void setLineChart(LineChart lineChart, List<Solution> solutions, String name,Sample sample) {
        List<Entry> entries = new ArrayList<Entry>();
        if(solutions==null)
        {
            return;
        }
        size=solutions.size();
        if(size<=0)
        {
            return;
        }

        int i=0;
        for(Solution solution:solutions)
        {
            entries.add(new Entry(i,Float.valueOf(solution.getConcentration())));
            i++;
        }

        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.parseColor("#000000"));
        dataSet.setFillColor(Color.parseColor("#000000"));
        dataSet.setCircleColor(Color.parseColor("#000000"));
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(3);
        dataSet.setDrawCircleHole(false);



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
        lineChart.setDescription(description(" "));
        lineChart.setDrawBorders(true);
        lineChart.setData(data);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);


        YAxis yAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxis1 = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);
        yAxis.setValueFormatter(new IAxisValueFormatter(){

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "pH"+ Common.doubleRemoveZero(String.valueOf(value));
            }
        });
        Legend l = lineChart.getLegend();
        l.setFormSize(12f);
        l.setTextColor(Color.parseColor("#000000"));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private List<String> getLabels(int size) {
        List<String> chartLabels = new ArrayList<>();
        for (int i = 0; i < (size+1); i++) {
            chartLabels.add( (i+1) + "min");
        }
        return chartLabels;
    }


}
