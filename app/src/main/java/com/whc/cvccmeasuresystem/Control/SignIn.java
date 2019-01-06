package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.UserDB;
import com.whc.cvccmeasuresystem.Model.User;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;


import static com.whc.cvccmeasuresystem.Common.Common.needSet;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.pageCon;
import static com.whc.cvccmeasuresystem.Common.Common.solution1;
import static com.whc.cvccmeasuresystem.Common.Common.solution2;
import static com.whc.cvccmeasuresystem.Common.Common.solution3;
import static com.whc.cvccmeasuresystem.Common.Common.solution4;
import static com.whc.cvccmeasuresystem.Common.Common.userId;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;
import static com.whc.cvccmeasuresystem.Control.History.HistoryMain.saveFiles;

public class SignIn extends Fragment{
    private View view;
    private Activity activity;
    private AutoCompleteTextView userName;
    private UserDB userDB;
    private BootstrapButton enter;
    private SharedPreferences sharedPreferences;
    public static  boolean signIn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signin_main, container, false);
        activity.setTitle(R.string.app_name);
        userName=view.findViewById(R.id.userName);
        enter=view.findViewById(R.id.enter);
        DataBase dataBase=new DataBase(activity);
        userDB=new UserDB(dataBase);
        solution1=null;
        solution2=null;
        solution3=null;
        solution4=null;
        pageCon=null;
        oldFragment=new ArrayList<>();
        needSet=false;
        saveFiles=null;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<String> users=userDB.getAllUserName();
        ArrayAdapter arrayAdapter =new ArrayAdapter(activity,android.R.layout.simple_spinner_item,users.toArray());
        userName.setAdapter(arrayAdapter);
        enter.setOnClickListener(new enterFunction());
    }

    private class enterFunction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String name=userName.getText().toString();
            if(name==null)
            {
               userName.setError("Please enter user Name");
               return;
            }
            name=name.trim();
            if(name.length()<=0)
            {
                userName.setError("Please enter user Name");
                return;
            }
            String oldName=userDB.findUserName(name);
            if(oldName==null)
            {
                userDB.insert(new User(name));
            }
            signIn=true;
            User user=userDB.findUserByName(name);
            sharedPreferences.edit().putInt(userId, user.getId()).apply();
            Common.switchFragment(new ChoiceFunction(),getFragmentManager());
        }
    }
}
