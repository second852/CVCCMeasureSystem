package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.SampleAdapter;
import com.whc.cvccmeasuresystem.Common.SolutionAdapter;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.checkViewIon;
import static com.whc.cvccmeasuresystem.Common.Common.userId;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;

public class BatchStep2Data extends Fragment{
    private View view;
    private Activity activity;
    private ListView listData;


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
        view = inflater.inflate(R.layout.batch_step2_data, container, false);
        listData=view.findViewById(R.id.listData);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setListView();
    }

    public void setListView()
    {
        listData.setAdapter(new SampleAdapter(activity,BatchStep2Main.samples,BatchStep2Main.dataMap));
    }

}
