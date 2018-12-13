package com.whc.cvccmeasuresystem.Control.History;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.FileAdapter;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.needData;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;


public class HistoryMain extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private View view, setDateView;
    private BootstrapEditText dateBegin, dateEnd;
    private LinearLayout showDate;
    private DatePicker datePicker;
    private String choiceDate;
    private ListView list;
    private SaveFileDB saveFileDB;
    private boolean allChoice;
    private ImageView search;
    private RelativeLayout progressL;

    private static List<SaveFile> saveFiles;
    private static List<SaveFile> choicePrint;
    public static SaveFile showFileDate;
    private GoogleApiClient mGoogleApiClient;
    private boolean firstEnter;
    private BootstrapButton export;
    private UserDB userDB;
    private static PageCon pageCon;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("History");
        saveFileDB = new SaveFileDB(new DataBase(activity));
        userDB=new UserDB(new DataBase(activity));
        allChoice = false;
    }

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                 progressL.setVisibility(View.VISIBLE);
                 Common.showToast(activity,"Uploading now!");
                    break;
                case 1:
                    progressL.setVisibility(View.GONE);
                    Common.showToast(activity,"Uploading is complete!");
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_main, container, false);
        findViewById();
        setOnClick();
        if (saveFiles == null) {
            saveFiles = saveFileDB.getAll();
            saveFiles.add(0, new SaveFile());
            choicePrint = new ArrayList<>();
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        setList();
        setPageCon();
    }

    private void setPageCon() {
        if(pageCon!=null)
        {
            if(pageCon.getCon1()!=null)
            {
                dateBegin.setText(pageCon.getCon1());
            }
            if(pageCon.getCon2()!=null)
            {
                dateEnd.setText(pageCon.getCon2());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageCon=new PageCon();
        pageCon.setCon1(dateBegin.getText().toString());
        pageCon.setCon2(dateEnd.getText().toString());
    }

    private void setList() {
        list.setDividerHeight(0);
        list.setAdapter(new FileAdapter(activity, saveFiles));
    }


    private void setOnClick() {
        dateBegin.setOnClickListener(new showDate());
        dateEnd.setOnClickListener(new showDate());
        showDate.setOnClickListener(new choiceDateClick());
        dateBegin.setShowSoftInputOnFocus(false);
        dateEnd.setShowSoftInputOnFocus(false);
        search.setOnClickListener(new searchNewData());
        export.setOnClickListener(new openCloudy());
    }

    private void findViewById() {

        dateBegin = view.findViewById(R.id.dateBegin);
        dateEnd = view.findViewById(R.id.dateEnd);
        showDate = view.findViewById(R.id.showDate);
        datePicker = view.findViewById(R.id.datePicker);
        list = view.findViewById(R.id.list);
        search = view.findViewById(R.id.search);
        progressL = view.findViewById(R.id.progressL);
        export = view.findViewById(R.id.export);
    }



    private class showDate implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setDateView = view;
            showDate.setVisibility(View.VISIBLE);
        }
    }

    private class choiceDateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            choiceDate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
            EditText editText = (EditText) setDateView;
            editText.setText(choiceDate);
            showDate.setVisibility(View.GONE);
            editText.setSelection(choiceDate.length());
            editText.setError(null);
        }
    }

    private CheckBox checkBoxFirst;
    public class FileAdapter extends BaseAdapter {

        private Context context;
        private List<SaveFile> saveFiles;



        public FileAdapter(Context context, List<SaveFile> saveFiles) {
            this.context = context;
            this.saveFiles = saveFiles;
        }

        @Override
        public int getCount() {
            return saveFiles.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.data_adapter_item3, parent, false);
            }
            CheckBox checkBox = itemView.findViewById(R.id.choice);
            TextView name1 = itemView.findViewById(R.id.name1);
            TextView slope1 = itemView.findViewById(R.id.slope1);
            TextView name2 = itemView.findViewById(R.id.name2);

            Drawable drawable;
            if (position == 0) {
                checkBoxFirst=checkBox;
                drawable = context.getResources().getDrawable(R.drawable.show_date_model_1);
                checkBox.setText("All");
                name1.setText("User");
                slope1.setText("Type");
                name2.setText("Datetime");
                checkBox.setChecked(allChoice);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox c = (CheckBox) view;
                        if (c.isChecked()) {
                            choicePrint.addAll(saveFiles);
                            choicePrint.remove(0);
                            allChoice = true;
                        } else {
                            choicePrint = new ArrayList<>();
                            allChoice = false;
                        }
                        HistoryMain.this.setList();
                    }
                });

            } else {
                final SaveFile saveFile = saveFiles.get(position);
                drawable = context.getResources().getDrawable(R.drawable.show_date_model_2);
                User user = userDB.findUserById(saveFile.getID());
                name1.setText(user.getName());
                slope1.setText(Common.MeasureType().get(saveFile.getMeasureType()));
                name2.setText(Common.timeToString.format(new Date(saveFile.getStatTime().getTime())));
                checkBox.setText(String.valueOf(position));
                boolean checkOne = (choicePrint.indexOf(saveFile) != -1);
                checkBox.setChecked(checkOne);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox c = (CheckBox) view;
                        if (c.isChecked()) {
                            choicePrint.add(saveFile);
                        } else {
                            allChoice = false;
                            checkBoxFirst.setChecked(false);
                            for(int i=0;i<choicePrint.size();i++)
                            {
                                if(saveFile.equals(choicePrint.get(i)))
                                {
                                    choicePrint.remove(i);
                                }
                            }
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFileDate = saveFile;
                        switchFragment(new HistoryShowMain(), getFragmentManager());
                        oldFragment.add(Common.HistoryMain);
                    }
                });

            }
            checkBox.setBackground(drawable);
            name1.setBackground(drawable);
            slope1.setBackground(drawable);
            name2.setBackground(drawable);
            return itemView;
        }
    }

    private class searchNewData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String begin = dateBegin.getText().toString();
            String end = dateEnd.getText().toString();

            if (begin == null) {
                dateBegin.setError(needData);
                return;
            }
            begin = begin.trim();
            if (begin.length() <= 0) {
                dateBegin.setError(needData);
                return;
            }

            if (end == null) {
                dateEnd.setError(needData);
                return;
            }
            end = end.trim();
            if (begin.length() <= 0) {
                dateEnd.setError(needData);
                return;
            }
            choicePrint = new ArrayList<>();
            String[] splitBegin = begin.split("/");
            int year = Integer.valueOf(splitBegin[0]);
            int month = Integer.valueOf(splitBegin[1]) - 1;
            int day = Integer.valueOf(splitBegin[2]);
            Calendar startCal = new GregorianCalendar(year, month, day, 0, 0, 0);
            splitBegin = end.split("/");
            year = Integer.valueOf(splitBegin[0]);
            month = Integer.valueOf(splitBegin[1]) - 1;
            day = Integer.valueOf(splitBegin[2]);
            Calendar endCal = new GregorianCalendar(year, month, day, 23, 59, 59);
            if (endCal.getTimeInMillis() < startCal.getTimeInMillis()) {
                dateBegin.setError("\"Start Time\" is bigger than \"Finish Time\" ");
                return;
            }
            saveFiles = saveFileDB.getFileByTime(startCal.getTimeInMillis(), endCal.getTimeInMillis());
            saveFiles.add(0, new SaveFile());
            setList();
        }
    }


    public void openCloud() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            Common.showToast(activity, "I can’t connect to the Internet!!");
            return;
        }
        firstEnter = true;
        progressL.setVisibility(View.VISIBLE);
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(final DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Common.showToast(activity, "I can’t connect to the Internet!");
                            return;
                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                // Otherwise, we can write our data to the new contents.
                                // Get an output stream for the contents.
                                OutputStream outputStream = result.getDriveContents().getOutputStream();
                                // Write the bitmap data from it.
                                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                                String fileName, fileType;
                                fileName = "CVCC.xls";
                                fileType = "File/xls";
                                handler.sendEmptyMessage(0);
                                outputExcel(bitmapStream);

                                try {
                                    outputStream.write(bitmapStream.toByteArray());
                                } catch (IOException e1) {

                                }
                                // Create the initial metadata - MIME type and title.
                                // Note that the user will be able to change the title later.
                                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                        .setMimeType(fileType).setTitle(fileName).build();
                                // Create an intent for the file chooser, and start it.
                                IntentSender intentSender = Drive.DriveApi
                                        .newCreateFileActivityBuilder()
                                        .setInitialMetadata(metadataChangeSet)
                                        .setInitialDriveContents(result.getDriveContents())
                                        .build(mGoogleApiClient);
                                try {
                                    activity.startIntentSenderForResult(
                                            intentSender, 3, null, 0, 0, 0);
                                } catch (IntentSender.SendIntentException e) {

                                }
                            }
                        };
                        new Thread(runnable).start();
                    }
                });
    }


    private void outputExcel(OutputStream outputStream) {
        try {
            int rowCount;
            SampleDB sampleDB=new SampleDB(new DataBase(activity));
            SolutionDB solutionDB=new SolutionDB(new DataBase(activity));
            HSSFWorkbook workbook = new HSSFWorkbook();
            int j=0;
            for(SaveFile saveFile:choicePrint)
            {
                User user=userDB.findUserById(saveFile.getUserId());
                Log.d("ZXXXXXXXXXX","ID"+saveFile.getID());
                Sheet sheetCon = workbook.createSheet(Common.timeToSheet.format(new Date(saveFile.getStatTime().getTime())));
                sheetCon.setColumnWidth(0, 10 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 10 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 20 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(3, 20* 256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 20* 256);// 調整欄位寬度
                sheetCon.setColumnWidth(5, 20* 256);// 調整欄位寬度
                sheetCon.setColumnWidth(6, 20* 256);// 調整欄位寬度

                //File
                sheetCon.addMergedRegion(new CellRangeAddress(0,0, 0, 3));
                Row row = sheetCon.createRow(0);
                row.createCell(0).setCellValue("File");
                //File title
                row = sheetCon.createRow(1);
                row.createCell(0).setCellValue("User");
                row.createCell(1).setCellValue("Type");
                row.createCell(2).setCellValue("DateTime");
                sheetCon.addMergedRegion(new CellRangeAddress(1,1, 2, 4));
                //File value
                row = sheetCon.createRow(2);
                row.createCell(0).setCellValue(user.getName());
                row.createCell(1).setCellValue(Common.MeasureType().get(saveFile.getMeasureType()));
                row.createCell(2).setCellValue(Common.timeToString.format(new Date(saveFile.getStatTime().getTime())));
                sheetCon.addMergedRegion(new CellRangeAddress(2,2, 2, 4));

                //Sample
                sheetCon.addMergedRegion(new CellRangeAddress(4,4, 0, 3));
                row = sheetCon.createRow(4);
                row.createCell(0).setCellValue("Sample");
                //Sample value
                List<Sample> samples=sampleDB.getFileSamepleAll(saveFile.getID());
                //Sample Type
                row = sheetCon.createRow(5);
                row.createCell(0).setCellValue("name");
                row.createCell(1).setCellValue("type");
                row.createCell(2).setCellValue("slope(mv/pH)");
                row.createCell(3).setCellValue("limitHighVoltage(mV)");
                row.createCell(4).setCellValue("limitLowVoltage(mV)");
                //Sample value
                rowCount=6;
                for (Sample sample:samples)
                {
                    row = sheetCon.createRow(rowCount);
                    row.createCell(0).setCellValue(sample.getName());
                    row.createCell(1).setCellValue(sample.getIonType());
                    row.createCell(2).setCellValue(checkNull(sample.getSlope()));
                    row.createCell(3).setCellValue(checkNull(sample.getLimitHighVoltage()));
                    row.createCell(4).setCellValue(checkNull(sample.getLimitLowVoltage()));
                    rowCount++;
                }

                //Solution
                rowCount++;
                sheetCon.addMergedRegion(new CellRangeAddress(rowCount,rowCount, 0, 3));
                row = sheetCon.createRow(rowCount);
                row.createCell(0).setCellValue("Solution");
                //Solution Type
                rowCount++;
                row = sheetCon.createRow(rowCount);
                row.createCell(0).setCellValue("name");
                row.createCell(1).setCellValue("Ion");
                row.createCell(2).setCellValue("Voltage(mv)");
                //Sample value
                rowCount++;
                for (Sample sample:samples)
                {
                    List<Solution> solutions=solutionDB.getSampleAll(sample.getID());
                    for(Solution solution:solutions)
                    {
                        row = sheetCon.createRow(rowCount);
                        row.createCell(0).setCellValue(sample.getName());
                        row.createCell(1).setCellValue(sample.getIonType()+solution.getConcentration());
                        row.createCell(2).setCellValue(solution.getVoltage());
                        rowCount++;
                    }
                }
                j++;
            }

            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String checkNull(String s)
    {
        if(s==null)
        {
            return " ";
        }else {
            return s;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            progressL.setVisibility(View.GONE);
            openCloud();
        } else if (requestCode == 3) {
            progressL.setVisibility(View.GONE);
            if (resultCode == -1) {
                Common.showToast(activity, "Success!");
            } else {
                Common.showToast(activity, "Fail!");
            }
            if(mGoogleApiClient!=null)
            {
                mGoogleApiClient.disconnect();
                mGoogleApiClient=null;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        saveFileToDrive();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        if (!firstEnter) {
            Common.showToast(activity, "Fail!");
            progressL.setVisibility(View.GONE);
            return;
        }
        firstEnter = false;
        try {
            result.startResolutionForResult(activity, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private class openCloudy implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(choicePrint.size()<=0)
            {
                Common.showToast(activity,"Please choice file");
                return;
            }
            openCloud();
        }
    }
}
