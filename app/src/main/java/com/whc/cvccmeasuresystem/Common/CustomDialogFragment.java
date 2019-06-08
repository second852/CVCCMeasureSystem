package com.whc.cvccmeasuresystem.Common;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.TextView;


import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.R;


import java.util.List;


public class CustomDialogFragment implements  DialogInterface.OnCancelListener, View.OnClickListener{

    private Context mContext;
    List<String> name;
    private Dialog mDialog;
    private ListView list;


    public CustomDialogFragment(Context context){
        this.mContext = context;
    }

    public CustomDialogFragment Channel(List<String> name){
        this.name = name;
        return this;
    }

    public CustomDialogFragment show(){
        mDialog = new Dialog(mContext, R.style.MyDialog);
        mDialog.setContentView(R.layout.diaglog);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);

        // 點邊取消
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        mDialog.setOnCancelListener(this);
        list=mDialog.findViewById(R.id.list);
        list.setAdapter(new NameAdapter(mContext,name));
        mDialog.show();

        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mDialog.dismiss();
        HumidityMain.showAlert=true;
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
        HumidityMain.showAlert=true;
    }


    public class NameAdapter extends BaseAdapter {

        private Context context;
        private List<String> name;

        public NameAdapter(Context context, List<String> name) {
            this.context = context;
            this.name = name;
        }

        @Override
        public int getCount() {
            return name.size();
        }

        @Override
        public Object getItem(int i) {
            return name.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.data_adapter_item4, parent, false);
            }
            TextView name=itemView.findViewById(R.id.name);
            name.setText(this.name.get(position));
            return itemView;
        }
    }

}
