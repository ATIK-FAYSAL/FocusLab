package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 11/2/2017.
 */

public class SetNewPassword extends AppCompatActivity
{
    private EditText eNewPassword,eConPassword;
    private Button bSave;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_new_password);
        eNewPassword = (EditText)findViewById(R.id.eNewPass);
        eConPassword = (EditText)findViewById(R.id.eConfPass);
        bSave = (Button)findViewById(R.id.bSave);
        onButtonClick();
    }

    private void onButtonClick()
    {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new CheckInternet(SetNewPassword.this).check_internet())
                {
                    if(eNewPassword.getText().toString().equals(eConPassword.getText().toString()))
                    {
                        if(eNewPassword.getText().toString().length()>=6&&eNewPassword.getText().toString().length()<=25)setNewPassword(eNewPassword.getText().toString());
                        else Toast.makeText(SetNewPassword.this,"Password must be in 6 to 25 characters",Toast.LENGTH_SHORT).show();
                    }else Toast.makeText(SetNewPassword.this,"Password and Confirm Password does not match.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setNewPassword(final String password)
    {
        String phone = getIntent().getExtras().getString("phone");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.orderByChild("phone").equalTo(phone).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                changePassword(key,password);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changePassword(final String key,final String password)
    {
        final Firebase firebase = new Firebase("https://focus-lab.firebaseio.com/user");
        final ProgressDialog ringProgressDialog = ProgressDialog.show(SetNewPassword.this, "Please wait ...", "Processing...", true);
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
        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                firebase.child(key).child("password").setValue(password);
                Toast.makeText(SetNewPassword.this,"Password changed.Now you can log in with new Password",Toast.LENGTH_SHORT).show();
                Close_activity(SetNewPassword.this,LogIn_Activity.class);
            }
        });
    }

    private static void Close_activity(Activity context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

}
