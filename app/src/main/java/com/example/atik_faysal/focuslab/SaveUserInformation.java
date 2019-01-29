package com.example.atik_faysal.focuslab;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 10/11/2017.
 */

public class SaveUserInformation extends AsyncTask<String,Void,Void>
{
    Context context;


    public SaveUserInformation(Context context)
    {
        this.context = context;
        Firebase.setAndroidContext(context);
    }


    @Override
    protected Void doInBackground(String... params) {
      try {
          Map<String,Object> orderInformation = new HashMap<>();
          orderInformation.put("name",params[0]);
          orderInformation.put("phone",params[1]);
          orderInformation.put("email",params[2]);
          orderInformation.put("password",params[3]);
          orderInformation.put("address",params[4]);
          Firebase firebase  = new Firebase("https://focus-lab.firebaseio.com/user");
          String path = firebase.push().getKey();
          firebase.child(path).updateChildren(orderInformation);
      }catch (Exception e)
      {
          e.printStackTrace();
      }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context,"Now you can login with phone number and password",Toast.LENGTH_LONG).show();
    }
}
