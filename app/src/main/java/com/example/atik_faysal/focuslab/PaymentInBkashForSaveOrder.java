package com.example.atik_faysal.focuslab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.client.Firebase;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static android.content.ContentValues.TAG;

/**
 * Created by user on 10/26/2017.
 */

public class PaymentInBkashForSaveOrder extends AppCompatActivity
{
    private TextView txt_ammount;
    private EditText e_trxId;
    private static String ammount;
    private static String PhotoId;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_bkash_for_save_order);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInformation();
        setToolbar();
    }


    private void setToolbar()
    {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void getSaveInformationForPayment(String photoId,String ammounT)
    {
        PhotoId = photoId;
        ammount = ammounT;
    }

    private void setInformation()
    {
        txt_ammount = (TextView)findViewById(R.id.txt_ammount);
        e_trxId = (EditText)findViewById(R.id.e_trxId);
        if(ammount!=null) txt_ammount.setText(ammount+".00/=");
        else  txt_ammount.setText("null");
    }

    public void onButtonClick(View view)
    {
        if(view.getId()==R.id.b_payment)
        {
            if(new CheckInternet(this).check_internet())
            {
                if(!e_trxId.getText().toString().isEmpty())
                {
                    getRootChild();
                    setConfirmationMessage();
                }else e_trxId.setError("Please Insert TRXID");
            }else Toast.makeText(this,"Please check your Internet connection!",Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.b_cancel)finish();
    }

    private void getRootChild()
    {
        final Firebase firebase = new Firebase("https://focus-lab.firebaseio.com/order");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("order");
        databaseReference.orderByChild("photoId").equalTo(PhotoId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                firebase.child(key).child("trxId").setValue(e_trxId.getText().toString());
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
    }



    public void setConfirmationMessage()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ProgressDialog ringProgressDialog = ProgressDialog.show(PaymentInBkashForSaveOrder.this, "Please wait", "Processing...", true);
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

                View view = LayoutInflater.from(PaymentInBkashForSaveOrder.this).inflate(R.layout.delivary_message_window,null);
                builder.setTitle("Congratulation");
                builder.setView(view);
                TextView txtDate;
                txtDate = (TextView)view.findViewById(R.id.txtDate);
                txtDate.setText(delivaryDate());
                Button button = (Button)view.findViewById(R.id.B_Done);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Close_activity(PaymentInBkashForSaveOrder.this,HomePage.class);
                        setAlertDialog();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private static void Close_activity(Activity context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }


    private String delivaryDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    private void setAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention");
        builder.setMessage("Do you want to Exit? or Give another order?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LogIn_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag",true);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Close_activity(PaymentInBkashForSaveOrder.this,HomePage.class);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
