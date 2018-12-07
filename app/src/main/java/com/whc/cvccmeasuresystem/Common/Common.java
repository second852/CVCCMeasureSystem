package com.whc.cvccmeasuresystem.Common;


import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.github.mikephil.charting.components.Description;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.SimpleFormatter;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALCULATOR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_SIGN_IN;

public class Common {
    public static String[] mainFunctionName = {"BatchStep1", "Ion channels", "Sensitivity", "Drift", "Hysteresis", "History"};
    public static int[] mainFunctionImage = {R.drawable.batch, R.drawable.ionchannels, R.drawable.sensitivity, R.drawable.drift, R.drawable.hysteresis, R.drawable.history};
    public static final String needName = "Please input this name";
    public static final String userShare = "CVCCUser";
    public static final String userId = "userId";
    public static SimpleDateFormat timeToString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    public static final String sample1String = "sample1";
    public static final String sample2String = "sample2";
    public static final String sample3String= "sample3";
    public static final String sample4String = "sample4";
    public static final String needIon = "Please input this concentration";
    public static final String needInt = "Please input this number";
    public static final String meaureStartNotExist = "Measuring Now! Please pressure \"Stop\" !";
    public static final String fileNotSave = "File don't save! Please pressure \"Finish\" !";

    //measure constant
    public static Sample sample1, sample2, sample3, sample4;
    public static HashMap<Sample,List<Solution>> dataMap;
    public static boolean startMeasure;
    public static Solution solution1,solution2,solution3,solution4;
    public static int currentPage;
    public static List<Sample> samples;
    public static int measureTimes;
    public static TCPClient tcpClient;
    public static boolean finishToSave;








    public static HashMap<String, String> MeasureType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("0", "BatchStep1");
        hashMap.put("1", "Ion channels");
        hashMap.put("2", "Sensitivity");
        hashMap.put("3", "Drift");
        hashMap.put("4", "Hysteresis");
        return hashMap;
    }



    public static Description description(String name) {
        Description description= new Description();
        description.setText(name);
        description.setTextSize(14f);
        description.setTextAlign(Paint.Align.LEFT);
        description.setPosition(250,140);
        return description;
    }

    public static LinkedHashMap<String, String> SolutionType() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("0", "pH");
        hashMap.put("1", "pK");
        hashMap.put("2", "pCa");
        hashMap.put("3", "pCl");
        hashMap.put("4", "X");
        return hashMap;
    }

    public static void switchFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment f : fragmentManager.getFragments()) {
            fragmentTransaction.remove(f);
        }
        fragmentTransaction.add(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    public static int DoubleToInt(double a) {
        double b = new BigDecimal(a)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return (int) b;
    }

    public static List<BootstrapText> SolutionTypeBS(Activity activity) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        for (String s : SolutionType().keySet()) {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(SolutionType().get(s))
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    //error true
    public static boolean checkViewIon(BootstrapEditText view, String s) {
        //過濾是否空白
        if (s == null) {
            view.setError(needIon);
            return true;
        }
        s = s.trim();
        if (s.length() <= 0) {
            view.setError(needIon);
            return true;
        }

        //過濾 數字
        try {
            new Double(s);
        } catch (Exception e) {
            view.setError(needInt);
            return true;
        }
        return false;
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

}
