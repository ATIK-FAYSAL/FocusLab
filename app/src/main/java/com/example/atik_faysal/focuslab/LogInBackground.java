package com.example.atik_faysal.focuslab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by user on 10/11/2017.
 */

public class LogInBackground extends AsyncTask<String,Void,Void>
{
    private Context context;
    private MakeOrder make;
    private AllSharedPreference allSharedPreference;
    public LogInBackground(Context context)
    {
        this.context = context;
        Firebase.setAndroidContext(context);
        allSharedPreference = new AllSharedPreference(context);
    }

    @Override
    protected void onPreExecute() {
        make = new MakeOrder();
        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait", "Authenticating...", true);
        ringProgressDialog.setCancelable(true);
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    protected Void doInBackground(String... params) {

        final String phoneNumber,password;
        phoneNumber = params[0];
        password = params[1];
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        final com.google.firebase.database.Query query = databaseReference.orderByChild("phone").equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                   query.addChildEventListener(new ChildEventListener() {
                       @Override
                       public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                           try {
                               String get_password = dataSnapshot.child("password").getValue(String.class);
                               if(get_password!=null&&get_password.equals(password))
                               {
                                   Intent page = new Intent(context,HomePage.class);
                                   allSharedPreference.savePreferenceData("session",true);
                                   allSharedPreference.savePrefPhoneNumber("session",phoneNumber);
                                   context.startActivity(page);
                               }else Toast.makeText(context,"Incorrect Password.\nPlease try again!",Toast.LENGTH_SHORT).show();
                           }catch (NullPointerException e)
                           {
                               e.printStackTrace();
                           }
                       }

                       @Override
                       public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                       @Override
                       public void onChildRemoved(DataSnapshot dataSnapshot) {}

                       @Override
                       public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                       @Override
                       public void onCancelled(DatabaseError databaseError) {}
                   });
                }else Toast.makeText(context,"Phone number doesn't exist.\nPlease create account first then try again",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }
}
