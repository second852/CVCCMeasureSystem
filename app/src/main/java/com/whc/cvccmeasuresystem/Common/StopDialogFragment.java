package com.whc.cvccmeasuresystem.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Set;
import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Set;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Set;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Set;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Set;

import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;


/**
 * Created by Wang on 2018/1/3.
 */

public class StopDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {


    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = "";
        String title = "Do you want to stop measuring?";

        return new AlertDialog.Builder(getActivity())
                .setTitle(Html.fromHtml(title))
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
                if (object instanceof BatchStep2Set) {
                    BatchStep2Main.handlerMessage.sendEmptyMessage(2);
                } else if (object instanceof SensitivityStep2Set) {
                    SensitivityStep2Main.handlerMessage.sendEmptyMessage(2);
                } else if (object instanceof HysteresisStep2Set) {
                    HysteresisStep2Main.handlerMessage.sendEmptyMessage(2);
                } else if (object instanceof DriftStep2Set) {
                    DriftStep2Main.handlerMessage.sendEmptyMessage(2);
                } else if (object instanceof IonChannelStep2Set) {
                    IonChannelStep2Main.handlerMessage.sendEmptyMessage(2);
                } else if (object instanceof IonChannelStep3Set) {
                    IonChannelStep3Main.handlerMessage.sendEmptyMessage(2);
                }else if(object instanceof HumidityMain)
                {
                    HumidityMain humidityMain= (HumidityMain) object;
                    startMeasure=false;
                    humidityMain.con.setText("break");
                    humidityMain.con.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                    humidityMain.stopMeasure();
                }
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}
