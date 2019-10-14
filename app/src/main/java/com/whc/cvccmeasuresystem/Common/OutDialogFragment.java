package com.whc.cvccmeasuresystem.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.whc.cvccmeasuresystem.Control.MainActivity;

import static com.whc.cvccmeasuresystem.Common.Common.userShare;


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
        String title="Do you want to exist?";

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
                SharedPreferences sharedPreferences=mainActivity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Common.endModule,true).apply();
                mainActivity.finish();
                System.exit(0);
                break;
            default:
                dialog.cancel();
                break;
        }
    }
}
