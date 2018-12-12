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
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.List;


public class FileAdapter extends BaseAdapter {

    private Context context;
    private List<SaveFile> saveFiles;

    public FileAdapter(Context context, List<SaveFile> saveFiles) {
        this.context = context;
        this.saveFiles = saveFiles;
    }

    @Override
    public int getCount() {
        return saveFiles.size();
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
        TextView name1 = itemView.findViewById(R.id.name1);
        TextView slope1 = itemView.findViewById(R.id.slope1);
        TextView name2 = itemView.findViewById(R.id.name2);
        TextView slope2 = itemView.findViewById(R.id.slope2);
        Drawable drawable;
        if (position == 0) {
            drawable = context.getResources().getDrawable(R.drawable.show_date_model_1);
            name1.setText("User");
            slope1.setText("Type");
            name2.setText("StartTime");
            slope2.setText("EndTime");
        } else {
            SaveFile saveFile = saveFiles.get(position);
            drawable = context.getResources().getDrawable(R.drawable.show_date_model_2);
        }
        name1.setBackground(drawable);
        slope1.setBackground(drawable);
        name2.setBackground(drawable);
        slope2.setBackground(drawable);
        return itemView;
    }
}
