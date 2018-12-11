package com.whc.cvccmeasuresystem.Common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.List;


public class SolutionAdapterSlope extends BaseAdapter {

    private Context context;
    public SampleDB sampleDB;
    private Integer[] index;

    public SolutionAdapterSlope(Context context, Integer[] index) {
        this.context = context;
        this.index = index;
        sampleDB=new SampleDB(new DataBase(context));
    }

    @Override
    public int getCount() {
        return index.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View itemView, final ViewGroup parent) {
        if (itemView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.data_adapter_item1, parent, false);
        }
        TextView name1=itemView.findViewById(R.id.name1);
        TextView slope1=itemView.findViewById(R.id.slope1);
        TextView name2=itemView.findViewById(R.id.name2);
        TextView slope2=itemView.findViewById(R.id.slope2);
        Drawable drawable;
        if(position==0)
        {
            drawable=context.getResources().getDrawable(R.drawable.show_date_model_1);
            name1.setText("Name");
            slope1.setText("Slope");
            name2.setText("Name");
            slope2.setText("Slope");
        }else {
            int index=this.index[position];
            drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
            switch (index)
            {
                case 0:
                    name1.setText(Common.sample1.getName());
                    slope1.setText(calculateSlop(Common.sample1));
                    name2.setText(Common.sample2.getName());
                    slope2.setText(calculateSlop(Common.sample2));
                    break;
                case 1:
                    name1.setText(Common.sample3.getName());
                    slope1.setText(calculateSlop(Common.sample3));
                    name2.setText(Common.sample4.getName());
                    slope2.setText(calculateSlop(Common.sample4));
                    break;
            }
        }
        name1.setBackground(drawable);
        slope1.setBackground(drawable);
        name2.setBackground(drawable);
        slope2.setBackground(drawable);
        return itemView;
    }

    private String calculateSlop(Sample sample)
    {
        List<Solution> solutions=Common.dataMap.get(sample);
        double s,r,xAverage=0.0,yAverage=0.0,b,diffX,diffY;
        int size=solutions.size();
        if(size<=1)
        {
            return "---mV/pH";
        }
        //平均
        for(int i=0;i<size;i++)
        {
            xAverage=xAverage+Double.valueOf(solutions.get(i).getConcentration());
            yAverage=yAverage+solutions.get(i).getVoltage();
        }
        xAverage=xAverage/size;
        yAverage=yAverage/size;
        //斜率
        double xDiff,yDiff,xyTotal=0.0,xTotal=0.0,yTotal=0.0;
        for(int i=0;i<size;i++)
        {
            xDiff=Double.valueOf(solutions.get(i).getConcentration())-xAverage;
            yDiff=solutions.get(i).getVoltage()-yAverage;
            xyTotal=xyTotal+xDiff*yDiff;
            xTotal=xTotal+xDiff*xDiff;
            yTotal=yTotal+yDiff*yDiff;
        }
        //斜率
        s=xyTotal/xTotal;
        sample.setSlope(String.valueOf(s));
        //線性度
        r=xyTotal/(Math.sqrt(xTotal)*Math.sqrt(yTotal));
        r=r*100;
        sample.setR(String.valueOf(r));
        //截距
        b=yAverage-s*xAverage;
        sample.setIntercept(String.valueOf(b));
        //標準差
        double diffResult=0.0;
        if(size>3)
        {
            for(int i=0;i<size;i++)
            {
                diffResult= diffResult+Math.pow(solutions.get(i).getVoltage()-(b+s*Double.valueOf(solutions.get(i).getConcentration())),2);
            }
            diffResult=diffResult/(size-2);
            diffResult=Math.sqrt(diffResult);
            sample.setStandardDeviation(String.valueOf(diffResult));
        }else {
            sample.setStandardDeviation("0");
        }
        //誤差係數X
        diffX=diffResult/(Math.sqrt(xTotal));
        sample.setDifferenceX(String.valueOf(diffX));
        //誤差係數Y
        diffY=Math.pow(xAverage,2)/xTotal;
        diffY=diffY+(double)1/size;
        System.out.println("誤差係數Y"+diffY);
        diffY=Math.pow(diffResult,2)*diffY;
        diffY=Math.sqrt(diffY);
        sample.setDifferenceY(String.valueOf(diffY));
        sample.setUnit("mV/pH");
        sampleDB.update(sample);
        return ((int)s)+"mV/pH";
    }

}
