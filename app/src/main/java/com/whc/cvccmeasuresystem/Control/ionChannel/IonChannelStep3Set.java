package com.whc.cvccmeasuresystem.Control.ionChannel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.R;



import static com.whc.cvccmeasuresystem.Common.Common.*;



public class IonChannelStep3Set extends Fragment {

    private View view;
    private Activity activity;
    private ImageView sample1I,sample2I,sample3I,sample4I;
    private AwesomeTextView sample1N,sample2N,sample3N,sample4N;
    private BootstrapEditText measureTime;
    private BootstrapButton start;
    private String mType;



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
        view = inflater.inflate(R.layout.ion_channel_step3_set, container, false);
        findViewById();
        return view;
    }






    @Override
    public void onStart() {
        super.onStart();

    }


    private void findViewById() {
        sample1I=view.findViewById(R.id.sample1I);
        sample1N=view.findViewById(R.id.sample1N);
        sample1N.setText(sample1.getName());

        sample2I=view.findViewById(R.id.sample2I);
        sample2N=view.findViewById(R.id.sample2N);
        sample2N.setText(sample2.getName());

        sample3I=view.findViewById(R.id.sample3I);
        sample3N=view.findViewById(R.id.sample3N);
        sample3N.setText(sample3.getName());

        sample4I=view.findViewById(R.id.sample4I);
        sample4N=view.findViewById(R.id.sample4N);
        sample4N.setText(sample4.getName());

        measureTime=view.findViewById(R.id.measureTime);
        start=view.findViewById(R.id.start);
        start.setOnClickListener(new startMeasureData());
    }


    private Runnable measureThread = new Runnable() {
        @Override
        public void run() {
            tcpClient = new TCPClient("1", mType, IonChannelStep3Main.handlerMessage, IonChannelStep3Set.this);
            tcpClient.run();
        }
    };


    private class startMeasureData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mType=measureTime.getText().toString();
            mType=checkViewInteger(measureTime,mType);
            if(mType==null)
            {
                return;
            }
            new Thread(measureThread).start();
        }
    }
}
