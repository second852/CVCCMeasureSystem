package com.whc.cvccmeasuresystem.Control.History;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;


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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.MainActivity;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Drobox.DbxRequestConfigFactory;
import com.whc.cvccmeasuresystem.Drobox.DropboxClientFactory;
import com.whc.cvccmeasuresystem.Drobox.UploadFileTask;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.whc.cvccmeasuresystem.Common.Common.needData;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;


public class HistoryMain extends Fragment {

    private static final String TAG ="HistoryMain" ;
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

    public static List<SaveFile> saveFiles;
    private static List<SaveFile> choicePrint;
    public static SaveFile showFileDate;
    private GoogleApiClient mGoogleApiClient;
    private boolean firstEnter;
    private BootstrapButton export;
    private UserDB userDB;
    private static PageCon pageCon;
    private LinearLayout fileChoice;
    private ImageView local;
    private ImageView dropBox;
    private ImageView cancel;
    private static final String dropBoxPath="/CVCC";

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    fileChoice.setVisibility(View.GONE);
                    progressL.setVisibility(View.VISIBLE);
                    Common.showToast(activity,"Uploading now!");
                    break;
                case 1:
                    String fileInfo=String.valueOf(msg.obj);
                    progressL.setVisibility(View.GONE);
                    Common.showToast(activity,"Uploading is complete! ");
                    Common.showToast(activity," File path is "+fileInfo);
                    break;
            }
        }
    };

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_main, container, false);
        findViewById();
        setOnClick();
        if (saveFiles == null) {
            HistoryMain.saveFiles = saveFileDB.getAll();
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

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.dropboxOpen) {
            try {
                dropBoxAction();
            }catch (Exception e){
                Log.e(TAG, "onResume: ",e);
            }
        }
    }

    //---dropBox 上傳
    private void dropBoxAction() throws Exception{
        MainActivity.dropboxOpen=false;
        try {
            DropboxClientFactory.per(HistoryMain.this.activity);
            DropboxClientFactory.getClient();
        }catch (Exception e){
            Log.e(TAG, "dropBoxAction: ",e);
            Common.showToast(HistoryMain.this.activity,"DropBox connect fail !");
            return;
        }
        Message message=handler.obtainMessage(0);
        message.sendToTarget();
        final String fileName=getFileName();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        DropBoxFileOut dropBoxFileOut=new DropBoxFileOut();
        Future<byte[]> dataFuture=executor.submit(dropBoxFileOut);
        while (!dataFuture.isDone()){}
        byte[] dataResult=dataFuture.get();
        if(dataResult==null){
            Common.showToast(activity,"UpLoad file fail !");
            return;
        }
        new UploadFileTask(activity, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                Message message=handler.obtainMessage(1);
                message.obj=dropBoxPath+"/"+fileName;
                message.sendToTarget();
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to upload file.", e);
                Toast.makeText(activity,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        },dataResult).execute(dropBoxPath, fileName);
    }

    public class DropBoxFileOut implements Callable<byte[]> {
        @Override
        public byte[] call() throws Exception {
            Thread.sleep(5);
            byte[]  result=outputExcel();
            return result;
        }
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
        export.setOnClickListener(new openFunction());
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
        fileChoice=view.findViewById(R.id.fileChoice);
        dropBox=view.findViewById(R.id.dropbox);
        local=view.findViewById(R.id.local);
        cancel=view.findViewById(R.id.cancelF);
        local.setOnClickListener(new LocalFunction());
        cancel.setOnClickListener(new CancelFunction());
        dropBox.setOnClickListener(new DropBoxFunction());
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
                User user = userDB.findUserById(saveFile.getUserId());
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



    private byte[] outputExcel() throws IOException {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            int rowCount;
            SampleDB sampleDB=new SampleDB(new DataBase(activity));
            SolutionDB solutionDB=new SolutionDB(new DataBase(activity));
            HSSFWorkbook workbook = new HSSFWorkbook();

            for(SaveFile saveFile:choicePrint)
            {
                User user=userDB.findUserById(saveFile.getUserId());
                Sheet sheetCon = workbook.createSheet(Common.timeToSheet.format(new Date(saveFile.getStatTime().getTime())));
                sheetCon.setColumnWidth(0, 10 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(1, 10 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(2, 20 * 256);// 調整欄位寬度
                sheetCon.setColumnWidth(3, 20* 256);// 調整欄位寬度
                sheetCon.setColumnWidth(4, 20* 256);// 調整欄位寬度
//                sheetCon.setColumnWidth(5, 20* 256);// 調整欄位寬度
                sheetCon.setColumnWidth(15, 25* 256);// 調整欄位寬度

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
                rowCount=0;
                sheetCon.addMergedRegion(new CellRangeAddress(rowCount,rowCount, 6, 15));
                row = sheetCon.getRow(rowCount);
                row.createCell(6).setCellValue("Solution");
                //Solution Type
                rowCount++;
                row = sheetCon.getRow(rowCount);
                row.createCell(6).setCellValue("Time");

                int i=6;
                for(Sample sample:samples)
                {
                    row.createCell(++i).setCellValue(sample.getIonType());
                    row.createCell(++i).setCellValue(sample.getName()+"(mv)");
                }
                row.createCell(++i).setCellValue("DateTime");

                //Sample value
                rowCount++;
                boolean firstLoop=true;
                int j=0,loop=0;
                for (Sample sample:samples)
                {
                    List<Solution> solutions=solutionDB.getSampleAll(sample.getID());
                    int k=0;
                    for(Solution solution:solutions)
                    {

                        row = sheetCon.getRow(rowCount+k);
                        if(row==null)
                        {
                            row = sheetCon.createRow(rowCount+k);
                        }
                        if(firstLoop)
                        {
                            row.createCell(6).setCellValue(j++);
                            row.createCell(15).setCellValue(Common.timeToString.format(new Date(solution.getTime().getTime())));
                        }else{
                            row = sheetCon.getRow(rowCount+k);
                        }
                        row.createCell(7+loop).setCellValue(solution.getConcentration());
                        row.createCell(8+loop).setCellValue(solution.getVoltage());
                        k++;
                    }
                    firstLoop=false;
                    loop=loop+2;
                }
            }

            workbook.write(bos);
            workbook.close();
            bos.flush();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
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

    }


    private class openFunction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(choicePrint.size()<=0)
            {
                Common.showToast(activity,"Please choice file");
                return;
            }
            fileChoice.setVisibility(View.VISIBLE);
        }
    }

    private class CancelFunction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fileChoice.setVisibility(View.GONE);
        }
    }

    private class LocalFunction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Message message=handler.obtainMessage(0);
            message.sendToTarget();
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, getFileName());
            LocalFileOut fileOut=new LocalFileOut(file);
            new Thread(fileOut).start();
        }
    }

    public class LocalFileOut implements Runnable{
        private  File file;
        public LocalFileOut(File file) {
            this.file = file;
        }
        @Override
        public void run() {
            try(OutputStream outputStream = new FileOutputStream(file)){
                byte[] data=outputExcel();
                outputStream.write(data);
            }catch (Exception e){
                Log.e(TAG, "run: ",e);
            }finally {
                Message message=handler.obtainMessage(1);
                String[] path= file.getAbsolutePath().split("/");
                int length=path.length-1;
                message.obj="/"+path[length-1]+"/"+path[length];
                message.sendToTarget();
            }
        }
    }

    private class DropBoxFunction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssId = wifiInfo.getSSID();
            if (ssId == null || ssId.indexOf("BCS_Device") != -1) {
                Common.showToast(activity, "Please change WiFi!");
                return;
            }
            MainActivity.dropboxOpen=true;
            Auth.startOAuth2PKCE(activity, getString(R.string.DropboxKey), DbxRequestConfigFactory.getRequestConfig(), DbxRequestConfigFactory.scope);
        }
    }

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
    private String getFileName(){
        String fileName="CVCC"+timeFormat.format(new Date())+".xls";
        return fileName;
    }


}
