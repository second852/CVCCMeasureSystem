package com.whc.cvccmeasuresystem.Control.History;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.R;



public class HistoryMain extends Fragment{

    private Activity activity;
    private View view,setDateView;
    private BootstrapEditText dateBegin,dateEnd;
    private LinearLayout showDate;
    private DatePicker datePicker;
    private String choiceDate;
    private ListView list;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("History");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_main, container, false);
        findViewById();
        setOnClick();
        return view;
    }

    private void setOnClick() {
        dateBegin.setOnClickListener(new showDate());
        dateEnd.setOnClickListener(new showDate());
        showDate.setOnClickListener(new choiceDateClick());
        dateBegin.setShowSoftInputOnFocus(false);
        dateEnd.setShowSoftInputOnFocus(false);
    }

    private void findViewById() {

        dateBegin=view.findViewById(R.id.dateBegin);
        dateEnd=view.findViewById(R.id.dateEnd);
        showDate=view.findViewById(R.id.showDate);
        datePicker=view.findViewById(R.id.datePicker);
        list=view.findViewById(R.id.list);
    }

    private class showDate implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setDateView=view;
            showDate.setVisibility(View.VISIBLE);
        }
    }

    private class choiceDateClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            choiceDate=datePicker.getYear()+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth();
            EditText editText= (EditText) setDateView;
            editText.setText(choiceDate);
            showDate.setVisibility(View.GONE);
            editText.setSelection(choiceDate.length());
        }
    }
}
