package com.whc.cvccmeasuresystem.Control.Sensitivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.DoubleToInt;
import static com.whc.cvccmeasuresystem.Common.Common.description;
import static com.whc.cvccmeasuresystem.Common.Common.*;



public class SensitivityStep2ConChart extends Fragment{
    private View view;
    private Activity activity;
    private LineChart[] lineCharts;
    private int size;
    public  static  TextView message;




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
            Paint p = lineChart.getPaint(Chart.PAINT_INFO);
            p.setTextSize(60);
            lineChart.setNoDataText("Sample"+i+" don't have any data!");
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





        setLineChart(lineCharts[0],volCon.get(sample1),sample1.getName(),"sample1");


        setLineChart(lineCharts[1],volCon.get(sample2),sample2.getName(),"sample2");
//
//
        setLineChart(lineCharts[2],volCon.get(sample3),sample3.getName(),"sample3");
//
//
        setLineChart(lineCharts[3],volCon.get(sample4),sample4.getName(),"sample4");
    }

    private String calculateSlop(List<Entry> entries,List<String> con,String name)
    {

        double s,r,xAverage=0.0,yAverage=0.0;
        int size=entries.size();
        if(size<=1)
        {
            return name;
        }
        //平均
        for(int i=0;i<size;i++)
        {
            xAverage=xAverage+Double.valueOf(con.get(i));
            yAverage=yAverage+entries.get(i).getY();
        }
        xAverage=xAverage/size;
        yAverage=yAverage/size;
        //斜率
        double xDiff,yDiff,xyTotal=0.0,xTotal=0.0,yTotal=0.0;
        for(int i=0;i<size;i++)
        {
            xDiff=Double.valueOf(con.get(i))-xAverage;
            yDiff=entries.get(i).getY()-yAverage;
            xyTotal=xyTotal+xDiff*yDiff;
            xTotal=xTotal+xDiff*xDiff;
            yTotal=yTotal+yDiff*yDiff;
        }
        s=xyTotal/xTotal;
        r=xyTotal/(Math.sqrt(xTotal)*Math.sqrt(yTotal));
        r=Math.sqrt(r);
        r=r*100;
        return name+"  "+((int)s)+"mV/pH"+"  "+((int)r)+"%";
    }



    private void setLineChart(LineChart lineChart, HashMap<String,List<Solution>> solutions, String name,String s) {
        String describe;
        List<Entry> entries = new ArrayList<Entry>();
        size=solutions.size();
        if(size<=0)
        {
            return;
        }


        int i=0;
        final List<String> ionType=new ArrayList<>();
        for(String con:solutions.keySet())
        {
            int average=0;
            List<Solution> ss=solutions.get(con);
            int size=ss.size();
            for(Solution solution:ss)
            {
                average=average+solution.getVoltage();
            }
            average=average/size;
            entries.add(new Entry(i,average));
            ionType.add(con);
            i++;
        }

        describe=calculateSlop(entries,ionType,s);

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
                    return ionType.get(index);
                } catch (Exception e) {
                    return " ";
                }
            }
        });


        lineChart.setDescription(description(describe));
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
                return DoubleToInt(value)+"(mv)";
            }
        });
        Legend l = lineChart.getLegend();
        l.setFormSize(12f);
        l.setTextColor(Color.parseColor("#000000"));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }


}