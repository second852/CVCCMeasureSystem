package com.whc.cvccmeasuresystem.Control.History;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.FileAdapter;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HistoryMain extends Fragment{

    private Activity activity;
    private View view,setDateView;
    private BootstrapEditText dateBegin,dateEnd;
    private LinearLayout showDate;
    private DatePicker datePicker;
    private String choiceDate;
    private ListView list;
    private List<SaveFile> saveFiles;
    private List<SaveFile> choicePrint;
    private SaveFileDB saveFileDB;
    private boolean allChoice;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("History");
        saveFileDB=new SaveFileDB(new DataBase(activity));
        allChoice=false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_main, container, false);
        findViewById();
        setOnClick();
        saveFiles=saveFileDB.getAll();
        saveFiles.add(0,new SaveFile());
        choicePrint=new ArrayList<>();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        setList();
    }

    private void setList()
    {
        list.setDividerHeight(0);
        list.setAdapter(new FileAdapter(activity,saveFiles));
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


    public class FileAdapter extends BaseAdapter {

        private Context context;
        private List<SaveFile> saveFiles;
        private UserDB userDB;


        public FileAdapter(Context context, List<SaveFile> saveFiles) {
            this.context = context;
            this.saveFiles = saveFiles;
            userDB=new UserDB(new DataBase(context));
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
                itemView = layoutInflater.inflate(R.layout.data_adapter_item3, parent, false);
            }
            CheckBox checkBox=itemView.findViewById(R.id.choice);
            TextView name1 = itemView.findViewById(R.id.name1);
            TextView slope1 = itemView.findViewById(R.id.slope1);
            TextView name2 = itemView.findViewById(R.id.name2);

            Drawable drawable;
            if (position == 0) {
                drawable = context.getResources().getDrawable(R.drawable.show_date_model_1);
                checkBox.setText("All");
                name1.setText("User");
                slope1.setText("Type");
                name2.setText("dateTime");
                checkBox.setChecked(allChoice);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox c= (CheckBox) view;
                        if(c.isChecked())
                        {
                            choicePrint.addAll(saveFiles);
                            allChoice=true;

                        }else{
                            choicePrint=new ArrayList<>();
                            allChoice=false;
                        }
                        HistoryMain.this.setList();
                    }
                });

            } else {
                final SaveFile saveFile = saveFiles.get(position);
                drawable = context.getResources().getDrawable(R.drawable.show_date_model_2);
                User user=userDB.findUserById(saveFile.getID());
                name1.setText(user.getName());
                slope1.setText(Common.MeasureType().get(saveFile.getMeasureType()));
                name2.setText(Common.timeToString.format(new Date(saveFile.getStatTime().getTime())));
                checkBox.setText(String.valueOf(position));
                boolean checkOne=(choicePrint.indexOf(saveFile)!=-1);
                checkBox.setChecked(checkOne);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox c= (CheckBox) view;
                        if(c.isChecked())
                        {
                            choicePrint.add(saveFile);
                        }else{
                            allChoice=false;
                        }
                    }
                });
            }
            name1.setBackground(drawable);
            slope1.setBackground(drawable);
            name2.setBackground(drawable);
            return itemView;
        }
    }
}
