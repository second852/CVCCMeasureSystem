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

import java.util.Date;
import java.util.List;

public class SolutionAdapter extends BaseAdapter {

    private Context context;
    private List<Solution> solutions;
    private DataBase dataBase;
    private SampleDB sampleDB;

    public SolutionAdapter(Context context, List<Solution> solutions) {
        this.context = context;
        this.solutions = solutions;
        dataBase=new DataBase(context);
        sampleDB=new SampleDB(dataBase.getReadableDatabase());
    }

    @Override
    public int getCount() {
        return solutions.size();
    }

    @Override
    public Object getItem(int i) {
        return solutions.get(i);
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
        TextView sampleName=itemView.findViewById(R.id.sampleName);
        TextView sampleIon=itemView.findViewById(R.id.sampleIon);
        TextView sampleIVol=itemView.findViewById(R.id.sampleIVol);
        TextView dateTime=itemView.findViewById(R.id.dateTime);
        Drawable drawable;
        if(position==0)
        {
            drawable=context.getResources().getDrawable(R.drawable.show_date_model_1);
            times.setText("Time");
            sampleName.setText("Name");
            sampleIon.setText("Ion");
            sampleIVol.setText("mV");
            dateTime.setText("DateTime");
        }else {
            drawable=context.getResources().getDrawable(R.drawable.show_date_model_2);
            Solution solution=solutions.get(position);
            Sample sample=sampleDB.findOldSample(solution.getSampleID());
            times.setText(solution.getNumber());
            sampleName.setText(sample.getName());
            sampleIon.setText(sample.getIonType()+solution.getConcentration());
            sampleIVol.setText(String.valueOf(solution.getVoltage()));
            dateTime.setText(Common.timeToString.format(new Date(solution.getTime().getTime())));
        }
        times.setBackground(drawable);
        sampleName.setBackground(drawable);
        sampleIon.setBackground(drawable);
        sampleIVol.setBackground(drawable);
        dateTime.setBackground(drawable);
        return itemView;
    }
}
