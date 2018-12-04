package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.R;

import java.util.List;

public class Batch extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapEditText firstName, secondName, thirdName, fourthName;
    private BootstrapDropDown firstType, secondType, thirdType, fourthType;
    private BootstrapButton next;
    private List<BootstrapText> bootstrapTexts;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("Batch Monitor");
        bootstrapTexts = Common.SolutionTypeBS(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.batch_main, container, false);
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
        next.setOnClickListener(new nextStep());
    }

    private void setDropDownClick(BootstrapDropDown bootstrapDropDown)
    {
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

        }
    }
}
