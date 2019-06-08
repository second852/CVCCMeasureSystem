package com.whc.cvccmeasuresystem.Control.Humidity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep1;
import com.whc.cvccmeasuresystem.Control.History.HistoryMain;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep1;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep1;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep1;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.HumidityVoltageDB;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Model.HumidityVoltage;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.whc.cvccmeasuresystem.Common.Common.CFName;
import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionImage;
import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionName;
import static com.whc.cvccmeasuresystem.Common.Common.needSet;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.pageCon;
import static com.whc.cvccmeasuresystem.Common.Common.solution1;
import static com.whc.cvccmeasuresystem.Common.Common.solution2;
import static com.whc.cvccmeasuresystem.Common.Common.solution3;
import static com.whc.cvccmeasuresystem.Common.Common.solution4;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;
import static com.whc.cvccmeasuresystem.Control.History.HistoryMain.saveFiles;


public class HumidityData extends Fragment {

    private View view;
    private Activity activity;
    private ListView listData;
    private HumidityVoltageDB humidityVoltageDB;
    private BootstrapButton backP;


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
        view = inflater.inflate(R.layout.humidity_data, container, false);
        humidityVoltageDB=new HumidityVoltageDB(new DataBase(activity));
        listData=view.findViewById(R.id.listData);
        listData.setDividerHeight(0);
        backP=view.findViewById(R.id.backP);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new HumidityMain();
                Common.switchFragment(fragment,getFragmentManager());
                Common.oldFragment.remove(Common.oldFragment.size()-1);
            }
        });

        SharedPreferences sharedPreferences  = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        int fileId=sharedPreferences.getInt(Common.fileId,0);
        List<HumidityVoltage> humidityVoltages=humidityVoltageDB.findUserByHuId(fileId);

        if(humidityVoltages.size()>0)
        {
            int lastNumber=humidityVoltages.size()-1;
            HumidityVoltage base=humidityVoltages.get(lastNumber);
            humidityVoltages.add(0,base);
            lastNumber=humidityVoltages.size()-1;
            humidityVoltages.remove(lastNumber);
        }
        humidityVoltages.add(0,new HumidityVoltage());
        listData.setAdapter(new VoltageAdapter(activity,humidityVoltages));
        return view;
    }

    public class VoltageAdapter extends BaseAdapter {

        private Context context;
        private List<HumidityVoltage> humidityVoltages;
        private int size;


        public VoltageAdapter(Context context, List<HumidityVoltage> humidityVoltages) {
            this.context = context;
            this.humidityVoltages = humidityVoltages;
            size=humidityVoltages.size();
        }

        @Override
        public int getCount() {
            return humidityVoltages.size();
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
                itemView = layoutInflater.inflate(R.layout.data_adapter_item5, parent, false);
            }
            TextView time=itemView.findViewById(R.id.time);
            TextView voltage1=itemView.findViewById(R.id.voltage1);
            TextView voltage2=itemView.findViewById(R.id.voltage2);
            TextView voltage3=itemView.findViewById(R.id.voltage3);
            TextView voltage4=itemView.findViewById(R.id.voltage4);
            String timeString,voltage1String,voltage2String,voltage3String,voltage4String;
            Drawable drawable;
            HumidityVoltage  humidityVoltage=humidityVoltages.get(position);
            switch (position) {
                case 0:
                    drawable=context.getDrawable(R.drawable.show_date_model_1);
                    timeString="Times";
                    voltage1String="S1(mV)";
                    voltage2String="S2(mV)";
                    voltage3String="S3(mV)";
                    voltage4String="S4(mV)";
                    break;
                case 1:
                    drawable=context.getDrawable(R.drawable.show_date_model_3);
                    timeString="base";
                    voltage1String=String.valueOf(humidityVoltage.getVoltage1());
                    voltage2String=String.valueOf(humidityVoltage.getVoltage2());
                    voltage3String=String.valueOf(humidityVoltage.getVoltage3());
                    voltage4String=String.valueOf(humidityVoltage.getVoltage4());
                    break;
                default:
                    drawable=context.getDrawable(R.drawable.show_date_model_2);
                    timeString=String.valueOf(size-position);
                    voltage1String=String.valueOf(humidityVoltage.getVoltage1());
                    voltage2String=String.valueOf(humidityVoltage.getVoltage2());
                    voltage3String=String.valueOf(humidityVoltage.getVoltage3());
                    voltage4String=String.valueOf(humidityVoltage.getVoltage4());
                    break;
            }

            time.setText(timeString);
            voltage1.setText(voltage1String);
            voltage2.setText(voltage2String);
            voltage3.setText(voltage3String);
            voltage4.setText(voltage4String);

            time.setBackground(drawable);
            voltage1.setBackground(drawable);
            voltage2.setBackground(drawable);
            voltage3.setBackground(drawable);
            voltage4.setBackground(drawable);
           return itemView;
        }
    }

}
