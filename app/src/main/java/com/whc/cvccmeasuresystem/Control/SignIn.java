package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
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

import java.util.List;

public class SignIn extends Fragment{
    private View view;
    private Activity activity;
    private AutoCompleteTextView userName;
    private UserDB userDB;
    private BootstrapButton enter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signin_main, container, false);
        activity.setTitle(R.string.app_name);
        userName=view.findViewById(R.id.userName);
        enter=view.findViewById(R.id.enter);


        DataBase dataBase=new DataBase(activity);
        userDB=new UserDB(dataBase.getReadableDatabase());
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
            String oldName=userDB.findlUserName(name);
            if(oldName==null)
            {
                userDB.insert(new User(name));
            }
            Common.switchFragment(new ChoiceFunction(),getFragmentManager().beginTransaction());
        }
    }
}
