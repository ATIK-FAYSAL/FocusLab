package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 10/12/2017.
 */

public class OrderSaveInBackground extends AsyncTask<String,Void,Void>
{
    Context context;
    String phone,facebook,history;
    private CheckIfOrderIsExist checkIfOrderIsExist;
    private MakeOrder makeOrder;
    public OrderSaveInBackground(Context context,String phone,String facebook,String history)
    {
        this.context = context;
        this.phone = phone;
        this.facebook = facebook;
        this.history = history;
    }

    @Override
    protected void onPreExecute() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", "Saving your order...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //checkIfOrderIsExist = new CheckIfOrderIsExist(context,phone,facebook,history);
                //checkIfOrderIsExist.getAllOrderFromFirebase();
            }
        });
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
        try
        {
            Firebase firebase  = new Firebase("https://focus-lab.firebaseio.com/order");
            String path = firebase.push().getKey();
            firebase.child(path).updateChildren(orderInformation);

        }catch (Exception e)
        {
            Toast.makeText(context,"Error : "+e.toString(),Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context,"Thank You, Your order is saved now",Toast.LENGTH_SHORT).show();
    }

}
