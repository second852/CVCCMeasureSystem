package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.R;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class IonChannelStep1 extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapEditText firstName, secondName, thirdName, fourthName;
    private BootstrapDropDown firstType, secondType, thirdType, fourthType;
    private BootstrapButton next;
    private List<BootstrapText> bootstrapTexts;
    private SharedPreferences sharedPreferences;
    private Sample sample1,sample2,sample3,sample4;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("Ion Channel Monitor Step1");
        bootstrapTexts = Common.SolutionTypeBS(activity);
        ((AppCompatActivity)activity).getSupportActionBar().show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ion_channel_step1, container, false);
        sharedPreferences= activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        findViewById();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        setDropDownClick(firstType);
        setDropDownClick(secondType);
        setDropDownClick(thirdType);
        setDropDownClick(fourthType);
        if(needSet)
        {
            setFileName();
        }
        next.setOnClickListener(new nextStep());
    }

    private void setFileName() {
        DataBase dataBase=new DataBase(activity);
        SampleDB sampleDB=new SampleDB(dataBase);
        //sample 1
        int sampleId=sharedPreferences.getInt(sample1String,0);
        sample1=sampleDB.findOldSample(sampleId);
        if(sample1!=null)
        {
            firstName.setText(sample1.getName());
            firstType.setText(sample1.getIonType());
        }
        //sample 2
        sampleId=sharedPreferences.getInt(sample2String,0);
        sample2=sampleDB.findOldSample(sampleId);
        if(sample2!=null)
        {
            secondName.setText(sample2.getName());
            secondType.setText(sample2.getIonType());
        }
        //sample 3
        sampleId=sharedPreferences.getInt(sample3String,0);
        sample3=sampleDB.findOldSample(sampleId);
        if(sample3!=null)
        {
            thirdName.setText(sample3.getName());
            thirdType.setText(sample3.getIonType());
        }
        //sample 4
        sampleId=sharedPreferences.getInt(sample4String,0);
        sample4=sampleDB.findOldSample(sampleId);
        if(sample4!=null)
        {
            fourthName.setText(sample4.getName());
            fourthType.setText(sample4.getIonType());
        }
    }

    private void setDropDownClick(BootstrapDropDown bootstrapDropDown) {
        choiceSolutionType choiceSolutionType = new choiceSolutionType();
        choiceSolutionType.setBootstrapDropDown(bootstrapDropDown);
        bootstrapDropDown.setOnDropDownItemClickListener(choiceSolutionType);
    }


    private void findViewById() {
        firstName = view.findViewById(R.id.firstName);
        secondName = view.findViewById(R.id.secondName);
        thirdName = view.findViewById(R.id.thirdName);
        fourthName = view.findViewById(R.id.fourthName);
        firstType = view.findViewById(R.id.firstType);
        secondType = view.findViewById(R.id.secondType);
        thirdType = view.findViewById(R.id.thirdType);
        fourthType = view.findViewById(R.id.fourthType);
        next = view.findViewById(R.id.next);
    }

    private class choiceSolutionType implements BootstrapDropDown.OnDropDownItemClickListener {

        private BootstrapDropDown bootstrapDropDown;


        public void setBootstrapDropDown(BootstrapDropDown bootstrapDropDown) {
            this.bootstrapDropDown = bootstrapDropDown;
        }

        @Override
        public void onItemClick(ViewGroup parent, View v, int id) {
            bootstrapDropDown.setText(bootstrapTexts.get(id));
        }
    }

    private class nextStep implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String getFirstName = firstName.getText().toString();
            String getSecondName = secondName.getText().toString();
            String getThirdName = thirdName.getText().toString();
            String getFourthName = fourthName.getText().toString();

            //first
            if (getFirstName == null) {
                firstName.setError(needName);
                return;
            }
            getFirstName=getFirstName.trim();
            if (getFirstName.length() <= 0) {
                firstName.setError(needName);
                return;
            }

            //second
            if (getSecondName == null) {
                secondName.setError(needName);
                return;
            }
            getSecondName=getSecondName.trim();
            if (getSecondName.length() <= 0) {
                secondName.setError(needName);
                return;
            }

            //third
            if (getThirdName == null) {
                thirdName.setError(needName);
                return;
            }
            getThirdName=getThirdName.trim();
            if (getThirdName.length() <= 0) {
                thirdName.setError(needName);
                return;
            }

            //fourth
            if (getFourthName == null) {
                fourthName.setError(needName);
                return;
            }
            getFourthName=getFourthName.trim();
            if (getFourthName.length() <= 0) {
                fourthName.setError(needName);
                return;
            }


            DataBase dataBase=new DataBase(activity);
            SaveFileDB saveFileDB=new SaveFileDB(dataBase);
            SampleDB sampleDB=new SampleDB(dataBase);

            if(needSet)
            {
                //update
                update(sample1,getFirstName,firstType.getText().toString(),sampleDB);
                update(sample2,getSecondName,secondType.getText().toString(),sampleDB);
                update(sample3,getThirdName,thirdType.getText().toString(),sampleDB);
                update(sample4,getFourthName,firstType.getText().toString(),sampleDB);

            }else {
                //insert File
                int useId=sharedPreferences.getInt(Common.userId,0);
                SaveFile saveFile=new SaveFile();
                saveFile.setMeasureType("1");
                saveFile.setName(Common.timeToString.format(new Date(System.currentTimeMillis())));
                saveFile.setStatTime(new Timestamp(System.currentTimeMillis()));
                saveFile.setEndTime(new Timestamp(System.currentTimeMillis()));
                saveFile.setUserId(useId);
                saveFileDB.insert(saveFile);
                SaveFile nowFile=saveFileDB.findOldSaveFile(saveFile.getName());
                //insert
                //Sample 1
                insert(sample1String,getFirstName,firstType.getText().toString(),nowFile.getID(),sampleDB);
                //Sample 2
                insert(sample2String,getSecondName,secondType.getText().toString(),nowFile.getID(),sampleDB);
                //Sample 3
                insert(sample3String,getThirdName,thirdType.getText().toString(),nowFile.getID(),sampleDB);
                //Sample 4
                insert(sample4String,getFourthName,fourthType.getText().toString(),nowFile.getID(),sampleDB);
            }
            Fragment fragment=new IonChannelStep2Main();
            Bundle bundle=new Bundle();
            bundle.putSerializable(Common.reBack,false);
            fragment.setArguments(bundle);
            switchFragment(new IonChannelStep2Main(),getFragmentManager());
            oldFragment.add(IonChannel1);
            startMeasure=false;
            indicateColor=0;
        }
    }


    public void update(Sample sample,String name,String measureType,SampleDB sampleDB)
    {
        sample.setName(name);
        sample.setIonType(measureType);
        sampleDB.update(sample);
    }



    public void insert(String saveName,String name,String measureType,int fileID,SampleDB sampleDB)
    {
        Sample sample=new Sample();
        sample.setName(name);
        sample.setIonType(measureType);
        sample.setFileID(fileID);
        sample.setLocation(saveName);
        sampleDB.insert(sample);
        Sample nowSample=sampleDB.findOldSample(name,fileID,saveName);
        sharedPreferences.edit().putInt(saveName,nowSample.getID()).apply();
    }

}
