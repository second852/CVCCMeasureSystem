package com.whc.cvccmeasuresystem.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.whc.cvccmeasuresystem.Control.MainActivity;


/**
 * Created by Wang on 2018/1/3.
 */

public class OutDialogFragment extends DialogFragment implements  DialogInterface.OnClickListener{


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
        String message="";
        String title="Do you exist?";

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
                MainActivity mainActivity= (MainActivity) object;
                mainActivity.finish();
                System.exit(0);
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}