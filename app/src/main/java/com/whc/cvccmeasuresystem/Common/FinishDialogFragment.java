package com.whc.cvccmeasuresystem.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Set;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Set;


/**
 * Created by Wang on 2018/1/3.
 */

public class FinishDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{


    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            FragmentTransaction ft = transaction;
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
        return super.show(transaction, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String  title="Save the records";
        String message="Do you want to Save the records?";



        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setIcon(null)
                .setMessage(message)
                .setPositiveButton("YES", this)
                .setNegativeButton("NO", this)
                .create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if(object instanceof BatchStep2Set)
                {
                   BatchStep2Set batchStep2Set= (BatchStep2Set) object;
                   batchStep2Set.finishMeasure();
                }else if(object instanceof SensitivityStep2Set)
                {
                    SensitivityStep2Set sensitivityStep2Set= (SensitivityStep2Set) object;
                    sensitivityStep2Set.finishMeasure();
                }else if(object instanceof HysteresisStep2Set)
                {
                    HysteresisStep2Set hysteresisStep2Set= (HysteresisStep2Set) object;
                    hysteresisStep2Set.finishMeasure();
                }
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}
