package com.whc.cvccmeasuresystem.Common;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALCULATOR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_SIGN_IN;

public class Common {
    public static String[] mainFunctionName={"Batch","Ion channels","Sensitivity","Drift","Hysteresis","History"};
    public static int[] mainFunctionImage = {R.drawable.batch,R.drawable.ionchannels,R.drawable.sensitivity,R.drawable.drift,R.drawable.hysteresis,R.drawable.history};


    public static HashMap<String,String> MeasureType(){
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("0","Batch");
        hashMap.put("1","Ion channels");
        hashMap.put("2","Sensitivity");
        hashMap.put("3","Drift");
        hashMap.put("4","Hysteresis");
        return  hashMap;
    }

    public static HashMap<String,String> SolutionType(){
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("0","pH");
        hashMap.put("1","pK");
        hashMap.put("2","pCa");
        hashMap.put("3","pCl");
        hashMap.put("4","X");
        return  hashMap;
    }

    public static void switchFragment(Fragment fragment,FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    public static List<BootstrapText> DateChoiceSetBsTest(Activity activity, String[] data) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : data) {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s + " ")
                    .addFontAwesomeIcon(FA_SIGN_IN)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }

}
