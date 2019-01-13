package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
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

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class IonChannelStep3TimeChart extends Fragment{
    private View view;
    private Activity activity;
    private LineChart[] lineCharts;
    private int size;
    public TextView message;



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
        view = inflater.inflate(R.layout.batch_step2_chart, container, false);
        lineCharts=new LineChart[4];
        lineCharts[0]=view.findViewById(R.id.charLine1);
        lineCharts[1]=view.findViewById(R.id.charLine2);
        lineCharts[2]=view.findViewById(R.id.charLine3);
        lineCharts[3]=view.findViewById(R.id.charLine4);
        message=view.findViewById(R.id.message);
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


        List<Solution> solutions=dataMap.get(sample1);
//        Solution solution=new Solution();
//        solution.setVoltage(200);
//        solutions.add(solution);
//        solutions.add(solution);
        setLineChart(lineCharts[0],solutions,sample1);

        solutions=dataMap.get(sample2);
        setLineChart(lineCharts[1],solutions,sample2);

        solutions=dataMap.get(sample3);
        setLineChart(lineCharts[2],solutions,sample3);

        solutions=dataMap.get(sample4);
        setLineChart(lineCharts[3],solutions,sample4);
    }



    private void setLineChart(LineChart lineChart, final List<Solution> solutions, final Sample sample) {
        List<Entry> entries = new ArrayList<Entry>();
        size=solutions.size();
        if(size<=0)
        {
            return;
        }

        for (int i=0;i<solutions.size();i++) {
            entries.add(new Entry(i, solutions.get(i).getVoltage()));
        }


        LineDataSet dataSet = new LineDataSet(entries, sample.getName());
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setHighlightEnabled(false);
        dataSet.setCircleRadius(3);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleColors(choiceColor);
        dataSet.setValueTextSize(12f);
        dataSet.setColors(choiceColor);


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


        lineChart.setDescription(description(sample.getName()));
        lineChart.setDrawBorders(true);
        lineChart.setData(data);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);

        YAxis yAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        LimitLine yHigh = new LimitLine(Float.valueOf(sample.getLimitHighVoltage()), "Limit Voltage");
        yHigh.setLineColor(Color.RED);
        yHigh.setTextColor(Color.RED);
        yAxis.addLimitLine(yHigh);
        LimitLine yLow = new LimitLine(Float.valueOf(sample.getLimitLowVoltage()), "Limit Voltage");
        yLow.setLineColor(Color.RED);
        yLow.setTextColor(Color.RED);
        yAxis.addLimitLine(yLow);
        yAxis.setValueFormatter(new IAxisValueFormatter(){

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleToInt(value)+"(mv)";
            }
        });


        YAxis yAxis1 = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setDrawAxisLine(false);
        yAxis1.setDrawLabels(false);

        lineChart.getLegend().setEnabled(false);
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
