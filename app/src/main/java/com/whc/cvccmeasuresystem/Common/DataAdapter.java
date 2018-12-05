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

import java.util.List;

public class DataAdapter extends BaseAdapter {

    private Context context;
    private List<Solution> solutions;
    private List<Sample> samples;

    public DataAdapter(Context context, List<Solution> objects) {
        this.context = context;
        this.solutions = objects;
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
            itemView = layoutInflater.inflate(R.layout.data_adapter_item, parent, false);
        }
        TextView times=itemView.findViewById(R.id.times);
        TextView sampleIon1=itemView.findViewById(R.id.sampleIon1);
        TextView sampleIVol1=itemView.findViewById(R.id.sampleIVol1);
        TextView sampleIon2=itemView.findViewById(R.id.sampleIon2);
        TextView sampleIVol2=itemView.findViewById(R.id.sampleIVol2);
        TextView sampleIon3=itemView.findViewById(R.id.sampleIon3);
        TextView sampleIVol3=itemView.findViewById(R.id.sampleIVol3);
        TextView sampleIon4=itemView.findViewById(R.id.sampleIon4);
        TextView sampleIVol4=itemView.findViewById(R.id.sampleIVol4);
        TextView dateTime=itemView.findViewById(R.id.dateTime);

        if(position==0)
        {
            times.setText("Time");
            sampleIon1.setText("pH");
            sampleIVol1.setText("S 1(mV)");
            sampleIon2.setText("pH");
            sampleIVol2.setText("S 2(mV)");
            sampleIon3.setText("pH");
            sampleIVol3.setText("S 3(mV)");
            sampleIon4.setText("pH");
            sampleIVol4.setText("S 4(mV)");
            dateTime.setText("dateTime");
        }else {

        }




        return itemView;
    }
}
