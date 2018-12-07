package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionImage;
import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionName;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;

public class ChoiceFunction extends Fragment {

    private View view;
    private Activity activity;
    private GridView gridView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        ((AppCompatActivity)activity).getSupportActionBar().show();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chociefunction_main, container, false);
        activity.setTitle(R.string.app_name);
        gridView=view.findViewById(R.id.gridView);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setGridView();
    }

    private void setGridView() {
        try {
            HashMap item;
            ArrayList items = new ArrayList<Map<String, Object>>();
            for (int i=0;i<mainFunctionName.length;i++ ) {
                item = new HashMap<String, Object>();
                item.put("image",mainFunctionImage[i]);
                item.put("text", mainFunctionName[i]);
                items.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(activity, items, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            gridView.setAdapter(adapter);
            gridView.setNumColumns(2);
            gridView.setOnItemClickListener(new choiceFunction());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class choiceFunction implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Fragment fragment;
            switch (i){
                case 0:
                    fragment=new BatchStep1();
                    switchFragment(fragment,getFragmentManager());
                    break;
                case 1:
                    break;
                case 2:
                    fragment=new SensitivityStep1();
                    switchFragment(fragment,getFragmentManager());
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
            }
        }
    }

}
