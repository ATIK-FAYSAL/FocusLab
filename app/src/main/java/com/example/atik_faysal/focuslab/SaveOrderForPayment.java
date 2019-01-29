package com.example.atik_faysal.focuslab;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 10/25/2017.
 */

public class SaveOrderForPayment extends AsyncTask<String,Void,Void>
{
    Context context;
    public SaveOrderForPayment(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        Map<String,Object> orderInformation = new HashMap<>();
        orderInformation.put("name",params[0]);
        orderInformation.put("phone",params[1]);
        orderInformation.put("email",params[2]);
        orderInformation.put("photoId",params[3]);
        orderInformation.put("photoSize",params[4]);
        orderInformation.put("totalCopy",params[5]);
        orderInformation.put("date",params[7]);
        orderInformation.put("trxId",params[8]);
        orderInformation.put("totalAmount",params[6]);
        orderInformation.put("delivery","No");
        orderInformation.put("facebookId",params[9]);
        Firebase firebase  = new Firebase("https://focus-lab.firebaseio.com/order");
        String path = firebase.push().getKey();
        firebase.child(path).updateChildren(orderInformation);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Toast.makeText(context,"Thank You.your order is save",Toast.LENGTH_SHORT).show();
    }
}
