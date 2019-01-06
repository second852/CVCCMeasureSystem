package com.whc.cvccmeasuresystem.Common;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.github.mikephil.charting.components.Description;
import com.google.gson.Gson;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.PagConDB;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Common {
    public static String[] mainFunctionName = {"BatchStep1", "Ion channels", "Sensitivity", "Drift", "Hysteresis", "History"};
    public static int[] mainFunctionImage = {R.drawable.batch, R.drawable.ionchannels, R.drawable.sensitivity, R.drawable.drift, R.drawable.hysteresis, R.drawable.history};
    public static final String needName = "Please input this name";
    public static final String userShare = "CVCCUser";
    public static final String userId = "userId";
    public static SimpleDateFormat timeToString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    public static SimpleDateFormat timeToSheet = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss ");
    public static final String sample1String = "sample1";
    public static final String sample2String = "sample2";
    public static final String sample3String = "sample3";
    public static final String sample4String = "sample4";
    public static final String needIon = "Please input this concentration";
    public static final String needInt = "Please input this number";
    public static final String needData = "Please input this data";
    public static final String measureStartNotExist = "Measuring Now! Please pressure \"Stop\" !";
    public static final String CFName = "ChoiceFunction";
    public static final String BS1 = "BatchStep1";
    public static final String BS2 = "BatchStep2Main";

    public static final String Sen1 = "SensitivityStep1";
    public static final String Sen2 = "SensitivityStep2Main";

    public static final String Hys1 = "HysteresisStep1";
    public static final String Hys2 = "HysteresisStep2Main";

    public static final String Drift1 = "Drift";
    public static final String Drift2Set = "Drift2Set";

    public static final String reBack = "reBack";
    public static final String IonChannel1 = "IonChannel1";
    public static final String IonChannel1Set = "IonChannel1";
    public static final String IonChannel2Set = "IonChannel2Set";
    public static final String IonChannel3Set = "IonChannel3Set";

    public static final String HistoryMain = "HistoryMain";

    public static final String finalFragment = "finalFragment";
    public static final String endMeasure = "endMeasure";
    public static final String finalPage = "finalPage";
    public static final String onPause = "onPause";
    public static final String endModule = "endModule";
    public static final String finalPage1 = "finalPage1";
    public static final String finalPage2 = "finalPage2";
    //measure constant
    public static Sample sample1, sample2, sample3, sample4;
    public static HashMap<Sample, List<Solution>> dataMap;
    public static boolean startMeasure;
    public static Solution solution1, solution2, solution3, solution4;
    public static int currentPage;
    public static List<Sample> samples;
    public static int measureTimes;
    public static HashMap<Sample, HashMap<String, List<Solution>>> volCon;

    public static List<Integer> choiceColor;
    public static String[] arrayColor = {"#007bff", "#28a745", "#fd7e14", "#ffc107", "#dc3545"};
    public static int oneColor;

    public static int indicateColor;
    public static PageCon pageCon;
    public static List<String> oldFragment;
    public static boolean needSet;
    public static DecimalFormat nf = new DecimalFormat("#,##0.00");
    public static List<String> errorSample;

    public static HashMap<String, String> MeasureType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("0", "BatchStep");
        hashMap.put("1", "Ion channels");
        hashMap.put("2", "Sensitivity");
        hashMap.put("3", "Drift");
        hashMap.put("4", "Hysteresis");
        return hashMap;
    }


    public static HashMap<String, String> pageConStep() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("0", "BatchStep");
        hashMap.put("1", "Ion channels step2");
        hashMap.put("11", "Ion channels step3");
        hashMap.put("2", "Sensitivity");
        hashMap.put("3", "Drift");
        hashMap.put("4", "Hysteresis");
        return hashMap;
    }


    public static void clossKeyword(Activity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(INPUT_METHOD_SERVICE);
        View view = context.getWindow().getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String doubleRemoveZero(String s) {
        double d = Double.valueOf(s);
        int a = (int) d;
        if (a == d) {
            return String.valueOf(a);
        } else {
            return String.valueOf(Common.nf.format(d));
        }
    }

    public static String calculateCon(Sample sample, Integer Voltage) {
        Double b, a, con;
        b = Double.valueOf(sample.getIntercept());
        a = (Double.valueOf(sample.getSlope()));
        con = (Voltage - b) / a;
        return String.valueOf(con);
    }


    public static Description description(String name) {
        Description description = new Description();
        description.setText(name);
        description.setTextSize(14f);
        description.setTextAlign(Paint.Align.LEFT);
        description.setPosition(250, 140);
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
        fragmentTransaction.replace(R.id.body, fragment);
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

    public static List<BootstrapText> loopList(Activity activity) {
        List<BootstrapText> bootstrapTexts = new ArrayList<>();
        bootstrapTexts.add(createBootstrapText(activity, "Loop1"));
        bootstrapTexts.add(createBootstrapText(activity, "Loop2"));
        bootstrapTexts.add(createBootstrapText(activity, "Loop3"));
        bootstrapTexts.add(createBootstrapText(activity, "Loop4"));
        bootstrapTexts.add(createBootstrapText(activity, "Loop5"));
        return bootstrapTexts;
    }

    public static BootstrapText createBootstrapText(Activity activity, String s) {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(s)
                .build();
        return text;
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


    //error true
    public static String checkViewInteger(BootstrapEditText view, String s) {
        //過濾是否空白
        if (s == null) {
            view.setError(needIon);
            return null;
        }
        s = s.trim();
        if (s.length() <= 0) {
            view.setError(needIon);
            return null;
        }
        //過濾 數字
        try {
            new Double(s);
        } catch (Exception e) {
            view.setError(needInt);
            return null;
        }
        return s;
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    public static void setSample(SharedPreferences sharedPreferences, Activity activity, DataBase dataBase) {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        choiceColor = new ArrayList<>();
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);

        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);


        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, new ArrayList<Solution>());
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, new ArrayList<Solution>());
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, new ArrayList<Solution>());
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, new ArrayList<Solution>());
        samples.add(sample4);
    }


    public static void setMeasureSample(SharedPreferences sharedPreferences, Activity activity, DataBase dataBase) {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        choiceColor = new ArrayList<>();

        dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);
        SolutionDB solutionDB = new SolutionDB(dataBase);
        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, solutionDB.getSampleAll(sampleID));
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, solutionDB.getSampleAll(sampleID));
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, solutionDB.getSampleAll(sampleID));
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, solutionDB.getSampleAll(sampleID));
        samples.add(sample4);
        for (Solution solution : dataMap.get(sample1)) {
            choiceColor.add(solution.getColor());
        }
    }


    public static void setIonMeasureSample(SharedPreferences sharedPreferences, Activity activity, String measureType) {
        dataMap = new HashMap<>();
        samples = new ArrayList<>();
        choiceColor = new ArrayList<>();

        DataBase dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);
        SolutionDB solutionDB = new SolutionDB(dataBase);
        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample1, solutionDB.getSampleAByIdByMeasureType(sampleID, measureType));
        samples.add(sample1);
        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2String, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample2, solutionDB.getSampleAByIdByMeasureType(sampleID, measureType));
        samples.add(sample2);
        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3String, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample3, solutionDB.getSampleAByIdByMeasureType(sampleID, measureType));
        samples.add(sample3);
        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4String, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        dataMap.put(sample4, solutionDB.getSampleAByIdByMeasureType(sampleID, measureType));
        samples.add(sample4);
        for (Solution solution : dataMap.get(sample1)) {
            choiceColor.add(solution.getColor());
        }
    }


    public static List<PageCon> getPagCon(SharedPreferences sharedPreferences, Activity activity, String step) {
        DataBase dataBase = new DataBase(activity);
        SampleDB sampleDB = new SampleDB(dataBase);
        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1String, 0);
        Sample sample = sampleDB.findOldSample(sampleID);

        PagConDB pagConDB = new PagConDB(dataBase);
        return pagConDB.getStepPagCon(step, sample.getFileID());
    }


    public static void savePageParameter(SharedPreferences sharedPreferences, PageCon pageCon) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pageCon);
        prefsEditor.putString(finalPage, json);
        prefsEditor.apply();
    }

    public static void savePageParameter(SharedPreferences sharedPreferences, PageCon pageCon, String key) {

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pageCon);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }


}
